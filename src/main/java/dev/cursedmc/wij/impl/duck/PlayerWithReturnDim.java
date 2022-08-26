package dev.cursedmc.wij.impl.duck;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

// todo create api
/**
 * Holds a return dimension ({@link RegistryKey}<{@link World}>). A mixin implements this class on every {@link net.minecraft.entity.player.PlayerEntity}.
 */
public interface PlayerWithReturnDim {
	RegistryKey<World> worldinajar$getReturnDim();
	void worldinajar$setReturnDim(RegistryKey<World> returnDim);
}
