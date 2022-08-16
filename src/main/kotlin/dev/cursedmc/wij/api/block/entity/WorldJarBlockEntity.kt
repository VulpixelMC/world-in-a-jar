package dev.cursedmc.wij.api.block.entity

import dev.cursedmc.wij.api.WorldContainer
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class WorldJarBlockEntity(
	blockPos: BlockPos?,
	blockState: BlockState?,
	val subworld: World? = null,
) : BlockEntity(BlockEntityTypes.WORLD_JAR, blockPos, blockState), WorldContainer {
	var magnitude: Int = -1
	val scale: Float
		get() = 1.0f / magnitude
	var x = 0
	var y = 0
	var z = 0
	
	override fun readNbt(nbt: NbtCompound) {
		magnitude = nbt.getInt("magnitude")
		x = nbt.getInt("x")
		y = nbt.getInt("y")
		z = nbt.getInt("z")
	}
	
	override fun writeNbt(nbt: NbtCompound) {
		super.writeNbt(nbt)
		if (magnitude != -1) {
			nbt.putInt("magnitude", magnitude)
		}
		
		nbt.putInt("x", x)
		nbt.putInt("y", y)
		nbt.putInt("z", z)
	}
	
	override fun getWorld(): World? {
		return this.subworld
	}
	
	companion object {
		const val DEFAULT_MAGNITUDE = 16
	}
}
