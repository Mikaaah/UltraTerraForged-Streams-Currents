# Manual in-game checks

Use a copied or new world before testing this experimental add-on. Keep a copy of both original mods, their existing configuration and the terrain preset.

1. Install the add-on on client and server alongside UTF 1.0.2+1.21.1 and Streams 2.15.0. Keep ordinary-water currents and natural-river currents enabled in Streams.
2. Use a UTF world with winding rivers or raised water enabled. Run `/utfstreams status` as an operator and check that it reports active.
3. Find a wide native main river above sea level. Compare with and without the add-on after a restart. Use a dropped item or a boat to distinguish a physical current from a shader animation.
4. Check a flat section, a bend, a falling step, a local rising step and a tributary junction. Detected rises may intentionally have no current.
5. Check that Streams' own tributaries still flow where they overlap the native layer. Existing Streams layers retain priority.
6. Check pools well above the river and underground water below its estimated bed: the native current should not appear there.
7. Leave and revisit the area, restart the game, and test with a separate multiplayer client. Compare shaders off and on.

For a reproducible fixture, see the test reports in `validation/`: they record the seed, coordinates and water elevation of a sampled native main river where available. A generator estimate does not prove every water block in the same biome or chunk belongs to that river.

Changing an established world's terrain preset can make native current estimates disagree with saved blocks. Use a separate new world for the optional sea-level diagnostic described in DEVELOPER_FINDINGS.md.
