/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package gay.sylv.wij.impl.block.entity

import gay.sylv.wij.impl.block.entity.render.JarChunk
import gay.sylv.wij.impl.block.entity.render.JarChunkRenderRegion
import gay.sylv.wij.impl.dimension.DimensionTypes
import gay.sylv.wij.impl.network.c2s.C2SPackets
import gay.sylv.wij.impl.network.c2s.WorldJarLoadedC2SPacket
import gay.sylv.wij.impl.network.s2c.S2CPackets
import gay.sylv.wij.impl.network.s2c.WorldJarLoadedS2CPacket
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.palette.PalettedContainer
import org.quiltmc.loader.api.minecraft.ClientOnly
import org.quiltmc.qkl.library.networking.playersTracking
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntity
import org.quiltmc.qsl.networking.api.PacketByteBufs
import org.quiltmc.qsl.networking.api.ServerPlayNetworking
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking

/**
 * A World Jar BlockEntity.
 *
 * ***Note**: You **must** call [WorldJarBlockEntity.setWorld] when overriding it.*
 */
class WorldJarBlockEntity(
	blockPos: BlockPos?,
	blockState: BlockState?,
) : BlockEntity(BlockEntityTypes.WORLD_JAR, blockPos, blockState), QuiltBlockEntity, gay.sylv.wij.api.BlockPosChecker {
	var magnitude: Int = -1
	val scale: Float
		get() = 1.0f / magnitude
	var subPos: BlockPos = BlockPos(0, -64, 0)
	var blockStates: Long2ObjectMap<BlockState> = Long2ObjectArrayMap() // this is likely slower. however, i'm doing this for now to save me headaches. TODO: move to PalettedContainers in each JarChunk.
	val blockEntities: Long2ObjectMap<BlockEntity> = Long2ObjectArrayMap()
	
	/**
	 * If the [BlockState]s in the jar have changed.
	 * This is used in rendering to determine whether we need to rebuild the VBOs.
	 */
	@ClientOnly
	internal var statesChanged = false
	
	/**
	 * [JarChunk]s that are being rendered.
	 */
	@ClientOnly
	val chunks: Long2ObjectMap<JarChunk> = Long2ObjectArrayMap()
	
	@ClientOnly
	val renderRegion: JarChunkRenderRegion = JarChunkRenderRegion(this, chunks)
	
	fun updateBlockStates(server: MinecraftServer) {
		val world = server.getWorld(DimensionTypes.WORLD_JAR_WORLD)!!
		val max = magnitude - 1
		for (x in 0..max) {
			for (y in 1..max) { // we move up one because the lowest layer is barrier blocks
				for (z in 0..max) {
					val pos = BlockPos(x, y, z)
					val state = world.getBlockState(pos.add(this.subPos))
					blockStates[pos.asLong()] = state
				}
			}
		}
	}
	
	override fun hasBlockPos(pos: BlockPos): Boolean {
		return pos.isWithinDistance(this.pos, magnitude.toDouble())
	}
	
	override fun setWorld(world: World) {
		super.setWorld(world)
		if (!world.isClient) {
			if (!INSTANCES.contains(this)) {
				INSTANCES.add(this)
			}
		} else if (MinecraftClient.getInstance().player?.world == world) {
			val buf = PacketByteBufs.create()
			val packet = WorldJarLoadedC2SPacket(pos)
			packet.write(buf)
			ClientPlayNetworking.send(C2SPackets.WORLD_JAR_LOADED, buf)
		}
	}
	
	override fun readNbt(nbt: NbtCompound) {
		super.readNbt(nbt)
		magnitude = nbt.getInt("magnitude")
		subPos = BlockPos.fromLong(nbt.getLong("pos"))
		
		if (magnitude > 16) {
			magnitude = 16
		}
		
		if (world?.isClient == true && MinecraftClient.getInstance().player?.world == world) {
			val buf = PacketByteBufs.create()
			val packet = WorldJarLoadedC2SPacket(pos)
			packet.write(buf)
			ClientPlayNetworking.send(C2SPackets.WORLD_JAR_LOADED, buf)
		}
	}
	
	override fun writeNbt(nbt: NbtCompound) {
		super.writeNbt(nbt)
		if (magnitude != -1) {
			nbt.putInt("magnitude", magnitude)
		}
		
		nbt.putLong("pos", subPos.asLong())
	}
	
	override fun toUpdatePacket(): Packet<ClientPlayPacketListener> {
		return BlockEntityUpdateS2CPacket.of(this)
	}
	
	override fun toSyncedNbt(): NbtCompound {
		return this.toNbt()
	}
	
	override fun markRemoved() {
		super.markRemoved()
		if (world?.isClient == false) {
			INSTANCES.remove(this)
		}
	}
	
	companion object {
		const val DEFAULT_MAGNITUDE = 16
		val INSTANCES: MutableList<WorldJarBlockEntity> = mutableListOf()
	}
}
