/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package dev.cursedmc.wij.api.network.s2c

import dev.cursedmc.wij.api.Initializable
import dev.cursedmc.wij.api.block.entity.BlockEntityTypes
import dev.cursedmc.wij.api.network.c2s.C2SPackets
import dev.cursedmc.wij.api.network.c2s.WorldJarLoadedC2SPacket
import dev.cursedmc.wij.impl.WIJConstants.id
import dev.cursedmc.wij.impl.WorldInAJar
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import org.quiltmc.qsl.networking.api.PacketByteBufs
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking

object S2CPackets : Initializable {
	internal val JAR_WORLD_BLOCK_UPDATE = id("jar_world_block_update")
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
						val state = blockStateContainer.method_12321(x, y, z)
						if (state.isAir) continue
						entity.blockStates[blockPos.asLong()] = state
					}
				}
			}
		}
		ClientPlayNetworking.registerGlobalReceiver(WORLD_JAR_LOADED) {
				_, _, recBuf, _ ->
			val recPacket = WorldJarLoadedS2CPacket(recBuf)
			val buf = PacketByteBufs.create()
			val packet = WorldJarLoadedC2SPacket(recPacket.pos)
			packet.write(buf)
			try {
				ClientPlayNetworking.send(C2SPackets.WORLD_JAR_LOADED, buf)
			} catch(_: Exception) {}
		}
	}
}
