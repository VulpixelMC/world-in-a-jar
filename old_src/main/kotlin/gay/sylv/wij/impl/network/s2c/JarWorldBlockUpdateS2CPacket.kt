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
package gay.sylv.wij.impl.network.s2c

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.util.math.BlockPos

/**
 * Signals that a block has updated. This sends the [BlockPos] and [BlockState] of the block as well as the [BlockPos] of the jar.
 */
class JarWorldBlockUpdateS2CPacket(val pos: BlockPos, val state: BlockState, val jarPos: BlockPos) : Packet<ClientPlayPacketListener> {
	constructor(buf: PacketByteBuf) : this(buf.readBlockPos(), Block.STATE_IDS.get(buf.readInt())!!, buf.readBlockPos())
	
	override fun write(buf: PacketByteBuf) {
		buf.writeBlockPos(pos)
		buf.writeInt(Block.STATE_IDS.getRawId(state))
		buf.writeBlockPos(jarPos)
	}
	
	override fun apply(listener: ClientPlayPacketListener) {
	}
}
