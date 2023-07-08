/**
 * World In a Jar
 * Copyright (C) 2023  Sylv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package gay.sylv.wij.impl.network.c2s

import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity
import io.netty.buffer.ByteBufAllocator
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ServerPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.util.math.BlockPos

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
