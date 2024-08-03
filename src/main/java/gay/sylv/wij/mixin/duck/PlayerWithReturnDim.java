package gay.sylv.wij.mixin.duck;

import gay.sylv.wij.impl.network.Networking;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Holds a return dimension ({@link net.minecraft.resources.ResourceKey}&lt;{@link net.minecraft.world.level.Level}&gt;). A mixin implements this class on every {@link net.minecraft.world.entity.player.Player}.
 */
public interface PlayerWithReturnDim {
	@Nullable ResourceKey<Level> worldinajar$getReturnDimension(Networking.JarLocation jarLocation);
	void worldinajar$setReturnDimension(Networking.JarLocation jarLocation, ResourceKey<Level> dimension);
}
