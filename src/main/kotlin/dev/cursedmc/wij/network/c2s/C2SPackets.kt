/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package dev.cursedmc.wij.network.c2s

import dev.cursedmc.wij.api.Initializable
import dev.cursedmc.wij.impl.duck.PlayerWithReturnDim
import dev.cursedmc.wij.impl.duck.PlayerWithReturnPos
import dev.cursedmc.wij.block.entity.BlockEntityTypes
import dev.cursedmc.wij.dimension.DimensionTypes
import dev.cursedmc.wij.network.s2c.JarWorldChunkUpdateS2CPacket
import dev.cursedmc.wij.network.s2c.S2CPackets
import dev.cursedmc.wij.impl.WIJConstants.id
import dev.cursedmc.wij.impl.toPalettedContainer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d
import net.minecraft.world.TeleportTarget
import org.quiltmc.qkl.library.networking.playersTracking
import org.quiltmc.qsl.networking.api.PacketByteBufs
import org.quiltmc.qsl.networking.api.ServerPlayNetworking
import org.quiltmc.qsl.worldgen.dimension.api.QuiltDimensions

object C2SPackets : Initializable {
	internal val WORLD_JAR_ENTER = id("world_jar_enter")
	internal val WORLD_JAR_LOADED = id("world_jar_loaded_c2s")
	internal val WORLD_JAR_UPDATE = id("world_jar_update")
	
	@Suppress("NAME_SHADOWING")
	override fun initialize() {
		ServerPlayNetworking.registerGlobalReceiver(WORLD_JAR_ENTER) {
				server, player, _, buf, _ ->
			val packet = WorldJarEnterC2SPacket(buf)
			val pos = packet.pos
			
			server.execute {
				val world = player.world
				val entityOption = world.getBlockEntity(pos, BlockEntityTypes.WORLD_JAR)
				if (entityOption.isEmpty) return@execute
				val entity = entityOption.get()
				
				val jarWorld = server.getWorld(DimensionTypes.WORLD_JAR_WORLD)
				
				val returnPos = player as PlayerWithReturnPos
				val returnDim = player as PlayerWithReturnDim
				returnPos.`worldinajar$setReturnPos`(player.pos)
				returnDim.`worldinajar$setReturnDim`(player.world.registryKey)
				
				QuiltDimensions.teleport<ServerPlayerEntity>(player, jarWorld, TeleportTarget(Vec3d.of(entity.subPos.add(0, 1, 0)), Vec3d.ZERO, 0f, 0f))
			}
		}
		ServerPlayNetworking.registerGlobalReceiver(WORLD_JAR_LOADED) {
				server, player, _, buf, _ ->
			val packet = WorldJarLoadedC2SPacket(buf)
			val pos = packet.pos
			
			server.execute {
				val world = player.world
				val entityOption = world.getBlockEntity(pos, BlockEntityTypes.WORLD_JAR)
				if (entityOption.isEmpty) return@execute
				val entity = entityOption.get()
				
				world.server?.let { entity.updateBlockStates(it) }
				
				for (player in entity.playersTracking) {
					val buf = PacketByteBufs.create()
					val packet = JarWorldChunkUpdateS2CPacket(pos, entity.blockStates.toPalettedContainer())
					packet.write(buf)
					ServerPlayNetworking.send(player, S2CPackets.JAR_WORLD_CHUNK_UPDATE, buf)
				}
			}
		}
		ServerPlayNetworking.registerGlobalReceiver(WORLD_JAR_UPDATE) {
				server, player, _, buf, _ ->
			val pos = buf.readBlockPos()
			val magnitude = buf.readInt()
			val entityPos = buf.readBlockPos()
			
			server.execute {
				if (player.world.getBlockEntity(entityPos)?.type == BlockEntityTypes.WORLD_JAR) {
					val entity = player.world.getBlockEntity(entityPos, BlockEntityTypes.WORLD_JAR).get()
					entity.subPos = pos.mutableCopy()
					entity.magnitude = magnitude
					entity.markDirty()
					entity.sync()
				}
			}
		}
	}
}
