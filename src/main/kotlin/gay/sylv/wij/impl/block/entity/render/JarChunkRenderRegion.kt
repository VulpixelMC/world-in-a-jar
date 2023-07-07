package gay.sylv.wij.impl.block.entity.render

import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.color.biome.BiomeColorProvider
import net.minecraft.client.world.BiomeColorCache
import net.minecraft.fluid.FluidState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockRenderView
import net.minecraft.world.chunk.light.LightingProvider
import org.quiltmc.loader.api.minecraft.ClientOnly

/**
 * A [BlockRenderView] for [JarChunk]s.
 * TODO: use the entity world's lighting
 */
@ClientOnly
class JarChunkRenderRegion(val entity: WorldJarBlockEntity, private val chunks: Long2ObjectMap<JarChunk>) : BlockRenderView {
	override fun getBlockState(pos: BlockPos): BlockState {
		if (isOutside(pos)) {
			return Blocks.AIR.defaultState
		}
//		val chunkPos = ChunkSectionPos.from(pos)
//		return chunks[chunkPos.asLong()]?.blockStates?.get(pos.x, pos.y, pos.z)!!
		return entity.blockStates[pos.asLong()] ?: Blocks.AIR.defaultState
	}
	
	override fun getFluidState(pos: BlockPos): FluidState {
		if (isOutside(pos)) {
			return Blocks.AIR.defaultState.fluidState
		}
		return entity.blockStates[pos.asLong()].fluidState
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
		return entity.magnitude
	}
	
	override fun getBottomY(): Int {
		return 0
	}
	
	override fun getLightingProvider(): LightingProvider? {
		return entity.world?.lightingProvider
	}
	
	/**
	 * Determines if the [BlockPos] is outside the range of the magnitude.
	 */
	private fun isOutside(blockPos: BlockPos): Boolean {
		return isOutsideInt(blockPos.x) || isOutsideInt(blockPos.y) || isOutsideInt(blockPos.z)
	}
	
	/**
	 * Determines if the [Int] is outside the range of the magnitude.
	 */
	private fun isOutsideInt(x: Int): Boolean {
		return x < 0 || x > entity.magnitude
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
