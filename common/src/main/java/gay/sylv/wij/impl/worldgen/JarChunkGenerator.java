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
package gay.sylv.wij.impl.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JarChunkGenerator extends ChunkGenerator {
	public static final MapCodec<JarChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					BiomeSource.CODEC
							.fieldOf("biome_source")
							.forGetter(JarChunkGenerator::biomeSource)
			).apply(instance, JarChunkGenerator::new)
	);
	
	private final BiomeSource biomeSource;
	
	public JarChunkGenerator(BiomeSource biomeSource) {
		super(biomeSource, JarChunkGenerator::jarGenSettings);
		this.biomeSource = biomeSource;
	}
	
	public BiomeSource biomeSource() {
		return biomeSource;
	}
	
	/**
	 * @return Jar generation settings.
	 */
	private static BiomeGenerationSettings jarGenSettings(Holder<Biome> biome) {
		return BiomeGenerationSettings.EMPTY;
	}
	
	@Override
	protected @NotNull MapCodec<? extends ChunkGenerator> codec() {
		return CODEC;
	}
	
	@Override
	public void applyCarvers(
			WorldGenRegion level,
			long seed,
			RandomState random,
			BiomeManager biomeManager,
			StructureManager structureManager,
			ChunkAccess chunk,
			GenerationStep.Carving step
	) {
		/* no-op */
	}
	
	@Override
	public void buildSurface(WorldGenRegion level, StructureManager structureManager, RandomState random, ChunkAccess chunk) {
		/* no-op */
	}
	
	@Override
	public void spawnOriginalMobs(WorldGenRegion level) {
		/* no-op */
	}
	
	@Override
	public int getGenDepth() {
		return 320;
	}
	
	@Override
	public @NotNull CompletableFuture<ChunkAccess> fillFromNoise(
			Blender blender,
			RandomState randomState,
			StructureManager structureManager,
			ChunkAccess chunk
	) {
		return CompletableFuture.completedFuture(chunk);
	}
	
	@Override
	public int getSeaLevel() {
		return 0;
	}
	
	@Override
	public int getMinY() {
		return -64;
	}
	
	@Override
	public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
		return 320;
	}
	
	@Override
	public @NotNull NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
		return new NoiseColumn(0, new BlockState[]{});
	}
	
	@Override
	public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {
		/* no-op */
	}
}
