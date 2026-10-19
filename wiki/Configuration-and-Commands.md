# Configuration and commands

Settings are in `config/utfstreams-common.toml`:

```toml
enabled = true
skipUphillSegments = true
nativeRiverSpeed = 1.0
maxCurrentDepth = 64
```

Restart after changing settings to rebuild cached grids. The native river layer takes priority inside its predicted height band, correcting conflicting generic currents. Other heights and non-native columns retain their original flow.

## Actual-water correction

`correctActualWaterFlow=true` also validates final ordinary/baked fluid and boat currents against loaded water.
`actualWaterScanRadius=16` bounds the connected-water search (4–32 blocks); each query visits at most 384 nodes.
No chunks are forced to load and no water or terrain blocks are rewritten. Nearby lower outlets determine the corrected direction.
Flat areas or outlets beyond the search bounds keep their original direction. Explicit Flow Control edits retain priority.

Stand in an affected stream and run `/utfstreams inspect` for the block position, fluid, cached grid angle and final flow vector.
`/utfstreams status` includes actual-water search/correction counters.

[← Wiki home](https://github.com/Mikaaah/UltraTerraForged-Streams-Currents/wiki/Home)
