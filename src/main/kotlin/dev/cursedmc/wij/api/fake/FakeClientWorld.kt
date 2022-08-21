package dev.cursedmc.wij.api.fake

import dev.cursedmc.wij.api.block.entity.render.WorldJarBlockEntityRenderer
import dev.cursedmc.wij.api.dimension.DimensionTypes
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.registry.Registry

class FakeClientWorld : ClientWorld(MinecraftClient.getInstance().networkHandler, MinecraftClient.getInstance().world?.levelProperties, DimensionTypes.WORLD_JAR_WORLD, MinecraftClient.getInstance().world?.registryManager?.get(Registry.DIMENSION_TYPE_KEY)?.getHolderOrThrow(DimensionTypes.WORLD_JAR_TYPE_KEY), MinecraftClient.getInstance().world?.simulationDistance!!, MinecraftClient.getInstance().world?.simulationDistance!!, MinecraftClient.getInstance()::getProfiler, MinecraftClient.getInstance().worldRenderer, false, 0) {
	override fun getBlockState(pos: BlockPos): BlockState {
		return if (this.isOutOfHeightLimit(pos)) {
			Blocks.VOID_AIR.defaultState
		} else {
			val worldChunk = this.getChunk(ChunkSectionPos.getSectionCoord(pos.x), ChunkSectionPos.getSectionCoord(pos.z))
			var empty = true
			if (!worldChunk.getBlockState(pos).isAir) {
				empty = false
			}
			if (empty) {
				worldChunk.loadToWorld()
			}
			worldChunk.getBlockState(pos)
		}
	}
}
