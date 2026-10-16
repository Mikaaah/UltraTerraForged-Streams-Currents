# Configuration & Commands

## Configuration

Settings are in `config/utfstreams-common.toml`:

```toml
enabled = true
skipUphillSegments = true
nativeRiverSpeed = 1.0
maxCurrentDepth = 64
```

Restart after changing settings to rebuild cached grids. Streams' own streams and existing flow layers retain priority where they overlap native rivers.

## When changes take effect

Restart the game or server after adjusting world-generation or flow settings. For current grids, allow Streams Reflowing to prepare newly visited areas before judging observed behavior.

## Suggested checks

1. Confirm at least one of **winding rivers** or **raised water** is enabled in the UltraTerraForged preset.
2. Keep `vanillaWaterFlow=true` and `flowVanillaRivers=true` in Streams Reflowing.
3. Run `/utfstreams status` as an operator.
4. Verify current behavior after grid preparation near a native river.

[← Wiki Home](Home.md)
