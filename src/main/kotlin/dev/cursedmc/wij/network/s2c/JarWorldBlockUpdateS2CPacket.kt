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
import net.minecraft.network.packet.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.util.math.BlockPos

class JarWorldBlockUpdateS2CPacket(val pos: BlockPos, val state: BlockState, val jarPos: BlockPos) : Packet<ClientPlayPacketListener> {
	constructor(buf: PacketByteBuf) : this(buf.readBlockPos(), Block.STATE_IDS.get(buf.readInt())!!, buf.readBlockPos())
	
	override fun write(buf: PacketByteBuf) {
		buf.writeBlockPos(pos)
		buf.writeInt(Block.STATE_IDS.getRawId(state))
		buf.writeBlockPos(jarPos)
	}
	
	override fun apply(listener: ClientPlayPacketListener) {
	}
}
