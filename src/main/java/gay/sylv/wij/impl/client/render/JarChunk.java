/**
 * World In a Jar
 * Copyright (C) 2024  VulpixelMC
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package gay.sylv.wij.impl.client.render;

import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * A full version of {@link JarLevelChunkSection}. This is used in lighting.
 * @author sylv
 */
public class JarChunk implements LightChunk {
	private final ChunkPos offset;
	private final WorldJarBlockEntity jar;
	private final Long2ObjectMap<BlockEntity> blockEntities = new Long2ObjectOpenHashMap<>(); // TODO: implement BEs and BERs
	private final ChunkSkyLightSources chunkSkyLightSources;
	
	/**
	 * @param offset The position of the chunk.
	 */
	public JarChunk(ChunkPos offset, WorldJarBlockEntity jar) {
		this.offset = offset;
		this.jar = jar;
		this.chunkSkyLightSources = new ChunkSkyLightSources(this);
	}
	
	@Override
	public int getHeight() {
		return jar.getScale();
	}
	
	@Override
	public int getMinBuildHeight() {
		return 0;
	}
	
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return blockEntities.get(pos.asLong());
	}
	
	@Override
	public @NotNull BlockState getBlockState(BlockPos pos) {
		SectionPos chunkPos = SectionPos.of(offset.x, SectionPos.posToSectionCoord(pos.getY()), offset.z);
		return jar.getChunkSections().get(chunkPos.asLong()).getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
	}
	
	@Override
	public @NotNull FluidState getFluidState(BlockPos pos) {
		return getBlockState(pos).getFluidState();
	}
	
	@Override
	public void findBlockLightSources(BiConsumer<BlockPos, BlockState> callback) {
		java.util.function.Predicate<BlockState> illuminates = state -> state.getLightEmission() != 0;
		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
		for (int i = 0; i < jar.getChunkDiameter(); i++) {
			JarLevelChunkSection section = getSection(i);
			if (section.maybeHas(illuminates)) {
				for (int x = 0; x <= 15; x++) {
					for (int y = 0; y <= 15; y++) {
						for (int z = 0; z <= 15; z++) {
							BlockState state = section.getBlockState(x, y, z);
							if (illuminates.test(state)) {
								callback.accept(blockPos.setWithOffset(section.getOrigin(), x, y, z), state);
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public @NotNull ChunkSkyLightSources getSkyLightSources() {
		return chunkSkyLightSources;
	}
	
	private JarLevelChunkSection getSection(int y) {
		SectionPos chunkPos = SectionPos.of(offset.x, y, offset.z);
		return jar.getChunkSections().get(chunkPos.asLong());
	}
}


