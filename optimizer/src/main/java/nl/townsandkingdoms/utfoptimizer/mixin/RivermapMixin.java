package nl.townsandkingdoms.utfoptimizer.mixin;

import com.pandaismyname1.ultraterraforged.world.worldgen.cell.Cell;
import com.pandaismyname1.ultraterraforged.world.worldgen.noise.domain.Domain;
import com.pandaismyname1.ultraterraforged.world.worldgen.rivermap.Rivermap;
import com.pandaismyname1.ultraterraforged.world.worldgen.rivermap.river.Network;
import nl.townsandkingdoms.utfoptimizer.UtfOptimizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Avoid both lake-domain noise calls outside network bounds, preserving exact carving order. */
@Mixin(value=Rivermap.class, remap=false)
public abstract class RivermapMixin {
    @Shadow(remap=false) private Domain riverWarp;
    @Shadow(remap=false) private Domain lakeWarp;
    @Shadow(remap=false) private Network[] networks;

    @Inject(method="apply(Lcom/pandaismyname1/ultraterraforged/world/worldgen/cell/Cell;FF)V",
        at=@At("HEAD"), cancellable=true, remap=false)
    private void utfoptimizer$apply(Cell cell, float x, float z, CallbackInfo ci) {
        if (!UtfOptimizer.skipUnusedLakeWarp) return;
        float rx=riverWarp.getX(x,z,0), rz=riverWarp.getZ(x,z,0);
        int first=0;
        while(first<networks.length && !networks[first].contains(rx,rz)) first++;
        if(first==networks.length){
            if(UtfOptimizer.measureStats) UtfOptimizer.skippedLakeWarps.increment();
            ci.cancel();
            return;
        }
        float lx=lakeWarp.getOffsetX(rx,rz,0), lz=lakeWarp.getOffsetZ(rx,rz,0);
        for(int i=first;i<networks.length;i++){
            Network n=networks[i];
            if(n.contains(rx,rz)) n.carve(cell,rx,rz,lx,lz);
        }
        if(UtfOptimizer.measureStats) UtfOptimizer.activeRiverColumns.increment();
        ci.cancel();
    }
}
