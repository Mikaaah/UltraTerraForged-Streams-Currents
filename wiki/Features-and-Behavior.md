# Features & Behavior

## Native river currents

This add-on registers **height-aware current layers** for UltraTerraForged's native river paths through Streams Reflowing. The current direction follows the generator's river routing and local water heights, rather than assuming all natural water sits at sea level.

Existing Streams Reflowing layers **retain priority at overlaps**. The add-on is active only in supported UltraTerraForged Overworld profiles and leaves the original mods installed and operational.

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

It does not rewrite water blocks or fix uphill terrain steps, disconnected riverbeds, waterfalls, tributaries or terrain carving. It skips detected uphill current sections by default.

For exact boundaries, read [Compatibility & Limitations](Compatibility-and-Limitations.md).

[← Wiki Home](Home.md)
