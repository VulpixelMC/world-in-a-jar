package dev.cursedmc.wij.api

import net.minecraft.util.math.BlockPos

/**
 * Checks if this object has the specified [net.minecraft.util.math.BlockPos].
 */
interface BlockPosChecker {
	fun hasBlockPos(pos: BlockPos): Boolean
}
