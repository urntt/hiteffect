# hiteffect

A simple client-side Fabric mod for Minecraft: Java Edition that lets you customize hit-related visual effects.

- **Flashing equipment.** In vanilla, only an entity's body flashes when it is hurt. With this mod, its equipment can flash too, each with its own toggle: armor, elytra, horse armor, wolf armor (with its cracks), saddles, and other equipment (llama carpets, happy ghast harnesses, nautilus armor).
- **Flashing items.** Items held or carried by an entity can flash with it, each with its own toggle: held items, items worn on the head (carved pumpkins, skulls, blocks), and the items of villagers, foxes, pandas and dolphins and the blocks of endermen.
- **First-person arm.** Your arm can flash in first person while you are hurt.
- **Hurt color.** Pick the flash color (hex code, RGB sliders, or presets) and its strength, or let it cycle through a rainbow.
- **Sharpness particles on every hit.** Your melee attacks show enchanted hit particles whatever you hold.
- **Crit particles on every hit.** Your melee attacks can also show critical hit particles (off by default).

Hit particles appear the moment you attack a mob or player, without waiting for the server. Only the entity you attack gets them; entities caught by a sweep do not, as in vanilla. When the server also sends a particle for that attack, for example with a Sharpness sword, it is not shown twice.

The mod is purely client-side: it only changes what your client draws, sends nothing to servers, and changes no gameplay.

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) and [Fabric API](https://modrinth.com/mod/fabric-api).
2. Download the jar from this repository's [Releases](https://github.com/urntt/hiteffect/releases) page. Each release supports a single Minecraft version, shown after the `+` in its version number. For example, `1.0.0+26.3` is for Minecraft 26.3.
3. Put the jar into your `.minecraft/mods` folder.

Optional:

- [Mod Menu](https://modrinth.com/mod/modmenu) opens the settings screen from the mod list.
- [OneConfig](https://modrinth.com/mod/oneconfig) can manage the settings instead (see below).

## Usage

Open the settings screen from Mod Menu, or bind the **Open hiteffect Settings** key in **Options → Controls → Key Binds**. It is unbound by default.

The button at the top of the screen selects the mode:

| Mode | Effect |
| --- | --- |
| Disabled | Every effect is off, and everything looks vanilla. |
| Custom (default) | The settings on this screen apply. |
| OneConfig | The settings registered with OneConfig apply, and are changed in OneConfig. This screen shows them greyed out. Only offered when OneConfig is installed. |

If OneConfig mode is selected but OneConfig is later removed, the custom settings apply until you pick another mode.

In custom mode, every flash toggle and the sharpness particles are on by default, the crit particles are off, and the hurt color is vanilla red at vanilla strength (30 %). The first preset color restores vanilla red. Settings are saved to `config/hiteffect.json` as soon as you change them.

## Development

Building requires the JDK version set by `java_version` in `gradle.properties`.

Build the mod:

```bash
./gradlew build
```

The jar is written to `build/libs/`.

Run the client game tests, which start Minecraft and check the settings screen and modes, the hurt color, which parts flash (by counting pixels in screenshots), and the hit particles:

```bash
./gradlew runClientGameTest
```

The game tests need a display. On a headless Linux machine, run them under Xvfb. Xvfb offers no sRGB-capable OpenGL visuals, so install Mesa's Vulkan driver (`mesa-vulkan-drivers` on Ubuntu) for the game to fall back to:

```bash
xvfb-run -a -s "-screen 0 1920x1080x24" ./gradlew runClientGameTest
```

Screenshots taken by the tests are saved to `build/run/clientGameTest/screenshots/`.

By default, the development client and the tests run without OneConfig. Add `-PoneConfigRuntime=true` to run them with OneConfig installed, which offers the OneConfig mode. In that run, the tests check the OneConfig mode and skip the in-world effect checks, which do not depend on OneConfig:

```bash
./gradlew runClientGameTest -PoneConfigRuntime=true
```

## License

[MIT](LICENSE)
