package dev.cursedmc.wij.impl

import dev.cursedmc.wij.impl.mixin.IdCountsStateAccessor
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IdCountsState
import net.minecraft.world.World
import net.minecraft.world.chunk.palette.PalettedContainer
import kotlin.reflect.KClass

fun MatrixStack.scale(scale: Float) {
	this.scale(scale, scale, scale)
}

@Suppress("UnusedReceiverParameter")
fun KClass<BlockPos.Mutable>.fromLong(packedPos: Long): BlockPos.Mutable {
	return BlockPos.Mutable(BlockPos.unpackLongX(packedPos), BlockPos.unpackLongY(packedPos), BlockPos.unpackLongZ(packedPos))
}

fun Long.toBlockPos(): BlockPos {
	return BlockPos.fromLong(this)
}

fun Long2ObjectMap<BlockState>.toPalettedContainer(): PalettedContainer<BlockState> {
	val blockStateContainer = PalettedContainer(Block.STATE_IDS, Blocks.AIR.defaultState, PalettedContainer.PaletteProvider.BLOCK_STATE)
	this.forEach {
			(posLong, state) ->
		val pos = posLong.toBlockPos()
		blockStateContainer.set(pos.x, pos.y, pos.z, state)
	}
	return blockStateContainer
}

fun nextJarId(world: World): Int {
	val idCounts = world.server?.overworld?.persistentStateManager?.getOrCreate(IdCountsState::fromNbt, ::IdCountsState, "idcounts")!!
	idCounts as IdCountsStateAccessor
	val id = idCounts.idCounts.getInt("jar") + 1
	idCounts.idCounts["jar"] = id
	idCounts.markDirty()
	return id
}
