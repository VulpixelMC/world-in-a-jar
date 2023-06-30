/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package gay.sylv.wij.impl.duck;

import net.minecraft.util.math.Vec3d;

// todo create api
/**
 * Holds a return position. A mixin implements this interface on all {@link net.minecraft.entity.player.PlayerEntity}s.
 */
@SuppressWarnings("unused")
public interface PlayerWithReturnPos {
	Vec3d worldinajar$getReturnPos();
	void worldinajar$setReturnPos(Vec3d returnPos);
}
