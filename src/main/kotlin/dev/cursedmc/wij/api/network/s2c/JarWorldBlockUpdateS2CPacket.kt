package dev.cursedmc.wij.api.network.s2c

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.network.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.util.math.BlockPos

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
