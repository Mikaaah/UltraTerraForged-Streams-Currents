package nl.townsandkingdoms.utfstreams.mixin;

import dev.streamsreflowing.core.RiverEngine;
import dev.streamsreflowing.core.river.RiverFlowField;
import dev.streamsreflowing.flow.StreamFlowGrid;
import net.minecraft.server.level.ServerLevel;
import nl.townsandkingdoms.utfstreams.UtfStreams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = StreamFlowGrid.class, remap = false)
public abstract class StreamGridMixin {
    @ModifyVariable(method = {"getOrBuild", "buildProvisional"}, at = @At("HEAD"),
            argsOnly = true, ordinal = 0, remap = false)
    private static boolean utfstreams$replaceGenericRiverPass(boolean original,
            ServerLevel level, RiverEngine engine, RiverFlowField.BiomeClass classifier,
            RiverFlowField.OrientationField orientation, boolean flowRivers, int cx, int cz) {
        return UtfStreams.context(level) == null ? original : false;
    }
}
