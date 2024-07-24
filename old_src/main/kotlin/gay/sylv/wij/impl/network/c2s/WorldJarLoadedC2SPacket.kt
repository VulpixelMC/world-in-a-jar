/**
 * World In a Jar
 * Copyright (C) 2023  Sylv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
