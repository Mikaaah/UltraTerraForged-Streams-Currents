package nl.townsandkingdoms.utfoptimizer.mixin;

import com.pandaismyname1.ultraterraforged.world.worldgen.landform.SiteCache;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import nl.townsandkingdoms.utfoptimizer.UtfOptimizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Optional: preserve most cached sites on the 4096-site overflow; default off. */
@Mixin(value=SiteCache.class, remap=false)
public abstract class SiteCacheMixin {
    @Redirect(method="get(IILcom/pandaismyname1/ultraterraforged/world/worldgen/landform/SiteCache$Finder;)Ljava/lang/Object;",
        at=@At(value="INVOKE",target="Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;clear()V",remap=false),
        remap=false)
    private void utfoptimizer$partialEviction(Long2ObjectOpenHashMap<?> sites) {
        if(!UtfOptimizer.incrementalSiteCacheEviction){ sites.clear(); return; }
        if(!sites.isEmpty()){
            long key=sites.keySet().iterator().nextLong();
            sites.remove(key);
            if(UtfOptimizer.measureStats) UtfOptimizer.partialCacheEvictions.increment();
        }
    }
}
