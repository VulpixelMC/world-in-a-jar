/**
 * World In a Jar
 * Copyright (C) 2023  Sylv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockRenderView
import net.minecraft.world.ChunkLightBlockView
import net.minecraft.world.chunk.light.ChunkSkyLightSources
import net.minecraft.world.chunk.light.LightingProvider
import org.quiltmc.loader.api.minecraft.ClientOnly
import java.util.function.BiConsumer
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * A [BlockRenderView] for [JarChunk]s.
 * TODO: use the entity world's lighting
 * TODO: move the [ChunkLightBlockView] so it's both server-side and client-side
 * @author sylv
 */
@ClientOnly
class JarChunkRenderRegion(val entity: WorldJarBlockEntity, private val chunks: Long2ObjectMap<JarChunk>, private val lightingProvider: JarLightingProvider) : BlockRenderView, ChunkLightBlockView {
	override fun getBlockState(pos: BlockPos): BlockState {
		if (isOutside(pos)) {
			return Blocks.AIR.defaultState
		}
		val chunkPos = ChunkSectionPos.from(pos)
		return chunks[chunkPos.asLong()]?.blockStates?.get(pos.x, pos.y, pos.z)!!
	}
	
	override fun getFluidState(pos: BlockPos): FluidState {
		if (isOutside(pos)) {
			return Blocks.AIR.defaultState.fluidState
		}
		val chunkPos = ChunkSectionPos.from(pos)
		return chunks[chunkPos.asLong()]?.blockStates?.get(pos.x, pos.y, pos.z)?.fluidState!!
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
	
	override fun getLightingProvider(): LightingProvider {
		return lightingProvider
	}
	
	@OptIn(ExperimentalStdlibApi::class)
	override fun findBlockLightSources(callback: BiConsumer<BlockPos, BlockState>?) {
		val blockPos = BlockPos.Mutable()
		for (i in 0..<entity.getChunkHeight()) {
			val chunkPos =
		}
	}
	
	override fun getSkyLightSources(): ChunkSkyLightSources {
		TODO("Not yet implemented")
	}
	
	/**
	 * Determines if the [BlockPos] is outside the range of the magnitude.
	 * @author sylv
	 */
	private fun isOutside(blockPos: BlockPos): Boolean {
		return isOutsideInt(blockPos.x) || isOutsideInt(blockPos.y) || isOutsideInt(blockPos.z)
	}
	
	/**
	 * Determines if the [Int] is outside the range of the magnitude.
	 * @author sylv
	 */
	private fun isOutsideInt(x: Int): Boolean {
		return x < 0 || x > entity.magnitude
	}
	
	companion object {
		/**
		 * A dummy BiomeColorCache.
		 * @author sylv
		 */
		val biomeColorCache: BiomeColorCache = BiomeColorCache {
			return@BiomeColorCache 0
		}
	}
}
