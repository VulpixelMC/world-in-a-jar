/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package gay.sylv.wij.impl.duck;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

// todo create api
/**
 * Holds a return dimension ({@link RegistryKey}<{@link World}>). A mixin implements this class on every {@link net.minecraft.entity.player.PlayerEntity}.
 */
@SuppressWarnings("unused")
public interface PlayerWithReturnDim {
	RegistryKey<World> worldinajar$getReturnDim();
	void worldinajar$setReturnDim(RegistryKey<World> returnDim);
}
