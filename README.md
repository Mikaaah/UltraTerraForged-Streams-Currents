# UltraTerraForged Streams Currents

[Project status](https://github.com/Mikaaah/Mikaaah/blob/master/STATUS.md) · [Report a bug](https://github.com/Mikaaah/UltraTerraForged-Streams-Currents/issues/new?template=bug_report.yml) · [Suggest a feature](https://github.com/Mikaaah/UltraTerraForged-Streams-Currents/issues/new?template=feature_request.yml) · [Questions](https://github.com/Mikaaah/UltraTerraForged-Streams-Currents/issues/new?template=question.yml) · [CurseForge download](https://www.curseforge.com/minecraft/mc-mods/ultraterraforged-streams-reflowing-compat)

[![Minecraft 1.21.1](https://img.shields.io/badge/Minecraft-1.21.1-5B8731?style=flat-square)](https://github.com/Mikaaah/UltraTerraForged-Streams-Currents)
[![NeoForge](https://img.shields.io/badge/Loader-NeoForge-D97724?style=flat-square)](https://neoforged.net/)
[![Experimental Alpha](https://img.shields.io/badge/Release-Experimental%20Alpha-F59E0B?style=flat-square)](CHANGELOG.md)
[![GPL-3.0](https://img.shields.io/badge/License-GPL--3.0-64748B?style=flat-square)](LICENSE)

Adds Streams Reflowing currents to UltraTerraForged's native rivers, using their paths and local water heights.

📚 **[Browse the documentation wiki](https://github.com/Mikaaah/UltraTerraForged-Streams-Currents/wiki/Home)** · [Installation](https://github.com/Mikaaah/UltraTerraForged-Streams-Currents/wiki/Getting-Started) · [Troubleshooting](https://github.com/Mikaaah/UltraTerraForged-Streams-Currents/wiki/Troubleshooting)

[Download 0.1.0-alpha](downloads/ultraterraforged-streams-currents-0.1.0-alpha-neoforge-1.21.1.jar?raw=true) · [Changelog](CHANGELOG.md) · [Report an issue](https://github.com/Mikaaah/UltraTerraForged-Streams-Currents/issues)

## Requirements

| Component | Version |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.219 or later within 21.1.x |
| Java | 21 |
| UltraTerraForged | 1.0.2+1.21.1 |
| Streams Reflowing | 2.15.0 |

Install the add-on alongside both original mods on the client and server.

## Setup

Use an UltraTerraForged Overworld with **winding rivers or raised water enabled**. At least one is required for the native river paths used by the adapter.

Keep these settings enabled in `config/streamsreflowing.properties`:

```properties
vanillaWaterFlow=true
flowVanillaRivers=true
```

The operator command `/utfstreams status` reports whether the adapter is active. Currents become available as Streams prepares its flow grids.

## Configuration

Settings are in `config/utfstreams-common.toml`:

```toml
enabled = true
skipUphillSegments = true
nativeRiverSpeed = 1.0
maxCurrentDepth = 64
```

Restart after changing settings to rebuild cached grids. Streams' own streams and existing flow layers retain priority where they overlap native rivers.

## Limitations

This is a current adapter. It does not change river terrain, repair elevated water steps or fix tributary placement. Detected uphill segments are skipped by default.

Current coverage uses terrain-generator estimates. It may differ from saved terrain after preset changes or from water modified by players or other mods. Previously generated Streams fluid blocks are not replaced.

## Building

Requires Java 21. Place the following dependency files in `libs/`:

- `ultraterraforged-1.0.2+1.21.1-neoforge.jar`
- `StreamsReflowing-1.21.1-neoforge-2.15.0.jar`

Run `./gradlew build` on Linux/macOS or `gradlew.bat build` on Windows. Output is written to `build/libs/`. Dependency JARs are not included in this repository.


## Contributing

Found a bug or want to improve the project? Read the [contribution guide](CONTRIBUTING.md) for reporting details, testing expectations and pull requests.


## Credits and license

By Towns & Kingdoms, for UltraTerraForged and Streams Reflowing.

[GPL-3.0-only](LICENSE).
