/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package dev.cursedmc.wij.network.s2c

import net.minecraft.network.packet.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.util.math.BlockPos

class WorldJarLoadedS2CPacket(val pos: BlockPos) : Packet<ClientPlayPacketListener> {
	constructor(buf: PacketByteBuf) : this(buf.readBlockPos())
	
	override fun write(buf: PacketByteBuf) {
		buf.writeBlockPos(pos)
	}
	
	override fun apply(listener: ClientPlayPacketListener) {
	}
}
