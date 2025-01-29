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
import gay.sylv.wij.impl.util.Conversions;
import gay.sylv.wij.impl.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A {@link net.minecraft.world.level.BlockAndTintGetter} for {@link JarLevelChunkSection}s.
 * TODO: use the entity world's lighting
 * @author sylv
 */
public class JarRenderChunkRegion implements BlockAndTintGetter {
	/**
	 * A cache of biome colors for calculating biome color.
	 */
	private final Map<ColorResolver, BlockTintCache> tintCache = createTintCache(BiomeColors.GRASS_COLOR_RESOLVER, BiomeColors.FOLIAGE_COLOR_RESOLVER, BiomeColors.WATER_COLOR_RESOLVER);
	private final WorldJarBlockEntity jar;
	private final JarLevelLightEngine lightEngine;
	
	public JarRenderChunkRegion(WorldJarBlockEntity jar, JarLevelLightEngine lightEngine) {
		this.jar = jar;
		this.lightEngine = lightEngine;
	}
	
	private Map<ColorResolver, BlockTintCache> createTintCache(ColorResolver... resolver) {
		var map = new HashMap<ColorResolver, BlockTintCache>();
		Arrays.stream(resolver)
				.map(x -> Pair.of(x, new BlockTintCache(pos -> calculateColor(pos, x))))
				.forEach(pair -> map.put(pair.first(), pair.second()));
		return map;
	}
	
	@Override
	public float getShade(Direction direction, boolean shade) {
		return Objects.requireNonNull(jar.getLevel()).getShade(direction, shade);
	}
	
	@Override
	public @NotNull LevelLightEngine getLightEngine() {
		return lightEngine;
	}
	
	@Override
	public int getBlockTint(BlockPos pos, ColorResolver colorResolver) {
		return tintCache.get(colorResolver).getColor(pos);
	}
	
	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		ChunkPos chunkPos = Conversions.convert(pos);
		return jar.getChunk(chunkPos.x, chunkPos.z).getBlockEntity(pos);
	}
	
	@Override
	public @NotNull BlockState getBlockState(BlockPos pos) {
		return jar.getBlockState(pos);
	}
	
	@Override
	public @NotNull FluidState getFluidState(BlockPos pos) {
		return jar.getFluidState(pos);
	}
	
	@Override
	public int getHeight() {
		return jar.getScale();
	}
	
	@Override
	public int getMinBuildHeight() {
		return -64;
	}
	
	/**
	 * This calls {@link ClientLevel#calculateBlockTint}.
	 * @author sylv
	 */
	private int calculateColor(BlockPos pos, ColorResolver colorProvider) {
		var client = Minecraft.getInstance();
		assert client.level != null;
		return client.level.calculateBlockTint(pos, colorProvider);
	}
}
