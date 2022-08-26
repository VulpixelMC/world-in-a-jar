package dev.cursedmc.wij.api.network.c2s

import net.minecraft.network.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ServerPlayPacketListener
import net.minecraft.util.math.BlockPos

class WorldJarEnterC2SPacket(val pos: BlockPos) : Packet<ServerPlayPacketListener> {
	constructor(buf: PacketByteBuf) : this(buf.readBlockPos())
	
	override fun write(buf: PacketByteBuf) {
		buf.writeBlockPos(pos)
	}
	
	override fun apply(listener: ServerPlayPacketListener) {
	}
}
