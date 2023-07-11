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
