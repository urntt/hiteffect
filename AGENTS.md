# AGENTS.md

Instructions for AI coding agents working in this repository. `CLAUDE.md` imports this file, so keep all agent guidance here.

## Project

`hiteffect` is a client-side [Fabric](https://fabricmc.net/) mod for Minecraft: Java Edition that customizes hit-related visual effects: the hurt flash of living entities (its color, and whether armor, held items, worn head items and the first-person arm flash with the body) and the particles shown when the player lands a melee hit.

## Project decisions

These decisions are settled. Do not deviate from them without the user's explicit approval.

### Minecraft and toolchain

- Development started on Minecraft 26.3 with Java 25. `gradle.properties` is the single source of truth for the mod, Minecraft, Fabric Loader, Loom, Fabric API, Mod Menu, OneConfig, and Java versions. `build.gradle`, `fabric.mod.json`, the mixin config, and the CI workflows read them from there; do not restate them elsewhere.
- Follow the latest official Fabric template ([FabricMC/fabric-example-mod](https://github.com/FabricMC/fabric-example-mod), also available from the [template generator](https://fabricmc.net/develop/template/)): the `net.fabricmc.fabric-loom` Gradle plugin, Mojang's official names with no `mappings` dependency, and `implementation` (not `modImplementation`) for dependencies. Do not use Yarn.
- Pin `loom_version` to a release version instead of the template's `-SNAPSHOT`, so builds are reproducible.
- Target only the latest stable (release) Minecraft version. Updates, fixes, and new features are always developed against it. Snapshots, pre-releases, and release candidates are not supported targets.
- Do not maintain older Minecraft versions and do not set up multi-version builds (no per-version branches, no Stonecutter or other preprocessors). When a new stable version is released, port the mod to it and drop the previous one.

### Identity

| Item | Value |
| --- | --- |
| Mod ID | `hiteffect` |
| Display name | `hiteffect` |
| Maven group | `com.urntt` |
| Base package | `com.urntt.hiteffect` |
| Version format | `<SemVer>+<Minecraft version>`, for example `1.0.0+26.3` |
| License | MIT |

The mod version itself follows [Semantic Versioning](https://semver.org/); the `+<Minecraft version>` suffix is build metadata naming the Minecraft version the build targets. Version numbers start at `1.0.0`.

### Distribution

- Releases are published only as GitHub Releases. Do not publish to Modrinth, CurseForge, or any other mod platform, and do not add publishing tooling for them.
- The mod is purely cosmetic: it sends no packets and changes no gameplay, so `README.md` needs no multiplayer or anti-cheat warning.

### Scope and behavior

- Client-only: `fabric.mod.json` declares `"environment": "client"`. There is no server-side component and no networking.
- Scope is hit-related visual effects. The features are:
  - Hurt flash on equipment, with a toggle per part (`FlashPart`): armor, elytra, horse armor, wolf armor (including the cracks of damaged armor), saddles, and other equipment (llama carpets, happy ghast harnesses, nautilus armor). Vanilla armor render pipelines define `NO_OVERLAY`, so flashing equipment is drawn with overlay-capable copies of those pipelines, and only while the entity shows the hurt overlay.
  - Hurt flash on items, with a toggle per part: held items, items worn on the head (carved pumpkins, skulls, blocks), villager and wandering trader items, fox, panda and dolphin items, and enderman blocks.
  - Hurt flash on the first-person arm (including the sleeve) while the local player is hurt, with its own toggle. Items held in first person do not flash.
  - Hurt flash color: an RGB color and a strength (0-100 %, vanilla is 30 %), or a rainbow that cycles through hues. It is applied by rewriting the hurt rows of the global `OverlayTexture`, so it affects every entity.
  - Sharpness (enchanted hit) particles for every melee attack of the local player, whatever the held item.
  - Critical hit particles for every melee attack of the local player.
- Hit particles appear instantly on the client when the local player attacks a living entity, without waiting for the server; the mod never relies on server confirmation. They follow only the local player's own melee attacks on the attacked entity; entities hit by a sweep are not included. Vanilla shows these particles when the server's animation packet arrives; when that packet repeats a particle the mod already showed for an attack on the same entity, it is skipped, so no hit shows it twice.
- The mod never sends packets.
- Configuration mode, shown as the first button of the configuration screen:
  - Disabled: every feature is off and the hurt color is vanilla.
  - Custom (default): the mod's own settings apply.
  - OneConfig: the settings registered with OneConfig apply, and the mod's own settings are shown greyed out with the values OneConfig currently applies. This mode is only offered when OneConfig is installed. If the configuration file selects it but OneConfig is missing, the mod behaves as in Custom mode and keeps the saved mode until the player changes it.
- Defaults in Custom mode: every flash toggle and the sharpness particles are on, crit particles on every hit are off (they would hide real critical hits), the hurt color is vanilla red (`#FF0000`) at 30 % strength, and the rainbow is off.
- A key binding opens the configuration screen. It is unbound by default. Mod Menu opens the same screen.
- Settings are saved to the configuration file immediately when changed.

### Localization

- All user-facing text, including key binding names, the key binding category, the configuration screen, and the OneConfig option titles, uses translation keys. Never hard-code display strings.
- Provide translations for `en_us` and `zh_cn`, and keep both complete whenever a translation key is added or changed.

### Dependencies

- Required: Fabric Loader and Fabric API.
- Optional: Mod Menu, declared under `suggests` in `fabric.mod.json`. The mod must load and work normally without it, so Mod Menu classes may only be referenced from the Mod Menu entrypoint.
- Optional: [OneConfig](https://modrinth.com/mod/oneconfig), approved by the user and declared under `suggests`. The build only compiles against its API (`clientCompileOnly`); it is never bundled. OneConfig classes may only be referenced from the `com.urntt.hiteffect.compat.oneconfig` package, which OneConfig reaches through its `oneconfig:config` entrypoint, so the mod loads and works normally without it.
- Configuration is hand-written without a config library: a JSON file in the Fabric config directory, serialized with Gson (bundled with Minecraft). The configuration screen uses vanilla widgets.
- Do not add other dependencies without the user's explicit approval.

### Implementation

- Language: Java only.
- Source sets: `src/main` holds only `fabric.mod.json`. All code and client resources live in `src/client`, and the client game tests live in `src/gametest`.
- Mixins: prefer the MixinExtras injectors bundled with Fabric Loader (for example `@ModifyExpressionValue` and `@WrapOperation`) over `@Redirect` and `@Overwrite`, to stay compatible with other mods and keep porting work small. Every mixin reads the effective settings, so Disabled mode keeps vanilla behavior.

### Testing

- The client game tests in `src/gametest` start Minecraft and check the configuration modes and screen, the open-configuration key, the hurt overlay color, that armor, held items, head items and the first-person arm each flash under their own toggle (by counting pixels in screenshots), and that the hit particles appear right after the attack, also on a target the server does not hurt. Keep them passing and extend them when behavior changes.
- They run twice: without OneConfig, and with `-PoneConfigRuntime=true`, which loads OneConfig and its dependencies (Fabric Language Kotlin, Compose Multiplatform) from Modrinth's Maven into the development client. Their Modrinth version ids live in `gradle.properties` next to `oneconfig_version`; update them together. The run with OneConfig checks the OneConfig mode and skips the in-world effect checks, because OneConfig's notifications cover parts of the screenshots.
- OneConfig stores its settings in the same config directory as the mod's own `hiteffect.json` and watches its files, so its config file must keep a different name (`hiteffect-oneconfig.json`).
- After porting to a new Minecraft version, run the client game tests. A successful build does not prove that the mixins still have the intended effect.
- `README.md` describes how to run them, including on a headless machine.

### CI, releases, and changelog

- GitHub Actions (`.github/workflows/build.yml`) builds the project and runs the client game tests, without and with OneConfig, on every push and pull request.
- Maintain `CHANGELOG.md` following [Keep a Changelog](https://keepachangelog.com/). Record every user-visible change under `Unreleased` in the same change that introduces it.
- `.github/workflows/release.yml` builds the mod and publishes a GitHub Release for the project version, with the jar attached and the matching `CHANGELOG.md` section as release notes. It runs when a tag `v<version>` (for example `v1.0.0+26.3`) is pushed, or when started manually on a branch, in which case it creates that tag on the branch's latest commit. It fails if a pushed tag does not match the project version, if the changelog has no section for the version, or if the release already exists.
- Release only when the user asks. To release, set `mod_version` in `gradle.properties`, rename `Unreleased` in `CHANGELOG.md` to `[<version>] - <YYYY-MM-DD>` above a new empty `Unreleased` section, commit, and push. Then start the release workflow on `main`. Claude Code cloud sessions cannot push tags, so start the workflow through the GitHub Actions API instead.

## Engineering principles

- Fix root causes, not symptoms. Diagnose the underlying cause before implementing a permanent fix. If an immediate mitigation is necessary, treat it as temporary and follow through with a root-cause fix.
- Prefer configuration-driven design for values that are expected to vary by environment, deployment, or product requirements. Avoid unexplained or duplicated magic values, but do not introduce configuration where a well-named constant is the clearer source of truth.
- Preserve a single source of truth and clear ownership for data, state, configuration, business logic, and authoritative documentation. Avoid duplicating canonical information across multiple locations.
- Do not maintain parallel legacy and replacement implementations without an explicit migration and removal plan.

## Documentation

- Keep documentation aligned with the code. When a code change affects documented behavior, APIs, architecture, configuration, workflows, or usage, update the relevant documentation in the same change.
- Keep each document's responsibility clear. For example, use `README.md` for project overview and usage, and `VISION.md` for product direction, architectural principles, or long-term decisions.
- Always specify a language identifier for fenced code blocks in Markdown.

## Language

- Communicate with the user in Chinese, including explanations, progress updates, and user-facing planning.
- Use English for development artifacts, including source code, comments, docstrings, documentation, READMEs, Git branch names, commit messages, and other deliverables intended to live in the repository.

## Git

- Do not change or override the Git author or committer identity. When an identity must be configured for commits created during the task, use:
  - Name: `urntt`
  - Email: `urntts@gmail.com`
- Do all actions on the user's behalf. Do not rewrite existing commit authorship unless explicitly requested. Do not add `Co-Authored-By` trailers or session links to commit messages or pull request descriptions.
- Develop on `main` and push directly to it. Branches and pull requests are not required.
- Because changes land on `main` without review, make sure `./gradlew build` and the client game tests pass locally before pushing.
- If a branch is used, give it a category-based prefix that reflects the purpose of the change, such as `feat/`, `fix/`, `refactor/`, `docs/`, `test/`, or `chore/`.
- Follow the [Conventional Commits](https://www.conventionalcommits.org/) specification for commit messages.
