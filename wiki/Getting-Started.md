# Getting Started

## Requirements

### Versions and dependencies

| Component | Version |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.219 or later within 21.1.x |
| Java | 21 |
| UltraTerraForged | 1.0.2+1.21.1 |
| Streams Reflowing | 2.15.0 |

Install the add-on alongside both original mods on the client and server.

## Installation

Install UltraTerraForged, Streams Reflowing and this adapter in the `mods` folder of your **client and dedicated server**. Both sides should have matching versions.

## Setup

Use an UltraTerraForged Overworld with **winding rivers or raised water enabled**. At least one is required for the native river paths used by the adapter.

Keep these settings enabled in `config/streamsreflowing.properties`:

```properties
vanillaWaterFlow=true
flowVanillaRivers=true
```

The operator command `/utfstreams status` reports whether the adapter is active. Currents become available as Streams prepares its flow grids.

## Next steps

- [Features & Behavior](Features-and-Behavior.md) — what the adapter does.
- [Configuration & Commands](Configuration-and-Commands.md) — settings and diagnostics.
- [Compatibility & Limitations](Compatibility-and-Limitations.md) — what this add-on does not fix.

[← Wiki Home](Home.md)
