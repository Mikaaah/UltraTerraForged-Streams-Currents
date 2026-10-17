# Features & Behavior

## Native river currents

This add-on registers **height-aware current layers** for UltraTerraForged's native river paths through Streams Reflowing. The current direction follows the generator's river routing and local water heights, rather than assuming all natural water sits at sea level.

Native river bands take priority over conflicting currents at the same elevation. Other elevations and non-native columns keep their original flow. The add-on is active only in supported UltraTerraForged Overworld profiles and leaves the original mods installed and operational.

## Required world settings

## Setup

Use an UltraTerraForged Overworld with **winding rivers or raised water enabled**. At least one is required for the native river paths used by the adapter.

Keep these settings enabled in `config/streamsreflowing.properties`:

```properties
vanillaWaterFlow=true
flowVanillaRivers=true
```

The operator command `/utfstreams status` reports whether the adapter is active. Currents become available as Streams prepares its flow grids.

## What it does not do

It does not rewrite water blocks or fix uphill terrain steps, disconnected riverbeds, waterfalls, tributaries or terrain carving. It compares final water heights in both directions, reverses rising stretches, and calms local basins by default.

For exact boundaries, read [Compatibility & Limitations](Compatibility-and-Limitations.md).

[← Wiki Home](Home.md)
