#!/usr/bin/env python3
"""Second-pass hydrology corrections, applied AFTER apply_fix.py on UTF 1.21.1."""
from pathlib import Path
import sys
root = Path(sys.argv[1])
base = root / "common/src/main/java/com/pandaismyname1/ultraterraforged"

def replace(path, old, new):
    file = base / path
    s = file.read_text()
    if s.count(old) != 1:
        raise RuntimeError(f"{path}: expected one exact patch anchor; found {s.count(old)}")
    file.write_text(s.replace(old, new, 1))
    print("Patched", path)

water="world/worldgen/rivermap/WaterLevels.java"
replace(water,
"""		// it meets the water it flows into at that water's level, stepping up or down to it
		water[water.length - 1] = mouth;
		carver.setWaterLevels(water);""",
"""		// Correct the receiver junction BEFORE installing the profile. The previous
		// code unconditionally replaced the last level, even when this produced a
		// 10+ block cliff in one 16-block reach. Lower upstream reaches towards the
		// receiving water, but never beyond the preset's permitted gorge depth.
		int[] blocks = new int[water.length];
		for (int i = 0; i < water.length; i++) {
			blocks[i] = levels.scale(water[i]);
		}
		blocks[blocks.length - 1] = levels.scale(mouth);
		easeMouthTransition(blocks, lowest, gorge, levels);
		for (int i = 0; i < blocks.length - 1; i++) {
			water[i] = height(blocks[i], levels);
		}
		// Keep the receiver exactly equal, including fractional precision.
		water[water.length - 1] = mouth;
		carver.setWaterLevels(water);""")
replace(water,
"""	private static final int TERRACE = 3;""",
"""	private static final int TERRACE = 1;
	// Maximum preferred loss of height per 16-block survey reach, before
	// terrain's gorge-depth constraint makes a genuine waterfall unavoidable.
	private static final int MAX_FALL_PER_REACH = 3;""")
replace(water,
"""	// the top water block of the next stretch of a river, from the last one's and the lowest land beside this one; never""",
"""	/**
	 * Ease a forced receiving-water jump backwards along the river. This only
	 * lowers calculated water levels; it cannot create raised, leaking banks.
	 * The natural minimum gorge depth is respected, so a large unavoidable
	 * elevation change remains a waterfall instead of cutting an extreme trench.
	 * Adjacent reaches with enough clearance lose at most MAX_FALL_PER_REACH.
	 */
	static void easeMouthTransition(int[] blocks, float[] lowest, float gorge, Levels levels) {
		for (int i = blocks.length - 2; i >= 0; i--) {
			int deepest = Math.max(levels.waterY,
				(int) Math.ceil((lowest[i] - gorge) * levels.worldHeight));
			int preferred = blocks[i + 1] + MAX_FALL_PER_REACH;
			if (blocks[i] > preferred) {
				blocks[i] = Math.max(deepest, preferred);
			}
		}
	}

	// the top water block of the next stretch of a river, from the last one's and the lowest land beside this one; never""")

raised="world/worldgen/rivermap/RaisedWater.java"
replace(raised,
"""				} else if (state.getFluidState().isEmpty()) {
					break;
				}""",
"""				} else if (state.getFluidState().isEmpty()) {
					break;
				} else if (state.is(Blocks.WATER)) {
					// Count pre-existing water too. Before, only freshly
					// placed blocks counted, so complete source columns
					// looked like empty neighbours and triggered spills.
					tops[i] = Math.max(tops[i], y);
				}""")
# Note: existing cells with waterlogged blocks are also fluid but not sources;
# source-cell classification is intentional here.

carve="world/worldgen/cave/CaveCarving.java"
path=base/carve
s=path.read_text()
s=s.replace("import java.util.BitSet;","import java.util.BitSet;\nimport java.util.Arrays;")
start=s.index("\tpublic void repairWaterIngress()")
end=s.index("\t/**\n\t * Replaces the solid rock",start)
new='''	public void repairWaterIngress() {
		if (this.carvedAir.isEmpty()) return;
		// Only allocate the flood data in chunks in which caves actually opened.
		int[] bestCeiling = new int[(this.chunk.getMaxBuildHeight() - this.bottom) * 256];
		Arrays.fill(bestCeiling, Integer.MIN_VALUE);
		BitSet flooded = new BitSet();
		ArrayDeque<Long> pending = new ArrayDeque<>();
		// A seed must be a NEWLY CARVED block actually touching water.
		for (int bit = this.carvedAir.nextSetBit(0); bit >= 0; bit = this.carvedAir.nextSetBit(bit + 1)) {
			int y = this.bottom + (bit >>> 8);
			int x = this.minX + (bit & 15);
			int z = this.minZ + ((bit >>> 4) & 15);
			int waterCeiling = this.touchingWaterCeiling(x, y, z);
			if (waterCeiling >= y) {
				pending.addLast(packIngress(bit, waterCeiling));
			}
		}
		// Follow both freshly carved and pre-existing AIR. A narrow newly carved
		// opening often joins a much larger vanilla air cave. Vertical expansion
		// may go UP, but never higher than its actual source-water level.
		while (!pending.isEmpty()) {
			long packed = pending.removeFirst();
			int bit = (int) packed;
			int ceiling = (int) (packed >> 32);
			if (bit < 0 || bit >= bestCeiling.length || bestCeiling[bit] >= ceiling) continue;
			int y = this.bottom + (bit >>> 8);
			if (y > ceiling) continue;
			int dx = bit & 15, dz = (bit >>> 4) & 15;
			this.pos.set(this.minX + dx, y, this.minZ + dz);
			BlockState state = this.chunk.getBlockState(this.pos);
			if (!state.isAir() && !flooded.get(bit)) continue;
			bestCeiling[bit] = ceiling;
			if (state.isAir()) {
				this.chunk.setBlockState(this.pos, WATER, false);
				flooded.set(bit);
				if (dx == 0 || dx == 15 || dz == 0 || dz == 15) {
					// The neighbour may not be generated yet; allow water to
					// continue past this boundary when the chunk is finalized.
					this.chunk.markPosForPostprocessing(this.pos);
				}
			}
			if (dx > 0) pending.addLast(packIngress(bit - 1, ceiling));
			if (dx < 15) pending.addLast(packIngress(bit + 1, ceiling));
			if (dz > 0) pending.addLast(packIngress(bit - 16, ceiling));
			if (dz < 15) pending.addLast(packIngress(bit + 16, ceiling));
			if (y > this.bottom) pending.addLast(packIngress(bit - 256, ceiling));
			if (y < ceiling && y + 1 < this.chunk.getMaxBuildHeight()) {
				pending.addLast(packIngress(bit + 256, ceiling));
			}
		}
	}

	private static long packIngress(int bit, int ceiling) {
		return ((long) ceiling << 32) | (bit & 0xFFFFFFFFL);
	}

	private int touchingWaterCeiling(int x, int y, int z) {
		int ceiling = Integer.MIN_VALUE;
		if (y + 1 < this.chunk.getMaxBuildHeight() && this.waterAt(x, y + 1, z)) ceiling = y + 1;
		if (x > this.minX && this.waterAt(x - 1, y, z)) ceiling = Math.max(ceiling, y);
		if (x < this.minX + 15 && this.waterAt(x + 1, y, z)) ceiling = Math.max(ceiling, y);
		if (z > this.minZ && this.waterAt(x, y, z - 1)) ceiling = Math.max(ceiling, y);
		if (z < this.minZ + 15 && this.waterAt(x, y, z + 1)) ceiling = Math.max(ceiling, y);
		return ceiling;
	}

	private boolean waterAt(int x, int y, int z) {
		return this.chunk.getBlockState(this.pos.set(x, y, z)).getFluidState().is(FluidTags.WATER);
	}

'''
s=s[:start]+new+s[end:]
path.write_text(s)
print("Patched",carve)
test=root/"common/src/test/java/com/pandaismyname1/ultraterraforged/world/worldgen/rivermap"
test.mkdir(parents=True,exist_ok=True)
(test/"WaterLevelTransitionTest.java").write_text('''package com.pandaismyname1.ultraterraforged.world.worldgen.rivermap;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.pandaismyname1.ultraterraforged.world.worldgen.heightmap.Levels;

public class WaterLevelTransitionTest {
    @Test void smoothsForcedMouthDropWithinAllowedGorge() {
        Levels levels = new Levels(384, 63);
        int[] y = {100, 100, 100, 100, 90};
        float[] land = {104f/384f, 104f/384f, 104f/384f, 104f/384f, 94f/384f};
        WaterLevels.easeMouthTransition(y, land, 32f/384f, levels);
        assertArrayEquals(new int[]{100,99,96,93,90}, y);
    }
    @Test void neverCutsBelowAllowedDepthOrRaisesWater() {
        Levels levels = new Levels(384, 63);
        int[] y = {107,105,100,69};
        float[] land = {110f/384f,108f/384f,105f/384f,72f/384f};
        WaterLevels.easeMouthTransition(y, land, 8f/384f, levels);
        assertEquals(97, y[2]); // gorge floor, not the ideal 72
        assertEquals(69, y[3]);
        assertTrue(y[0] <= 107 && y[1] <= 105);
    }
    @Test void preservesGentleTransitions() {
        Levels levels = new Levels(384,63);
        int[] y = {90,88,86,84};
        float[] land = {94f/384f,92f/384f,90f/384f,88f/384f};
        WaterLevels.easeMouthTransition(y,land,32f/384f,levels);
        assertArrayEquals(new int[]{90,88,86,84},y);
    }
}
''')
print("Hydrology v2 applied")
