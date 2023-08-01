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
package gay.sylv.wij.impl.block.entity.render

import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.fluid.FluidState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.ChunkLightBlockView
import net.minecraft.world.chunk.light.ChunkSkyLightSources
import java.util.function.BiConsumer

/**
 * A full version of [JarChunkSection]. This is used in lighting.
 * @param offset The position of the chunk.
 * @author sylv
 */
class JarChunk(private val offset: ChunkPos, private val entity: WorldJarBlockEntity) : ChunkLightBlockView {
	private val blockEntities: Long2ObjectMap<BlockEntity> = Long2ObjectOpenHashMap() // TODO: implement BEs and BERs
	
	override fun getHeight(): Int {
		return entity.scale
	}
	
	override fun getBottomY(): Int {
		return 0
	}
	
	override fun getBlockEntity(pos: BlockPos): BlockEntity? {
		return blockEntities[pos.asLong()]
	}
	
	override fun getBlockState(pos: BlockPos): BlockState {
		val chunkPos = ChunkSectionPos.from(offset.x, ChunkSectionPos.getSectionCoord(pos.y), offset.z)
		return entity.chunkSections[chunkPos.asLong()].blockStates[pos.x.and(15), pos.y.and(15), pos.z.and(15)]
	}
	
	override fun getFluidState(pos: BlockPos): FluidState {
		return getBlockState(pos).fluidState
	}
	
	@OptIn(ExperimentalStdlibApi::class)
	override fun findBlockLightSources(callback: BiConsumer<BlockPos, BlockState>) {
		val illuminates: (BlockState) -> Boolean = { state: BlockState -> state.luminance != 0 }
		val blockPos = BlockPos.Mutable()
		for (i in 0..<entity.getChunkDiameter()) {
			val section = getSection(i)
			if (section.hasAny(illuminates)) {
				for (x in 0..15) {
					for (y in 0..15) {
						for (z in 0..15) {
							val state = section.getBlockState(x, y, z)
							if (illuminates.invoke(state)) {
								callback.accept(blockPos.set(section.origin, x, y, z), state)
							}
						}
					}
				}
			}
		}
	}
	
	override fun getSkyLightSources(): ChunkSkyLightSources? {
		return null
	}
	
	/**
	 * @return the [JarChunkSection] at the given Y-coordinate.
	 */
	private fun getSection(y: Int): JarChunkSection {
		val chunkPos = ChunkSectionPos.from(offset.x, y, offset.z)
		return entity.chunkSections[chunkPos.asLong()]
	}
}
