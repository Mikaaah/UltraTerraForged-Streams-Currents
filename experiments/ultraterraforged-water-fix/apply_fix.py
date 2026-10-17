#!/usr/bin/env python3
"""Apply experimental water continuity fixes to UltraTerraForged 1.21.1, 1.0.2."""
from pathlib import Path
import sys

root = Path(sys.argv[1]).resolve()
base = root / "common/src/main/java/com/pandaismyname1/ultraterraforged"
def change(path, edits):
    file = base / path
    s = file.read_text()
    for before, after in edits:
        if s.count(before) != 1:
            raise RuntimeError(f"{file}: expected one occurrence, got {s.count(before)}: {before[:80]}")
        s = s.replace(before, after, 1)
    file.write_text(s)
    print("Patched", path)

change("world/worldgen/cave/CaveCarving.java", [
    ("import org.jetbrains.annotations.Nullable;", """import java.util.ArrayDeque;
import java.util.BitSet;
import net.minecraft.tags.FluidTags;
import org.jetbrains.annotations.Nullable;"""),
    ("private final int[] ground = new int[256];", """private final int[] ground = new int[256];
    // Only blocks freshly carved into air can be flooded, not naturally sealed caves.
    private final BitSet carvedAir = new BitSet();"""),
    ("""        if (state != fill) {
            this.chunk.setBlockState(this.pos, fill, false);
        }
    }

    /**
     * Replaces the solid rock""".replace("        ", "\t\t").replace("    ", "\t"), """        if (state != fill) {
            if (fill == AIR && !state.isAir() && y >= this.bottom && y < this.chunk.getMaxBuildHeight()) {
                this.carvedAir.set(this.index(x, y, z));
            }
            this.chunk.setBlockState(this.pos, fill, false);
        }
    }

    // Bit-packed block positions in this chunk, from minimum build height.
    private int index(int x, int y, int z) {
        return (y - this.bottom) * 256 + (z - this.minZ) * 16 + (x - this.minX);
    }

    /**
     * Water is placed before the custom cave pass. A cave that opens into a lake
     * otherwise leaves an air bubble beneath the surface. Flood only carved air
     * connected to existing water; do not fill disconnected subterranean caves.
     */
    public void repairWaterIngress() {
        if (this.carvedAir.isEmpty()) return;
        ArrayDeque<Integer> pending = new ArrayDeque<>();
        for (int bit = this.carvedAir.nextSetBit(0); bit >= 0; bit = this.carvedAir.nextSetBit(bit + 1)) {
            int y = this.bottom + (bit >>> 8);
            int x = this.minX + (bit & 15);
            int z = this.minZ + ((bit >>> 4) & 15);
            if (this.touchesWater(x, y, z)) this.fillIngress(bit, pending);
        }
        while (!pending.isEmpty()) {
            int bit = pending.removeFirst();
            int dy = bit >>> 8, dx = bit & 15, dz = (bit >>> 4) & 15;
            if (dx > 0) this.fillIngress(bit - 1, pending);
            if (dx < 15) this.fillIngress(bit + 1, pending);
            if (dz > 0) this.fillIngress(bit - 16, pending);
            if (dz < 15) this.fillIngress(bit + 16, pending);
            if (dy > 0) this.fillIngress(bit - 256, pending);
        }
    }

    private void fillIngress(int bit, ArrayDeque<Integer> pending) {
        if (!this.carvedAir.get(bit)) return;
        this.carvedAir.clear(bit);
        int y = this.bottom + (bit >>> 8);
        int dx = bit & 15, dz = (bit >>> 4) & 15;
        this.pos.set(this.minX + dx, y, this.minZ + dz);
        if (!this.chunk.getBlockState(this.pos).isAir()) return;
        this.chunk.setBlockState(this.pos, WATER, false);
        pending.addLast(bit);
        if (dx == 0 || dx == 15 || dz == 0 || dz == 15) {
            this.chunk.markPosForPostprocessing(this.pos);
        }
    }

    private boolean touchesWater(int x, int y, int z) {
        if (y + 1 < this.chunk.getMaxBuildHeight() && this.waterAt(x, y + 1, z)) return true;
        if (x > this.minX && this.waterAt(x - 1, y, z)) return true;
        if (x < this.minX + 15 && this.waterAt(x + 1, y, z)) return true;
        if (z > this.minZ && this.waterAt(x, y, z - 1)) return true;
        return z < this.minZ + 15 && this.waterAt(x, y, z + 1);
    }

    private boolean waterAt(int x, int y, int z) {
        return this.chunk.getBlockState(this.pos.set(x, y, z)).getFluidState().is(FluidTags.WATER);
    }

    /**
     * Replaces the solid rock""".replace("        ", "\t\t").replace("    ", "\t")),
])

change("world/worldgen/cave/CaveFeatures.java", [
    ("""\t\tfor (Feature feature : this.features) {
\t\t\tfeature.carve(carving);
\t\t}
\t}""", """\t\tfor (Feature feature : this.features) {
\t\t\tfeature.carve(carving);
\t\t}
\t\tcarving.repairWaterIngress();
\t}""")
])
change("world/worldgen/rivermap/RaisedWater.java", [
    ("""\t\tLevels levels = context.levels;
\t\tint[] surfaces = new int[256];""", """\t\tLevels levels = context.levels;
\t\t// Restore missing sea-surface blocks at submerged ravines.
\t\tfillOceanSurface(chunk, cells, levels);
\t\tint[] surfaces = new int[256];"""),
    ("""\tprivate RaisedWater() {
\t}

\tpublic static void fill""", """\tprivate RaisedWater() {
\t}

\tprivate static void fillOceanSurface(ChunkAccess chunk, Tile.Chunk cells, Levels levels) {
\t\tint top = levels.waterY;
\t\tif (top < chunk.getMinBuildHeight() || top >= chunk.getMaxBuildHeight()) return;
\t\tChunkPos chunkPos = chunk.getPos();
\t\tint minX = chunkPos.getMinBlockX(), minZ = chunkPos.getMinBlockZ();
\t\tBlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
\t\tBlockState water = Blocks.WATER.defaultBlockState();
\t\tfor (int dz = 0; dz < 16; dz++) {
\t\t\tfor (int dx = 0; dx < 16; dx++) {
\t\t\t\tCell cell = cells.getCell(dx, dz);
\t\t\t\t// Exposed ocean/sea-level river only: leave land and sealed caves untouched.
\t\t\t\tif (cell.height >= levels.water) continue;
\t\t\t\tint x = minX + dx, z = minZ + dz;
\t\t\t\tif (!chunk.getBlockState(pos.set(x, top, z)).isAir()) continue;
\t\t\t\tfor (int y = top; y >= Math.max(chunk.getMinBuildHeight(), top - MAX_DEPTH); y--) {
\t\t\t\t\tBlockState state = chunk.getBlockState(pos.set(x, y, z));
\t\t\t\t\tif (!state.isAir()) break;
\t\t\t\t\tchunk.setBlockState(pos, water, false);
\t\t\t\t}
\t\t\t\tif (dx == 0 || dz == 0 || dx == 15 || dz == 15) {
\t\t\t\t\tchunk.markPosForPostprocessing(pos.set(x, top, z));
\t\t\t\t}
\t\t\t}
\t\t}
\t}

\tpublic static void fill""")
])
change("world/worldgen/terrain/populator/RiverPopulator.java", [
    ("""    // the water stays level from one sample to the next, so it changes in steps: small waterfalls
    private static float sample(float[] values, float t) {
    \treturn values[stretch(t, values.length)];
    }""", """    // Distribute 3-5 block drops across the reach instead of a single vertical wall.
    // Actual water blocks are quantized to individual block-height steps.
    private static float sample(float[] values, float t) {
        float position = NoiseUtil.clamp(t, 0.0F, 1.0F) * (values.length - 1);
        int index = Math.min((int) position, values.length - 2);
        return NoiseUtil.lerp(values[index], values[index + 1], position - index);
    }""")
])
test_file = root / "common/src/test/java/com/pandaismyname1/ultraterraforged/test/RiverWaterTransitionTest.java"
test_file.write_text("""package com.pandaismyname1.ultraterraforged.test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import com.pandaismyname1.ultraterraforged.world.worldgen.terrain.populator.RiverPopulator;
public class RiverWaterTransitionTest {
    @Test void tallDropsAreInterpolated() throws Exception {
        Method sample = RiverPopulator.class.getDeclaredMethod("sample", float[].class, float.class);
        sample.setAccessible(true);
        float[] water = {100.0F, 95.0F};
        assertEquals(100.0F, (float) sample.invoke(null, water, 0.0F), 0.001F);
        assertEquals(98.75F, (float) sample.invoke(null, water, 0.25F), 0.001F);
        assertEquals(97.50F, (float) sample.invoke(null, water, 0.50F), 0.001F);
        assertEquals(96.25F, (float) sample.invoke(null, water, 0.75F), 0.001F);
        assertEquals(95.0F, (float) sample.invoke(null, water, 1.0F), 0.001F);
    }
}
""")
print("Water continuity patch applied.")
