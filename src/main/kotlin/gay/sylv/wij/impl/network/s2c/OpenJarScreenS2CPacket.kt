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
package gay.sylv.wij.impl.network.s2c

import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.util.math.BlockPos

/**
 * Tells the client to open the jar screen.
 * @param pos The position of the [WorldJarBlockEntity]
 * @author sylv
 */
class OpenJarScreenS2CPacket(val pos: BlockPos) : Packet<ClientPlayPacketListener> {
	override fun write(buf: PacketByteBuf) {
		buf.writeBlockPos(pos)
	}
	
	override fun apply(listener: ClientPlayPacketListener?) {
	}
}
