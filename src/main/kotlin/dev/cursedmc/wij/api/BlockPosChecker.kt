/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package dev.cursedmc.wij.api

import net.minecraft.util.math.BlockPos

/**
 * Checks if this object has the specified [net.minecraft.util.math.BlockPos].
 */
interface BlockPosChecker {
	fun hasBlockPos(pos: BlockPos): Boolean
}
