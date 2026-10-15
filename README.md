# UltraTerraForged Streams Currents — experimental

[Download the current alpha JAR](downloads/ultraterraforged-streams-currents-0.1.0-alpha-neoforge-1.21.1.jar?raw=true) · [Validation](VALIDATION.md)

A separate current adapter for **Minecraft 1.21.1**, **NeoForge 21.1.219–21.1.x**, **UltraTerraForged 1.0.2+1.21.1**, and **Streams Reflowing 2.15.0**. Java 21 is required.

An early test build for height-aware native river currents. This is not complete hydrology compatibility and does not change terrain or replace either original JAR.

## Install

1. Keep both original mods installed.
2. Add this JAR to the client and server `mods` folders. For singleplayer, install in your instance.
3. Use an UltraTerraForged Overworld with **raised water or winding rivers enabled**. These settings initialize the native route frames this build uses. The previously supplied GenOne preset has both enabled. If both are off, the adapter stays inactive and leaves Streams' original path alone.
4. Keep `vanillaWaterFlow=true` and `flowVanillaRivers=true` in `config/streamsreflowing.properties`.
5. Restart and run `/utfstreams status` as an operator. Active should be true; column counts increase when Streams builds grids near a native river.

Currents arrive through Streams' normal grid preparation and synchronization. Allow the area to finish preparing before judging a newly visited river.

The Alex's Caves fix is independent and may remain installed. This JAR does not require Alex's Caves.

## Behavior

- Observes UTF's selected native river carver and path position during explicit terrain queries.
- Reads each column's native water elevation and bed estimate rather than assuming sea-level water.
- Derives horizontal direction from the routed, warped world-space channel toward its native mouth.
- Registers a layer through Streams' `StreamFlowGrid.registerLayerProducer` API. Existing Streams layers retain first priority at overlaps.
- Uses Streams' own height-aware grid cache and networking format.
- Replaces the generic natural-river current pass only in supported active UTF Overworlds. Streams' own streams remain enabled.
- Stops the generic natural-river feature from baking an unrelated direction into native UTF water. Native water stays ordinary water.
- Skips detected local uphill steps by default; it does not reverse a whole river to conceal a rise.

## Configuration

First launch creates `config/utfstreams-common.toml`:

```toml
enabled = true
skipUphillSegments = true
nativeRiverSpeed = 1.0
maxCurrentDepth = 64
```

Restart after changing current settings so cached grids are rebuilt. Turning off the uphill guard can draw currents toward higher water; this is a debugging option, not a terrain repair.

## Limits

- Does not repair uphill steps, waterfalls, disconnected water, river junctions, or tributary carving.
- Does not bridge native terrain heights into Streams' watershed planner or speed up its height queries.
- Uses generator estimates rather than inspecting saved block columns. It can disagree with old terrain after a preset change, with player-built pools at matching coordinates/heights, or with another mod's river changes.
- Existing baked fluid blocks are not converted back. Existing Streams layers retain priority. Test in a copied or new world first.
- Client rendering, shaders and full modpack compatibility need in-game testing. See VALIDATION.md for completed checks.

## Build

Use JDK 21 and place these original files in `libs/`:

```text
ultraterraforged-1.0.2+1.21.1-neoforge.jar
StreamsReflowing-1.21.1-neoforge-2.15.0.jar
```

Linux/macOS: `./gradlew build`. Windows: `gradlew.bat build`.

Artifacts appear in `build/libs/`. Original dependency JARs are not redistributed. The optional `runtime-libs/` folder supplies development server dependencies only. Tests run only with the explicit developer JVM property `utfstreams.integrationTest`; normal launches never run tests or stop the server.

## Credits and license

Independent experimental add-on by Towns & Kingdoms. Not an official release by either original mod team. GPL-3.0-only; see LICENSE. Streams Reflowing retains its own license and its original code/JAR are not bundled.
