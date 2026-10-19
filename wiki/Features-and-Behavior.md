# Features and behavior

- Adds height-aware Streams currents to UltraTerraForged’s native river paths.
- Corrects final ordinary-water, baked-fluid and boat currents against nearby loaded water.
- Reverses uphill route segments and calms local basins by default.
- Preserves explicit Flow Control edits and never rewrites water or terrain.

## Setup

Use an UltraTerraForged Overworld with **winding rivers or raised water enabled**. At least one is required for the native river paths used by the adapter.

Keep these settings enabled in `config/streamsreflowing.properties`:

```properties
vanillaWaterFlow=true
flowVanillaRivers=true
```

The operator command `/utfstreams status` reports whether the adapter is active. Currents become available as Streams prepares its flow grids.

## Actual-water correction

`correctActualWaterFlow=true` also validates final ordinary/baked fluid and boat currents against loaded water.
`actualWaterScanRadius=16` bounds the connected-water search (4–32 blocks); each query visits at most 384 nodes.
No chunks are forced to load and no water or terrain blocks are rewritten. Nearby lower outlets determine the corrected direction.
Flat areas or outlets beyond the search bounds keep their original direction. Explicit Flow Control edits retain priority.

Stand in an affected stream and run `/utfstreams inspect` for the block position, fluid, cached grid angle and final flow vector.
`/utfstreams status` includes actual-water search/correction counters.

[← Wiki home](https://github.com/Mikaaah/UltraTerraForged-Streams-Currents/wiki/Home)
