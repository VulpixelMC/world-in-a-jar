package gay.sylv.wij.mixin.duck;

import gay.sylv.wij.impl.network.Networking;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Holds a return position. A mixin implements this interface on all {@link net.minecraft.world.entity.player.Player}s.
 */
public interface PlayerWithReturnPos {
	@Nullable Vec3 worldinajar$getReturnPos(Networking.JarLocation jarLocation);
	void worldinajar$setReturnPos(Networking.JarLocation jarLocation, Vec3 returnPos);
}
