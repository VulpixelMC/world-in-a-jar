/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package dev.cursedmc.wij.network.s2c

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.network.packet.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.palette.PalettedContainer

/**
 * Signals that a chunk (entire size of jar) has updated. This sends the [BlockPos] of the jar as well as the [PalettedContainer]<[BlockState]> of the chunk.
 */
class JarWorldChunkUpdateS2CPacket(val pos: BlockPos, val blockStateContainer: PalettedContainer<BlockState>) : Packet<ClientPlayPacketListener> {
	constructor(buf: PacketByteBuf) : this(buf.readBlockPos(), buf.run {
		val container = PalettedContainer(Block.STATE_IDS, Blocks.AIR.defaultState, PalettedContainer.PaletteProvider.BLOCK_STATE)
		container.readPacket(this@run)
		container
	})
	
	override fun write(buf: PacketByteBuf) {
		buf.writeBlockPos(pos)
		blockStateContainer.writePacket(buf)
	}
	
	override fun apply(listener: ClientPlayPacketListener) {
	}
}
