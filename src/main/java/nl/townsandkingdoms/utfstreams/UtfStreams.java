package nl.townsandkingdoms.utfstreams;

import com.mojang.logging.LogUtils;
import com.pandaismyname1.ultraterraforged.world.worldgen.GeneratorContext;
import com.pandaismyname1.ultraterraforged.world.worldgen.UTFRandomState;
import dev.streamsreflowing.flow.StreamFlowGrid;
import dev.streamsreflowing.platform.Services;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

@Mod("utfstreams")
public final class UtfStreams {
    public static final Logger LOG = LogUtils.getLogger();
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLED, SKIP_UPHILL;
    public static final ModConfigSpec.DoubleValue SPEED;
    public static final ModConfigSpec.IntValue MAX_DEPTH;
    static {
        var b = new ModConfigSpec.Builder();
        ENABLED = b.define("enabled", true);
        SKIP_UPHILL = b.comment("Leave detected uphill river stretches without native currents; does not repair terrain.")
                .define("skipUphillSegments", true);
        SPEED = b.defineInRange("nativeRiverSpeed", 1.0, 0.1, 2.0);
        MAX_DEPTH = b.defineInRange("maxCurrentDepth", 64, 1, 256);
        SPEC = b.build();
    }
    public UtfStreams(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, SPEC);
        bus.addListener(this::setup);
        NeoForge.EVENT_BUS.addListener(UtfStreams::commands);
        IntegrationChecks.register();
    }
    private void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            StreamFlowGrid.registerLayerProducer(NativeFlowLayers::layers);
            LOG.info("UTF Streams experimental native current layer registered; terrain is not modified.");
        });
    }
    public static GeneratorContext context(ServerLevel level) {
        if (level == null || !level.dimension().equals(Level.OVERWORLD) || !ENABLED.get()) return null;
        // All hooks must agree on activation. Preserve the original baked-fluid path
        // when ordinary-water currents or natural-river currents are disabled.
        if (!Services.PLATFORM.vanillaWaterFlow() || !Services.PLATFORM.flowVanillaRivers()) return null;
        Object random = level.getChunkSource().randomState();
        GeneratorContext context = random instanceof UTFRandomState state && state.usesCells()
                ? state.generatorContext() : null;
        if (context == null) return null;
        // These options make UTF initialize the native world-space route frames.
        // Leave the original Streams path untouched on unsupported plain presets.
        var rivers = context.preset.rivers();
        return rivers.raisedWater || rivers.winding ? context : null;
    }
    private static void commands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("utfstreams").requires(s -> s.hasPermission(2))
                .then(Commands.literal("status").executes(c -> {
                    boolean active = context(c.getSource().getLevel()) != null;
                    c.getSource().sendSuccess(() -> Component.literal("UTF Streams: active=" + active
                            + "; native columns=" + NativeFlowLayers.COLUMNS.sum()
                            + "; uphill skipped=" + NativeFlowLayers.UPHILL.sum()
                            + "; layer builds=" + NativeFlowLayers.BUILDS.sum()), false);
                    return active ? 1 : 0;
                })));
    }
}
