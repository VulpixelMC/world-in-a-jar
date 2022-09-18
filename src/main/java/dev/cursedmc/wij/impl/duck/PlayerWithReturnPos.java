package dev.cursedmc.wij.impl.duck;

import net.minecraft.util.math.Vec3d;

// todo create api
/**
 * Holds a return position. A mixin implements this interface on all {@link net.minecraft.entity.player.PlayerEntity}s.
 */
public interface PlayerWithReturnPos {
	Vec3d worldinajar$getReturnPos();
	void worldinajar$setReturnPos(Vec3d returnPos);
}
