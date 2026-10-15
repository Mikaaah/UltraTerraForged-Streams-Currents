package nl.townsandkingdoms.utfstreams.mixin;

import com.pandaismyname1.ultraterraforged.world.worldgen.cell.Cell;
import com.pandaismyname1.ultraterraforged.world.worldgen.terrain.populator.RiverPopulator;
import nl.townsandkingdoms.utfstreams.NativeCapture;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RiverPopulator.class, remap = false)
public abstract class RiverCaptureMixin {
    @Inject(method = "apply", at = @At("HEAD"), remap = false)
    private void utfstreams$enter(Cell cell, float px, float pz, float pt,
                                  float x, float z, float t, CallbackInfo ci) {
        NativeCapture.entering((RiverPopulator)(Object)this, cell, t);
    }

    @Inject(method = "tag", at = @At(value = "FIELD",
            target = "Lcom/pandaismyname1/ultraterraforged/world/worldgen/cell/Cell;terrain:Lcom/pandaismyname1/ultraterraforged/world/worldgen/terrain/Terrain;",
            opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER), remap = false)
    private void utfstreams$tagged(Cell cell, float bed, float level, CallbackInfo ci) {
        NativeCapture.tagged((RiverPopulator)(Object)this, cell);
    }
}
