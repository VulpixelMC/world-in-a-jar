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
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.color.biome.BiomeColorProvider
import net.minecraft.client.color.world.BiomeColors
import net.minecraft.client.world.BiomeColorCache
import net.minecraft.client.world.ClientWorld
import net.minecraft.fluid.FluidState
import net.minecraft.util.Unit
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockRenderView
import net.minecraft.world.ChunkLightBlockView
import net.minecraft.world.chunk.light.LightingProvider
import org.quiltmc.loader.api.minecraft.ClientOnly

/**
 * A [BlockRenderView] for [JarChunkSection]s.
 * TODO: use the entity world's lighting
 * @author sylv
 */
@ClientOnly
class JarChunkRenderRegion(val entity: WorldJarBlockEntity, private val lightingProvider: JarLightingProvider) : BlockRenderView {
	/**
	 * A cache of biome colors for calculating biome color.
	 * @author sylv
	 */
	private val colorCache: Map<BiomeColorProvider, BiomeColorCache> = hashMapOf(
		Pair(BiomeColors.GRASS_COLOR, BiomeColorCache { calculateColor(it, BiomeColors.GRASS_COLOR) }),
		Pair(BiomeColors.FOLIAGE_COLOR, BiomeColorCache { calculateColor(it, BiomeColors.FOLIAGE_COLOR)}),
		Pair(BiomeColors.WATER_COLOR, BiomeColorCache { calculateColor(it, BiomeColors.WATER_COLOR)}),
	)
	
	override fun getBlockState(pos: BlockPos): BlockState {
		if (isOutside(pos)) {
			return Blocks.AIR.defaultState
		}
		return entity.getBlockState(pos)
	}
	
	override fun getFluidState(pos: BlockPos): FluidState {
		if (isOutside(pos)) {
			return Blocks.AIR.defaultState.fluidState
		}
		return entity.getBlockState(pos).fluidState
	}
	
	override fun getBlockEntity(pos: BlockPos): BlockEntity? {
		val chunkPos = ChunkSectionPos.from(pos)
		return entity.chunks[chunkPos.asLong()].getBlockEntity(pos)
	}
	
	override fun getBrightness(direction: Direction?, shaded: Boolean): Float {
		return entity.world?.getBrightness(direction, shaded)!!
	}
	
	override fun getColor(pos: BlockPos?, biomeColorProvider: BiomeColorProvider?): Int {
		return colorCache[biomeColorProvider]!!.getBiomeColor(pos)
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
		return x < 0 || x > entity.magnitude - 1
	}
	
	/**
	 * This calls [ClientWorld.calculateColor].
	 * @author sylv
	 */
	private fun calculateColor(pos: BlockPos, colorProvider: BiomeColorProvider): Int {
		val client = MinecraftClient.getInstance()
		return client.world!!.calculateColor(pos, colorProvider)
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
