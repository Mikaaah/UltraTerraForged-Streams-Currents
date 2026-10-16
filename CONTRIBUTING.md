# Contributing to UltraTerraForged Streams Currents

This is an **experimental current adapter** for UltraTerraForged and Streams Reflowing on Minecraft 1.21.1 / NeoForge. It does **not** repair terrain or river carving.

## Reporting current issues

Include:

- Exact Minecraft, NeoForge, UltraTerraForged and Streams Reflowing versions.
- World seed, UltraTerraForged preset, and whether raised water or winding rivers is enabled.
- River coordinates (including Y), whether chunks are new or previously generated, and screenshots/video if useful.
- `/utfstreams status` output and relevant `utfstreams-common.toml` and `streamsreflowing.properties` settings.
- Expected flow direction/height versus observed results, plus relevant logs with private details removed.

Distinguish current-direction issues from upstream terrain issues such as uphill river steps, disconnected water, or tributary carving.

## Pull requests

- Preserve existing Streams layers' priority and avoid silently changing terrain blocks.
- Keep the inactive/vanilla profile and other dimensions unaffected.
- Update [README.md](README.md), [CHANGELOG.md](CHANGELOG.md) or [VALIDATION.md](VALIDATION.md) as needed.
- Follow the Java 21 [build instructions](README.md#building). Dependency JARs are not redistributed.
- Explain tests with actual worlds, current grids, networking and clients where relevant; do not report simulator checks as full in-game validation.

Please open an issue to discuss significant changes to river detection or current behavior.
