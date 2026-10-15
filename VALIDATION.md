# Validation — 0.1.0-alpha

## Build

Built with JDK 21.0.12.1, Gradle 9.2.1, ModDevGradle 2.0.148 and NeoForge 21.1.219. The twelve focused flow-rule checks run through `verifyFlowRules` as part of `build` and passed. Compiled JAR classes match the tested class outputs and target Java 21. No original Streams, UTF, or Minecraft classes are bundled.

## Dedicated-server environment

- Minecraft 1.21.1 / NeoForge 21.1.219.
- UltraTerraForged 1.0.2+1.21.1 and Streams Reflowing 2.15.0, supplied by the user.
- TerraBlender 4.1.0.8, Alex's Caves unofficial port 2.0.10, Citadel unofficial port 2.7.6, and the separate Terraforged Alex's Caves Fix 1.0.0 were also installed.
- Seed 918273645, the supplied tuned GenOne UTF preset, world ceiling Y=512, sea level 63. Raised water and winding rivers enabled.

## Passed current tests

The isolated current run used Streams' stream/lake carving disabled and MEDIUM terrain accuracy. A second run used stream/lake generation enabled and LOW terrain accuracy. Reports are `validation/isolated-currents.json` and `validation/streams-generation-enabled.json`.

Both passed:

- Native main-river sample at **X=103, Y=93, Z=8458**, above the generic sea-level band.
- Actual generated water at that predicted surface height.
- Native current layer present at the surface, bounded to the native water/bed estimate.
- Existing Streams-layer priority retained at an overlapping test column through its real layer-composition API.
- Full cached and provisional Streams grids include the native current; the server physics lookup sees it.
- No current at the same X/Z thirty-two blocks above the sampled surface.
- Actual NeoForge Streams payload codec preserves the layered grid on encode/decode, and its client-side data lookup preserves both the current and its height limit.
- The actual UTF level-selection method produced the controlled sequence 117, 96, 141, demonstrating that it can raise a river level. The uphill rejection rule recognizes that rise.
- `/utfstreams status` succeeds in the active profile. Nether and End are inactive.

One layer build for the sampled chunk took approximately 16.1 ms in the isolated run and 8.6 ms in the generation-enabled run. These are individual fixture timings, not a general performance benchmark. Added layer production repeats native terrain queries; this is a candidate for upstream optimization.

A separate vanilla Overworld with the same installed mods passed the inactive-profile and Nether/End checks. See `validation/vanilla-control.json`.

## Limits of these checks

No real remote client connected. The packet codec and client data-store lookup were exercised in the dedicated-server test process; client rendering, shaders, actual boat/item behavior observed in-game, complete network sessions, every junction, and the full modpack remain untested.

The adapter does not change native terrain or repair rises, waterfalls, disconnected channels or stream outlets. Its current bands come from generation estimates, so saved or modified block columns can disagree. This remains an experimental current adapter rather than full hydrology compatibility.

The runner reused the existing matching NeoForge development artifacts; all add-on source was compiled fresh. Environment-specific build wrappers and runtime dependencies are excluded from the source package.

## Binary

`ultraterraforged-streams-currents-0.1.0-alpha-neoforge-1.21.1.jar`

SHA-256: `a6f91041b279607f5f71db8dae9df441abb9dd2cf234d64c90e8d139bdf967ac`
