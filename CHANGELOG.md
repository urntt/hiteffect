# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html). Versions carry the targeted Minecraft version as build metadata, for example `1.0.0+26.3`.

## [Unreleased]

## [1.1.0+26.3] - 2026-11-27

### Changed

- New installations start in OneConfig mode. Without OneConfig installed, it applies the mod's own settings as before, so OneConfig takes over as soon as it is installed. Existing configuration files keep their mode.

## [1.0.0+26.3] - 2026-11-19

### Added

- Equipment flashes together with hurt entities, with a toggle each for armor, elytra, horse armor, wolf armor, saddles and other equipment.
- Items flash together with hurt entities, with a toggle each for held items, items worn on the head, and the items of villagers, foxes, pandas, dolphins and endermen.
- The first-person arm flashes while the player is hurt.
- Configurable hurt flash color and strength, with a hex input, RGB sliders, presets and a rainbow mode.
- Sharpness particles the moment the player attacks, whatever the held item.
- Optional crit particles the moment the player attacks.
- Settings screen with a mode button (disabled, custom, OneConfig), opened from Mod Menu or an unbound key binding.
- Optional OneConfig integration that manages the settings in OneConfig mode.
- Mod icon.
