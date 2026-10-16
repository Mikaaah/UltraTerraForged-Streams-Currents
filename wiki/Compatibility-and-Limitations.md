# Compatibility & Limitations

## Limitations

This is a current adapter. It does not change river terrain, repair elevated water steps or fix tributary placement. Detected uphill segments are skipped by default.

Current coverage uses terrain-generator estimates. It may differ from saved terrain after preset changes or from water modified by players or other mods. Previously generated Streams fluid blocks are not replaced.

## Active worlds and competing behavior

The current adapter requires an active UltraTerraForged Overworld using raised water or winding river paths. Existing Streams Reflowing flow layers remain authoritative where they overlap. The mod does not correct river routing, water heights or old fluid blocks.

## Tested dependency versions

See [Getting Started](Getting-Started.md) for the specific versions documented by this repository. Newer dependency versions are not automatically guaranteed to work.

## Bugs outside this add-on's scope

Native UltraTerraForged terrain can contain uneven river heights or disconnected water. This adapter adds *currents*, not complete watershed or carving compatibility. Saved terrain, player edits, and other world-generation mods can differ from generated water estimates.

[← Wiki Home](Home.md)
