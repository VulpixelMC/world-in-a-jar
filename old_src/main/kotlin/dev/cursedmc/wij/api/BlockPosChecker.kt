/**
 * World In a Jar
 * Copyright (C) 2023  Sylv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package dev.cursedmc.wij.api

import net.minecraft.util.math.BlockPos

/**
 * Checks if this object has the specified [net.minecraft.util.math.BlockPos].
 * @see gay.sylv.wij.api.BlockPosChecker
 */
@Suppress("unused")
@Deprecated(message = "Moved to gay.sylv.wij.api", level = DeprecationLevel.ERROR)
interface BlockPosChecker {
	fun hasBlockPos(pos: BlockPos): Boolean
}
