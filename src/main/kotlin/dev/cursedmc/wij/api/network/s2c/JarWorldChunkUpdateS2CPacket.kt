package dev.cursedmc.wij.api.network.s2c

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.network.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.palette.PalettedContainer

class JarWorldChunkUpdateS2CPacket(val pos: BlockPos, val blockStateContainer: PalettedContainer<BlockState>) : Packet<ClientPlayPacketListener> {
	constructor(buf: PacketByteBuf) : this(buf.readBlockPos(), buf.run {
		val container = PalettedContainer(Block.STATE_IDS, Blocks.AIR.defaultState, PalettedContainer.PaletteProvider.BLOCK_STATE)
		container.method_12326(this@run)
		container
	})
	
	override fun write(buf: PacketByteBuf) {
		buf.writeBlockPos(pos)
		blockStateContainer.method_12325(buf) // write
	}
	
	override fun apply(listener: ClientPlayPacketListener) {
	}
}
