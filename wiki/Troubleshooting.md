# Troubleshooting

## The add-on does not activate

- Verify Minecraft **1.21.1**, compatible NeoForge **21.1.x**, **Java 21** and the exact dependency versions in [Getting Started](Getting-Started.md).
- Install the compatibility add-on alongside both required mods. Dedicated servers and clients need matching JARs.
- Make sure the world type is an **UltraTerraForged Overworld**.
- Run `/utfstreams status` as an operator. The preset must enable winding rivers or raised water.

## River currents are missing

Ensure `vanillaWaterFlow=true` and `flowVanillaRivers=true` in `config/streamsreflowing.properties`. Currents use Streams Reflowing's grid preparation, so newly visited regions can take time. Existing baked fluid blocks are not replaced.

## Water appears to run uphill

The addon skips detected uphill *current segments* by default. It does not lower, reshape, reconnect or repair the underlying water and terrain. See the configuration and limitations guides.

## Report a reproducible issue

Open a [bug report](https://github.com/Mikaaah/UltraTerraForged-Streams-Currents/issues/new?template=bug_report.yml) with:

- Exact versions of all required mods and the Java/NeoForge runtime.
- World seed, preset and whether the affected chunks are new or existing.
- Coordinates **including Y**, expected behavior, observed behavior and screenshots.
- Diagnostic command output and relevant configuration values.
- Logs with private server details, user tokens and personal information removed.

[← Wiki Home](Home.md)
