/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package gay.sylv.wij.impl.network.s2c

import gay.sylv.wij.impl.WIJConstants.id
import gay.sylv.wij.impl.block.entity.BlockEntityTypes
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity
import gay.sylv.wij.impl.block.entity.render.JarChunk
import gay.sylv.wij.impl.network.c2s.C2SPackets
import gay.sylv.wij.impl.network.c2s.WorldJarLoadedC2SPacket
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import org.quiltmc.qsl.networking.api.PacketByteBufs
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking
import kotlin.math.ceil

object S2CPackets : gay.sylv.wij.api.Initializable {
	internal val JAR_WORLD_BLOCK_UPDATE = id("jar_world_block_update")
	@Suppress("MemberVisibilityCanBePrivate")
	internal val JAR_WORLD_CHUNK_UPDATE = id("jar_world_chunk_update")
	internal val WORLD_JAR_LOADED = id("world_jar_loaded")
	
	override fun initialize() {
		ClientPlayNetworking.registerGlobalReceiver(JAR_WORLD_BLOCK_UPDATE) {
				client, _, buf, _ ->
			val packet = JarWorldBlockUpdateS2CPacket(buf)
			val pos = packet.pos
			val jarPos = packet.jarPos
			val entityOption = client.world?.getBlockEntity(jarPos, BlockEntityTypes.WORLD_JAR) ?: return@registerGlobalReceiver
			if (entityOption.isEmpty) return@registerGlobalReceiver
			val entity = entityOption.get()
			
			entity.blockStates[pos.asLong()] = packet.state
			client.execute { entity.statesChanged = true }
		}
		ClientPlayNetworking.registerGlobalReceiver(JAR_WORLD_CHUNK_UPDATE) {
				client, _, buf, _ ->
			val packet = JarWorldChunkUpdateS2CPacket(buf)
			val pos = packet.pos
			val blockStateContainer = packet.blockStateContainer
			val entityOption = client.world?.getBlockEntity(pos, BlockEntityTypes.WORLD_JAR)
			if (entityOption!!.isEmpty) {
				return@registerGlobalReceiver
			}
			val entity = entityOption.get()
			
			entity.blockStates = Long2ObjectArrayMap()
			
			val max = entity.magnitude - 1
			for (x in 0..max) {
				for (y in 1..max) {
					for (z in 0..max) {
						val blockPos = BlockPos(x, y, z)
						val state = blockStateContainer.get(x, y, z)
						if (state.isAir) continue
						entity.blockStates[blockPos.asLong()] = state
					}
				}
			}
			
			// recreate chunks
			entity.chunks.clear()
			val max1 = ceil(entity.magnitude / 16f).toInt() // technically we include blocks that aren't in the magnitude since those are still parts of chunks
			val beginPos = BlockPos(0, 0, 0)
			val offset = BlockPos(max1, max1, max1)
			for (blockPos in BlockPos.iterate(beginPos, offset)) { // dirty hack
				val chunkPos = ChunkSectionPos.from(blockPos.x, blockPos.y, blockPos.z)
				client.execute { entity.chunks.put(chunkPos.asLong(), JarChunk(chunkPos)) }
			}
			
			client.execute { entity.statesChanged = true }
		}
	}
}
