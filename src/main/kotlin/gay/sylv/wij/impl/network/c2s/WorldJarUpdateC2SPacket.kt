/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package gay.sylv.wij.impl.network.c2s

import io.netty.buffer.ByteBufAllocator
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ServerPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.util.math.BlockPos
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity

/**
 * This packet tells the server to update the properties of a [WorldJarBlockEntity] to the given parameters.
 * @param pos The [BlockPos] of the target location to display in the [WorldJarBlockEntity].
 * @param magnitude The scale of the [WorldJarBlockEntity] that will be displayed.
 * @param entityPos The [BlockPos] of the [WorldJarBlockEntity].
 */
class WorldJarUpdateC2SPacket(private val pos: BlockPos, private val magnitude: Int, private val entityPos: BlockPos) : Packet<ServerPlayPacketListener> {
	override fun write(buf: PacketByteBuf) {
		buf.writeBlockPos(pos)
		buf.writeInt(magnitude)
		buf.writeBlockPos(entityPos)
	}
	
	override fun apply(listener: ServerPlayPacketListener) {
	}
	
	companion object {
		/**
		 * Remind me why I wrote this.
		 * @author sylv
		 */
		fun buf(pos: BlockPos, magnitude: Int, entityPos: BlockPos): PacketByteBuf {
			val byteBuf = ByteBufAllocator.DEFAULT.buffer()
			val buf = PacketByteBuf(byteBuf)
			
			buf.writeBlockPos(pos)
			buf.writeInt(magnitude)
			buf.writeBlockPos(entityPos)
			
			return buf
		}
	}
}
