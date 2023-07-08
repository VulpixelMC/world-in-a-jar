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
package gay.sylv.wij.impl.block.entity

import gay.sylv.wij.impl.block.entity.render.JarChunk
import gay.sylv.wij.impl.block.entity.render.JarChunkRenderRegion
import gay.sylv.wij.impl.dimension.DimensionTypes
import gay.sylv.wij.impl.network.c2s.C2SPackets
import gay.sylv.wij.impl.network.c2s.WorldJarLoadedC2SPacket
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.World
import net.minecraft.world.chunk.palette.PalettedContainer
import org.quiltmc.loader.api.minecraft.ClientOnly
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntity
import org.quiltmc.qsl.networking.api.PacketByteBufs
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking
import kotlin.math.ceil

/**
 * A World Jar [BlockEntity].
 * @author sylv
 */
class WorldJarBlockEntity(
	blockPos: BlockPos?,
	blockState: BlockState?,
) : BlockEntity(BlockEntityTypes.WORLD_JAR, blockPos, blockState), QuiltBlockEntity, gay.sylv.wij.api.BlockPosChecker {
	/**
	 * TODO: docs
	 * @author sylv
	 */
	var magnitude: Int = -1
	/**
	 * TODO: docs
	 * @author sylv
	 */
	val scale: Float
		get() = 1.0f / magnitude
	var subPos: BlockPos = BlockPos(0, -64, 0)
	var blockStates: Long2ObjectMap<BlockState> = Long2ObjectOpenHashMap() // this is likely slower. however, i'm doing this for now to save me headaches. TODO: move to PalettedContainers in each JarChunk.
	val blockEntities: Long2ObjectMap<BlockEntity> = Long2ObjectOpenHashMap() // TODO: implement BEs and BERs
	
	/**
	 * If the [BlockState]s in the jar have changed.
	 * This is used in rendering to determine whether we need to rebuild the VBOs.
	 * @author sylv
	 */
	@ClientOnly
	internal var statesChanged = false
	
	/**
	 * [JarChunk]s that are being rendered.
	 * @author sylv
	 */
	@ClientOnly
	val chunks: Long2ObjectMap<JarChunk> = Long2ObjectOpenHashMap()
	
	/**
	 * TODO: docs
	 * @author sylv
	 */
	@ClientOnly
	val renderRegion: JarChunkRenderRegion = JarChunkRenderRegion(this, chunks)
	
	/**
	 * TODO: docs
	 * @author sylv
	 */
	fun updateBlockStates(server: MinecraftServer) {
		val world = server.getWorld(DimensionTypes.WORLD_JAR_WORLD)!!
		val max = magnitude - 1
		for (x in 0..max) {
			for (y in 1..max) { // we move up one because the lowest layer is barrier blocks
				for (z in 0..max) {
					val pos = BlockPos(x, y, z)
					val state = world.getBlockState(pos.add(this.subPos))
					blockStates[pos.asLong()] = state
				}
			}
		}
	}
	
	/**
	 * TODO: docs
	 * @author sylv
	 */
	override fun hasBlockPos(pos: BlockPos): Boolean {
		return pos.isWithinDistance(this.pos, magnitude.toDouble())
	}
	
	/**
	 * TODO: docs
	 * @author sylv
	 */
	override fun setWorld(world: World) {
		super.setWorld(world)
		if (!world.isClient) {
			if (!INSTANCES.contains(this)) {
				mapInstance()
			}
		}
	}
	
	/**
	 * TODO: docs
	 * @author sylv
	 */
	override fun readNbt(nbt: NbtCompound) {
		super.readNbt(nbt)
		magnitude = nbt.getInt("magnitude")
		subPos = BlockPos.fromLong(nbt.getLong("pos"))
		
		if (magnitude > 16) {
			magnitude = 16
		}
		
		if (world?.isClient == true) { // we're on the client
			// tell the server we're loaded
			val buf = PacketByteBufs.create()
			val packet = WorldJarLoadedC2SPacket(pos)
			packet.write(buf)
			ClientPlayNetworking.send(C2SPackets.WORLD_JAR_LOADED, buf)
		}
	}
	
	/**
	 * TODO: docs
	 * @author sylv
	 */
	override fun writeNbt(nbt: NbtCompound) {
		super.writeNbt(nbt)
		if (magnitude != -1) {
			nbt.putInt("magnitude", magnitude)
		}
		
		nbt.putLong("pos", subPos.asLong())
	}
	
	/**
	 * TODO: docs
	 * @author sylv
	 */
	override fun toUpdatePacket(): Packet<ClientPlayPacketListener> {
		return BlockEntityUpdateS2CPacket.of(this)
	}
	
	/**
	 * TODO: docs
	 * @author sylv
	 */
	override fun toSyncedNbt(): NbtCompound {
		return this.toNbt()
	}
	
	/**
	 * TODO: docs
	 * @author sylv
	 */
	override fun markRemoved() {
		super.markRemoved()
		if (world?.isClient == false) {
			unmapInstance()
		}
	}
	
	/**
	 * This method is called upon updating a chunk on the clientside. It first remaps [BlockState]s to the given [PalettedContainer]<[BlockState]>, then recreates the [JarChunk]s, and finally marks [statesChanged] as `true`.
	 * @author sylv
	 */
	@ClientOnly
	fun onChunkUpdate(client: MinecraftClient, blockStateContainer: PalettedContainer<BlockState>) {
		// remap block states
		blockStates = Long2ObjectOpenHashMap()
		
		val max = magnitude - 1
		for (x in 0..max) {
			for (y in 1..max) {
				for (z in 0..max) {
					val blockPos = BlockPos(x, y, z)
					val state = blockStateContainer.get(x, y, z)
					if (state.isAir) continue
					blockStates[blockPos.asLong()] = state
				}
			}
		}
		
		// recreate chunks
		chunks.clear()
		val max1 = ceil(magnitude / 16f).toInt() // technically we include blocks that aren't in the magnitude since those are still parts of chunks
		val beginPos = BlockPos(0, 0, 0)
		val offset = BlockPos(max1, max1, max1)
		for (blockPos in BlockPos.iterate(beginPos, offset)) { // dirty hack
			val chunkPos = ChunkSectionPos.from(blockPos.x, blockPos.y, blockPos.z)
			client.execute { chunks.put(chunkPos.asLong(), JarChunk(chunkPos)) }
		}
		
		client.execute { statesChanged = true }
	}
	
	/**
	 * Puts the current instance in [INSTANCE_MAP] and [INSTANCES].
	 * @author sylv
	 */
	private fun mapInstance() {
		if (INSTANCES.contains(this)) return
		
		// map
		val chunkPos = ChunkPos.toLong(pos)
		val list = INSTANCE_MAP.get(chunkPos)
		if (list != null) {
			list.add(this)
		} else {
			INSTANCE_MAP.put(chunkPos, mutableListOf(this))
		}
		
		// put
		INSTANCES.add(this)
	}
	
	/**
	 * Removes the current instance from [INSTANCE_MAP] and [INSTANCES].
	 * @author sylv
	 */
	private fun unmapInstance() {
		// map
		val chunkPos = ChunkPos.toLong(pos)
		val list = INSTANCE_MAP.get(chunkPos)
		list.remove(this)
		if (list.isEmpty()) {
			INSTANCE_MAP.remove(chunkPos)
		}
		
		// put
		INSTANCES.remove(this)
	}
	
	companion object {
		/**
		 * The default scale or "magnitude" of the [WorldJarBlockEntity].
		 * @author sylv
		 */
		const val DEFAULT_MAGNITUDE = 16
		/**
		 * A list of all the instances of [WorldJarBlockEntity].
		 * @author sylv
		 */
		val INSTANCES: MutableList<WorldJarBlockEntity> = mutableListOf()
		
		/**
		 * A map of [ChunkPos]'s to all instances.
		 * @author sylv
		 */
		val INSTANCE_MAP: Long2ObjectMap<MutableList<WorldJarBlockEntity>> = Long2ObjectOpenHashMap()
	}
}
