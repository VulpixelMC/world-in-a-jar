package dev.cursedmc.wij.api.network.c2s

import net.minecraft.network.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ServerPlayPacketListener
import net.minecraft.util.math.BlockPos

class WorldJarUpdateC2SPacket(private val pos: BlockPos, private val scale: Int) : Packet<ServerPlayPacketListener> {
	override fun write(buf: PacketByteBuf) {
		buf.writeBlockPos(pos)
		buf.writeInt(scale)
	}
	
	override fun apply(listener: ServerPlayPacketListener) {
		TODO("Not yet implemented")
	}
}
