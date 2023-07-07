package gay.sylv.wij.impl.network.c2s

import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity
import gay.sylv.wij.impl.network.s2c.JarWorldChunkUpdateS2CPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ServerPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.util.math.BlockPos

/**
 * This packet tells the server that the client has loaded the [WorldJarBlockEntity] and is ready to receive a [JarWorldChunkUpdateS2CPacket].
 *
 * **Yes, I know this is open to DDoS attacks. If you have a better solution, feel free to implement it your-fucking-self.**
 * @param pos The [BlockPos] of the [WorldJarBlockEntity].
 * @author sylv
 */
class WorldJarLoadedC2SPacket(private val pos: BlockPos) : Packet<ServerPlayPacketListener> {
	override fun write(buf: PacketByteBuf) {
		buf.writeBlockPos(pos)
	}
	
	override fun apply(listener: ServerPlayPacketListener?) {
	}
}
