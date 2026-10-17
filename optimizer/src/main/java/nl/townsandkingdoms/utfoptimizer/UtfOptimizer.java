package nl.townsandkingdoms.utfoptimizer;

import com.mojang.logging.LogUtils;
import java.util.concurrent.atomic.LongAdder;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

@Mod(UtfOptimizer.MOD_ID)
public final class UtfOptimizer {
    public static final String MOD_ID = "utfoptimizer";
    public static final Logger LOG = LogUtils.getLogger();
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue SKIP_LAKE_WARP, INCREMENTAL_CACHE, METRICS;
    public static volatile boolean skipUnusedLakeWarp = true;
    public static volatile boolean incrementalSiteCacheEviction = false;
    public static volatile boolean measureStats = false;
    public static final LongAdder skippedLakeWarps = new LongAdder();
    public static final LongAdder activeRiverColumns = new LongAdder();
    public static final LongAdder partialCacheEvictions = new LongAdder();
    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.push("generation");
        SKIP_LAKE_WARP = b.comment("Skip lake warp when no river network reaches the cell; leaves terrain output unchanged.").define("skipUnusedLakeWarp", true);
        INCREMENTAL_CACHE = b.comment("Experimental: evict one site instead of clearing all cached sites. Off by default.").define("incrementalSiteCacheEviction", false);
        b.pop();
        b.push("diagnostics");
        METRICS = b.comment("Enable counters for /utfoptimizer status; slight overhead.").define("enableCounters", false);
        b.pop();
        SPEC = b.build();
    }
    public UtfOptimizer(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, SPEC);
        bus.addListener(this::onLoad);
        bus.addListener(this::onReload);
        NeoForge.EVENT_BUS.addListener(UtfOptimizer::registerCommands);
        LOG.info("UltraTerraForged Optimizer v0.1.0 initialized");
    }
    private void onLoad(ModConfigEvent.Loading e) { if(e.getConfig().getSpec()==SPEC) reload(); }
    private void onReload(ModConfigEvent.Reloading e) { if(e.getConfig().getSpec()==SPEC) reload(); }
    private static void reload() {
        skipUnusedLakeWarp=SKIP_LAKE_WARP.get();
        incrementalSiteCacheEviction=INCREMENTAL_CACHE.get();
        measureStats=METRICS.get();
        LOG.info("UTF Optimizer: lake-warp={}, incremental-cache={}, diagnostics={}", skipUnusedLakeWarp,incrementalSiteCacheEviction,measureStats);
    }
    private static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("utfoptimizer").requires(s->s.hasPermission(2))
            .then(Commands.literal("status").executes(c->{
                String msg="UTF Optimizer | skipUnusedLakeWarp="+skipUnusedLakeWarp
                    +" | incrementalEviction="+incrementalSiteCacheEviction
                    +" | counters="+measureStats
                    +" | skipped columns="+skippedLakeWarps.sum()
                    +" | river columns="+activeRiverColumns.sum()
                    +" | partial evictions="+partialCacheEvictions.sum();
                c.getSource().sendSuccess(()->Component.literal(msg),false);
                return 1;
            }))
            .then(Commands.literal("resetstats").executes(c->{
                skippedLakeWarps.reset(); activeRiverColumns.reset(); partialCacheEvictions.reset();
                c.getSource().sendSuccess(()->Component.literal("UTF Optimizer counters reset."),false);
                return 1;
            })));
    }
}
