/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package dev.cursedmc.wij.generator

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.Blocks
import net.minecraft.registry.Holder
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryOps
import net.minecraft.structure.StructureManager
import net.minecraft.util.math.BlockPos
import net.minecraft.world.ChunkRegion
import net.minecraft.world.HeightLimitView
import net.minecraft.world.Heightmap
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.Biomes
import net.minecraft.world.biome.source.BiomeAccess
import net.minecraft.world.biome.source.FixedBiomeSource
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.RandomState
import net.minecraft.world.gen.chunk.Blender
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.chunk.VerticalBlockSample
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Function

class VoidChunkGenerator(biome: Holder.Reference<Biome>) :
	ChunkGenerator(FixedBiomeSource(biome)) {
	override fun getCodec(): Codec<out ChunkGenerator> {
		return CODEC
	}
	
	override fun carve(
		chunkRegion: ChunkRegion?,
		seed: Long,
		randomState: RandomState?,
		biomeAccess: BiomeAccess?,
		structureManager: StructureManager?,
		chunk: Chunk?,
		generationStep: GenerationStep.Carver?
	) {
	}
	
	override fun buildSurface(
		region: ChunkRegion?,
		structureManager: StructureManager?,
		randomState: RandomState?,
		chunk: Chunk?
	) {
	}
	
	override fun populateEntities(region: ChunkRegion?) {
	}
	
	override fun getWorldHeight(): Int {
		return 320
	}
	
	override fun populateNoise(
		executor: Executor?,
		blender: Blender?,
		randomState: RandomState?,
		structureManager: StructureManager?,
		chunk: Chunk?
	): CompletableFuture<Chunk> {
		for (x in 0..16) {
			for (z in 0..16) {
				chunk?.setBlockState(BlockPos(chunk.pos.startX + x, chunk.bottomY, chunk.pos.startZ + z), Blocks.BARRIER.defaultState, false)
			}
		}
		
		return CompletableFuture.completedFuture(chunk)
	}
	
	override fun getSeaLevel(): Int {
		return 0
	}
	
	override fun getMinimumY(): Int {
		return -64
	}
	
	override fun getHeight(
		x: Int,
		z: Int,
		heightmap: Heightmap.Type?,
		world: HeightLimitView?,
		randomState: RandomState?
	): Int {
		return 320
	}
	
	override fun getColumnSample(
		x: Int,
		z: Int,
		world: HeightLimitView?,
		randomState: RandomState?
	): VerticalBlockSample {
		return VerticalBlockSample(0, arrayOfNulls(0))
	}
	
	override fun method_40450(list: MutableList<String>?, randomState: RandomState?, pos: BlockPos?) {
	}
	
	companion object {
		private val BIOME: RegistryKey<Biome> = Biomes.THE_VOID
		
		@JvmField
		val CODEC: Codec<VoidChunkGenerator> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<VoidChunkGenerator> ->
			instance.group(RegistryOps.retrieveElement(BIOME)).apply(
				instance, instance.stable(
					Function { reference: Holder.Reference<Biome> ->
						VoidChunkGenerator(
							reference
						)
					})
			)
		}
	}
}
