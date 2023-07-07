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
import gay.sylv.wij.impl.block.entity.render.JarChunk
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import org.quiltmc.loader.api.minecraft.ClientOnly
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking
import kotlin.math.ceil

/**
 * TODO: docs
 * @author sylv
 */
@ClientOnly
object S2CPackets : gay.sylv.wij.api.Initializable {
	internal val JAR_WORLD_BLOCK_UPDATE = id("jar_world_block_update")
	internal val JAR_WORLD_CHUNK_UPDATE = id("jar_world_chunk_update")
	
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
			if (entityOption!!.isEmpty) { // if the jar entity doesn't exist, return
				println("fake")
				return@registerGlobalReceiver
			}
			
			val entity = entityOption.get()
			entity.onChunkUpdate(client, blockStateContainer)
		}
	}
}
