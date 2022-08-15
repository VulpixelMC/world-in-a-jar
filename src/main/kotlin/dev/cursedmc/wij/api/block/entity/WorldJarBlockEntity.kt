package dev.cursedmc.wij.api.block.entity

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class WorldJarBlockEntity(
	blockPos: BlockPos?,
	blockState: BlockState?,
	val subworld: World? = null,
) : BlockEntity(BlockEntityTypes.WORLD_JAR, blockPos, blockState) {
	var magnitude: Int = DEFAULT_MAGNITUDE
	val scale: Float
		get() = 1.0f / magnitude
	var jarId = -1
	
	override fun readNbt(nbt: NbtCompound) {
		magnitude = nbt.getInt("scale")
		if (magnitude == 0) {
			magnitude = DEFAULT_MAGNITUDE
		}
	}
	
	override fun writeNbt(nbt: NbtCompound) {
		nbt.putInt("scale", magnitude)
	}
	
	companion object {
		const val DEFAULT_MAGNITUDE = 16
	}
}
