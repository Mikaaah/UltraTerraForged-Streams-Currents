package nl.townsandkingdoms.utfstreams;

import com.pandaismyname1.ultraterraforged.world.worldgen.GeneratorContext;
import com.pandaismyname1.ultraterraforged.world.worldgen.cell.Cell;
import com.pandaismyname1.ultraterraforged.world.worldgen.rivermap.RiverRoutes;
import com.pandaismyname1.ultraterraforged.world.worldgen.terrain.populator.RiverPopulator;
import com.pandaismyname1.ultraterraforged.world.worldgen.util.PosUtil;

/** Observes native generation only during this add-on's explicit sample query. */
public final class NativeCapture {
    private static final ThreadLocal<Query> ACTIVE = new ThreadLocal<>();
    private static final ThreadLocal<Cell> CELLS = ThreadLocal.withInitial(Cell::new);
    private NativeCapture() {}

    public static void entering(RiverPopulator carver, Cell cell, float t) {
        Query q = ACTIVE.get();
        if (q != null && q.cell == cell) { q.active = carver; q.t = t; }
    }
    public static void tagged(RiverPopulator carver, Cell cell) {
        Query q = ACTIVE.get();
        if (q != null && q.cell == cell && q.active == carver) {
            q.selected = carver; q.selectedT = q.t;
        }
    }

    public static Sample sample(GeneratorContext context, int x, int z) {
        Cell cell = CELLS.get().reset();
        Query previous = ACTIVE.get();
        Query query = new Query(cell);
        ACTIVE.set(query);
        try {
            // Re-run the native pure terrain query. Cached cells would skip the observer.
            context.lookup.compute(cell, x, z);
        } finally {
            if (previous == null) ACTIVE.remove(); else ACTIVE.set(previous);
        }
        RiverPopulator carver = query.selected;
        if (!cell.terrain.isRiver() || carver == null || carver.frame == null) return null;
        float t = Math.max(0, Math.min(1, query.selectedT));
        float dt = Math.min(0.05f, 8.0f / Math.max(1, carver.river.length));
        float before = Math.max(0, t - dt), after = Math.min(1, t + dt);
        if (after <= before) return null;
        int scale = context.levels.worldHeight;
        float level = cell.waterLevel > 0 ? cell.waterLevel : context.levels.water;
        int surface = FlowRules.waterY(level, scale);
        if (!(cell.height < level) || surface < context.levels.waterY) return null;
        long a = RiverRoutes.channelInWorld(carver, before);
        long b = RiverRoutes.channelInWorld(carver, after);
        double dx = PosUtil.unpackLeftf(b) - PosUtil.unpackLeftf(a);
        double dz = PosUtil.unpackRightf(b) - PosUtil.unpackRightf(a);
        if (!FlowRules.usableVector(dx, dz)) return null;
        boolean uphill = FlowRules.uphill(carver.waterLevelAt(before) * scale,
                carver.waterLevelAt(t) * scale, carver.waterLevelAt(after) * scale);
        return new Sample(dx, dz, surface, FlowRules.waterY(cell.height, scale), uphill,
                carver.main, x, z);
    }

    private static final class Query {
        final Cell cell;
        RiverPopulator active, selected;
        float t, selectedT;
        Query(Cell cell) { this.cell = cell; }
    }
    public record Sample(double dx, double dz, int surfaceY, int bedY,
                         boolean uphill, boolean main, int x, int z) {}
}
