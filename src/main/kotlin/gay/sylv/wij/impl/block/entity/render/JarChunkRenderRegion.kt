package gay.sylv.wij.impl.block.entity.render

import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity
import gay.sylv.wij.impl.generator.VoidChunkGenerator
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.color.biome.BiomeColorProvider
import net.minecraft.client.world.BiomeColorCache
import net.minecraft.fluid.FluidState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockRenderView
import net.minecraft.world.chunk.light.LightingProvider

/**
 * A [BlockRenderView] for [JarChunk]s.
 */
class JarChunkRenderRegion(val entity: WorldJarBlockEntity, val chunks: Short2ObjectMap<JarChunk>) : BlockRenderView {
	override fun getBlockState(pos: BlockPos): BlockState {
		return entity.blockStates.get(pos.x, pos.y, pos.z)
	}
	
	override fun getFluidState(pos: BlockPos): FluidState {
		return entity.blockStates.get(pos.x, pos.y, pos.z).fluidState
	}
	
	override fun getBlockEntity(pos: BlockPos): BlockEntity? {
		return entity.blockEntities[pos.asLong()]
	}
	
	override fun getBrightness(direction: Direction?, shaded: Boolean): Float {
		return entity.world?.getBrightness(direction, shaded)!!
	}
	
	override fun getColor(pos: BlockPos?, biomeColorProvider: BiomeColorProvider?): Int {
		return biomeColorCache.getBiomeColor(pos)
	}
	
	override fun getHeight(): Int {
		return VoidChunkGenerator.HEIGHT
	}
	
	override fun getBottomY(): Int {
		return VoidChunkGenerator.MINIMUM_Y
	}
	
	override fun getLightingProvider(): LightingProvider? {
		return null
	}
	
	companion object {
		/**
		 * A dummy BiomeColorCache.
		 */
		val biomeColorCache: BiomeColorCache = BiomeColorCache {
			return@BiomeColorCache 0
		}
	}
}
