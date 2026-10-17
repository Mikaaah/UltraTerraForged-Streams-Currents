# UltraTerraForged Optimizer v0.1.0
Separate NeoForge 1.21.1 mod, requires UltraTerraForged 1.0.2. Keeps seed and terrain algorithms identical.
- Enabled: skip two unnecessary lake-warp noise calculations for terrain cells outside river-network bounds.
- Disabled (experimental): incrementally evict landform/cave site cache keys instead of clearing all 4097.
- Disabled: optional counters; /utfoptimizer status and /utfoptimizer resetstats.
- config/utfoptimizer-common.toml controls both optimizations.
Not a replacement for UltraTerraForged. Back up worlds; validate in modpacks with C2ME, Streams Reflowing etc.
No claimed speedup without profiling. Build with NeoForge ModDevGradle. Official upstream jar is a compile dependency and is not packaged in the addon.
