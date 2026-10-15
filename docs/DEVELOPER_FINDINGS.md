# Findings from the supplied 1.21.1 JARs

Inspected UltraTerraForged **1.0.2+1.21.1** and Streams Reflowing **2.15.0**. These findings apply to those exact binaries.

## Three distinct issues

1. **Bridge detection and API:** Streams' `RTFBridge` recognizes ReTerraForged and FreeTerraForged namespaces, not UltraTerraForged. Its reflection expects different package paths, a different cell field name and an optional native flow-field interface. Changing a mod ID alone is insufficient.
2. **Current height:** Streams' generic natural-river fallback in `StreamFlowGrid.build` uses a sea-level band when native stream column heights are absent. UTF's raised rivers need per-column heights rather than a wider global band.
3. **Hydrology:** UTF's `WaterLevels.step` can raise a river's water level when the gorge-depth constraint requires it. Supplying a direction cannot make a rising river physically continuous.

Streams already provides `StreamFlowGrid.registerLayerProducer`: each extra layer has direction, speed, lower Y and upper Y for 256 columns, and follows its existing grid pipeline.

## Included experiment

The adapter supplies horizontal currents and Y ranges, keeps existing Streams layers first, and skips locally detected rises. It does not solve watershed geometry or stream outlet placement.

An observer activates only during this add-on's queries, using a thread-local query and the queried cell identity. It records the carver when `RiverPopulator.tag` actually writes the river terrain tag. `RiverRoutes.channelInWorld` provides a native routed/warped tangent. `Cell.waterLevel`, `Cell.height` and `Levels.worldHeight` supply the current band; a zero water-level override uses the normal native sea level.

No block writes, water-height changes or route edits are performed. Generator estimates can still disagree with saved blocks, so this is not a guaranteed repair of existing worlds.

## Longer-term upstream work

- Publish the final native river tangent, surface Y and bed Y directly while UTF computes cells, with suitable copy/reset and chunk synchronization. This avoids repeating terrain queries for current grids.
- Let Streams consume these height-bounded currents and native elevations for its outlet planner.
- Resolve gorge-depth and monotonic-water-level constraints across whole river networks, including lake outlets and tributary junctions. Changing current direction cannot repair a rising water step.
- Validate waterfall faces, lakes, confluences, renderer behavior and previously saved chunks separately.

## Diagnostic preset test

In a throwaway new world, use `rivers.raisedWater=false` and retain `rivers.winding=true` to isolate the changing-height feature while keeping native route frames. This is a diagnostic test, not proof of full compatibility. Do not change an established world's preset in place for this test.
