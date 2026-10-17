# Changelog

## 0.2.0-beta — Streams Reflowing 2.15.2

- Compile and test against Streams Reflowing 2.15.2 and UltraTerraForged 1.0.2 for Minecraft 1.21.1.
- Disable the new generic TerraForged course estimate while the native adapter is active, avoiding two competing course calculations.
- Compare final native water heights ahead and behind each route tangent, including confluences. Reverse locally rising stretches; calm basins when both directions rise.
- Give native height-bounded bands priority over contradictory generic currents. Preserve flow at other elevations and outside native river columns.
- Carry zero-speed basin bands through the normal cache and packet codec instead of leaving holes that can fall back to conflicting flow.
- Expand direction regression tests and update integration checks for native priority.


## 0.1.0-alpha

- Added native UltraTerraForged river currents using local water heights and river paths.
- Integrated with Streams Reflowing's flow grids while preserving existing layer priority.
- Added an uphill-segment guard and configurable current speed and depth.
- Added `/utfstreams status`.
