package dev.cursedmc.wij.api.block.entity

import dev.cursedmc.wij.api.WorldContainer
import dev.cursedmc.wij.impl.fromLong
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.Packet
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class WorldJarBlockEntity(
	blockPos: BlockPos?,
	blockState: BlockState?,
	val subworld: World? = null,
) : BlockEntity(BlockEntityTypes.WORLD_JAR, blockPos, blockState), WorldContainer {
	var magnitude: Int = -1
	val scale: Float
		get() = 1.0f / magnitude
	var subPos: BlockPos.Mutable = BlockPos.Mutable(0, 0, 0)
	
	override fun readNbt(nbt: NbtCompound) {
		magnitude = nbt.getInt("magnitude")
		subPos = BlockPos.Mutable::class.fromLong(nbt.getLong("pos"))
	}
	
	override fun writeNbt(nbt: NbtCompound) {
		super.writeNbt(nbt)
		if (magnitude != -1) {
			nbt.putInt("magnitude", magnitude)
		}
		
		nbt.putLong("pos", subPos.asLong())
	}
	
	override fun toInitialChunkDataNbt(): NbtCompound {
		return this.toNbt()
	}
	
	override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? {
		return BlockEntityUpdateS2CPacket.of(this)
	}
	
	override fun getWorld(): World? {
		return this.subworld
	}
	
	companion object {
		const val DEFAULT_MAGNITUDE = 16
	}
}
