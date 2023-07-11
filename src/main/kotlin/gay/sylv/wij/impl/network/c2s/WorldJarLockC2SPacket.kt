package gay.sylv.wij.impl.network.c2s

import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ServerPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.util.math.BlockPos

/**
 * Tells the server that we want to lock/unlock a jar.
 * @param pos The [BlockPos] of the jar.
 * @param locked Whether the jar is locked.
 * @author sylv
 */
class WorldJarLockC2SPacket(private val pos: BlockPos, private val locked: Boolean) : Packet<ServerPlayPacketListener> {
	override fun write(buf: PacketByteBuf) {
		buf.writeBlockPos(pos)
		buf.writeBoolean(locked)
	}
	
	override fun apply(listener: ServerPlayPacketListener?) {
	}
}
