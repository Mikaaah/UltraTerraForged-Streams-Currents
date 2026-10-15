package nl.townsandkingdoms.utfstreams;

import com.pandaismyname1.ultraterraforged.world.worldgen.GeneratorContext;
import dev.streamsreflowing.core.RiverEngine;
import dev.streamsreflowing.flow.StreamFlowGrid;
import dev.streamsreflowing.platform.Services;
import net.minecraft.server.level.ServerLevel;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

/** Server-produced layers use Streams' existing height-aware cache and network path. */
public final class NativeFlowLayers {
    public static final LongAdder SAMPLES = new LongAdder(), COLUMNS = new LongAdder(),
            UPHILL = new LongAdder(), BUILDS = new LongAdder(), NANOS = new LongAdder();
    private NativeFlowLayers() {}

    public static List<StreamFlowGrid.Layer> layers(ServerLevel level, RiverEngine engine, int cx, int cz) {
        GeneratorContext context = UtfStreams.context(level);
        if (context == null || !Services.PLATFORM.flowVanillaRivers()
                || !Services.PLATFORM.vanillaWaterFlow()) return List.of();
        long started = System.nanoTime();
        byte[] angles = new byte[256], speeds = new byte[256];
        short[] lo = new short[256], hi = new short[256];
        Arrays.fill(angles, (byte)-1);
        int count = 0;
        try {
            for (int z = 0; z < 16; z++) for (int x = 0; x < 16; x++) {
                int wx = (cx << 4) + x, wz = (cz << 4) + z;
                SAMPLES.increment();
                NativeCapture.Sample s = NativeCapture.sample(context, wx, wz);
                if (s == null) continue;
                if (UtfStreams.SKIP_UPHILL.get() && s.uphill()) { UPHILL.increment(); continue; }
                // The original Streams layer stays first and retains its own tributary flow.
                // Do not sample engine.plan here: provisional builds may not have a ready watershed.
                int i = x + z * 16;
                int bottom = Math.max(s.bedY() + 1, s.surfaceY() - UtfStreams.MAX_DEPTH.get() + 1);
                int top = s.surfaceY();
                if (bottom > top) continue;
                angles[i] = StreamFlowGrid.encode(s.dx(), s.dz());
                speeds[i] = StreamFlowGrid.packSpeed(UtfStreams.SPEED.get());
                lo[i] = (short)bottom; hi[i] = (short)top;
                count++;
            }
            COLUMNS.add(count);
            return count == 0 ? List.of() : List.of(new StreamFlowGrid.Layer(angles, speeds, lo, hi));
        } finally { BUILDS.increment(); NANOS.add(System.nanoTime() - started); }
    }
}
