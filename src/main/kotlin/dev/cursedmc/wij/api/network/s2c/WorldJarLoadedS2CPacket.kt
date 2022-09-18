package dev.cursedmc.wij.api.network.s2c

import net.minecraft.network.Packet
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
