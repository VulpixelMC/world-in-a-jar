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
import gay.sylv.wij.impl.block.entity.render.JarChunkSection
import gay.sylv.wij.impl.block.entity.render.JarLightingProvider
import gay.sylv.wij.impl.dimension.DimensionTypes
import gay.sylv.wij.impl.network.c2s.C2SPackets
import gay.sylv.wij.impl.network.c2s.WorldJarLoadedC2SPacket
import gay.sylv.wij.impl.toBlockPos
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
import net.minecraft.world.ChunkLightBlockView
import net.minecraft.world.World
import net.minecraft.world.chunk.ChunkProvider
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
) : BlockEntity(BlockEntityTypes.WORLD_JAR, blockPos, blockState), QuiltBlockEntity, gay.sylv.wij.api.BlockPosChecker, ChunkProvider {
	/**
	 * TODO: docs
	 * @author sylv
	 */
	var scale: Int = -1
	/**
	 * TODO: docs
	 * @author sylv
	 */
	val visualScale: Float
		get() = 1.0f / scale
	
	/**
	 * Whether the [WorldJarBlockEntity] is locked for non-operators.
	 * @author sylv
	 */
	var locked: Boolean = false
	
	/**
	 * TODO: docs
	 * @author sylv
	 */
	var subPos: BlockPos = BlockPos(0, -64, 0)
	
	/**
	 * If the [BlockState]s in the jar have changed.
	 * This is used in rendering to determine whether we need to rebuild the VBOs.
	 * @author sylv
	 */
	@ClientOnly
	internal var statesChanged = false
	
	/**
	 * [JarChunkSection]s that are loaded in the [WorldJarBlockEntity].
	 * @author sylv
	 */
	val chunkSections: Long2ObjectMap<JarChunkSection> = Long2ObjectOpenHashMap()
	
	/**
	 * The full versions of chunks that are loaded in the [WorldJarBlockEntity]. This is used in lighting.
	 * @author sylv
	 */
	val chunks: Long2ObjectMap<JarChunk> = Long2ObjectOpenHashMap()
	
	/**
	 * TODO: docs
	 * @author sylv
	 */
	@ClientOnly
	lateinit var lightingProvider: JarLightingProvider
	
	/**
	 * TODO: docs
	 * @author sylv
	 */
	@ClientOnly
	lateinit var renderRegion: JarChunkRenderRegion
	
	/**
	 * Is the current jar rendering? This should prevent jars inside jars.
	 * @author sylv
	 */
	@ClientOnly
	var isRendering: Boolean = false
	
	/**
	 * TODO: docs
	 * @author sylv
	 */
	fun updateBlockStates(server: MinecraftServer) {
		initializeChunks()
		val world = server.getWorld(DimensionTypes.WORLD_JAR_WORLD)!!
		val max = scale - 1
		for (x in 0..max) {
			for (y in 0..max) {
				for (z in 0..max) {
					val pos = BlockPos(x, y, z)
					val state = world.getBlockState(pos.add(subPos))
					if (state.isAir) continue
					setBlockState(pos, state)
				}
			}
		}
	}
	
	/**
	 * TODO: docs
	 * @author sylv
	 */
	override fun hasBlockPos(pos: BlockPos): Boolean {
		return pos.isWithinDistance(this.pos, scale.toDouble())
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
		} else {
			lightingProvider = JarLightingProvider(this, hasBlockLight = true, hasSkyLight = true)
			renderRegion = JarChunkRenderRegion(this, lightingProvider)
		}
	}
	
	/**
	 * TODO: docs
	 * @author sylv
	 */
	override fun readNbt(nbt: NbtCompound) {
		super.readNbt(nbt)
		scale = nbt.getInt("magnitude")
		subPos = nbt.getLong("pos").toBlockPos()
		locked = nbt.getBoolean("locked")
		
		if (scale > MAX_SCALE) {
			scale = MAX_SCALE
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
		if (scale != -1) {
			nbt.putInt("magnitude", scale)
		}
		
		nbt.putLong("pos", subPos.asLong())
		nbt.putBoolean("locked", locked)
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
	 * This function always returns null.
	 * @return null
	 */
	override fun getChunk(chunkX: Int, chunkZ: Int): ChunkLightBlockView? {
		val chunkPos = ChunkPos.toLong(chunkX, chunkZ)
		return chunks[chunkPos]
	}
	
	/**
	 * This method is called upon updating a chunk on the clientside. It first remaps [BlockState]s to the given [PalettedContainer]<[BlockState]>, then recreates the [JarChunkSection]s, and finally marks [statesChanged] as `true`.
	 * @author sylv
	 */
	@ClientOnly
	fun onChunkUpdate(client: MinecraftClient, sectionPos: ChunkSectionPos, blockStateContainer: PalettedContainer<BlockState>) {
		client.execute {
			// clear chunks
			if (sectionPos.asLong() == 0L) {
				chunkSections.clear()
				chunks.clear()
			}
			
			// put chunk
			val chunkSection = JarChunkSection(sectionPos, true)
			val chunkPos = ChunkPos(sectionPos.x, sectionPos.z)
			val chunk = JarChunk(chunkPos, this)
			
			// remap block states
			chunkSection.blockStates = blockStateContainer
			
			chunkSections.put(sectionPos.asLong(), chunkSection)
			chunks.put(chunkPos.toLong(), chunk)
			
			statesChanged = true
		}
	}
	
	/**
	 * Sets a [BlockState] at the specified position.
	 * @author sylv
	 */
	fun setBlockState(pos: BlockPos, state: BlockState) {
		val sectionPos = ChunkSectionPos.from(pos)
		val section = chunkSections[sectionPos.asLong()]
		section.blockStates.set(pos.x.and(15), pos.y.and(15), pos.z.and(15), state)
	}
	
	/**
	 * Gets a [BlockState] from the specified position.
	 * @return [BlockState]
	 * @author sylv
	 */
	fun getBlockState(pos: BlockPos): BlockState {
		val sectionPos = ChunkSectionPos.from(pos)
		val section = chunkSections[sectionPos.asLong()]
		return section.blockStates.get(pos.x.and(15), pos.y.and(15), pos.z.and(15))
	}
	
	/**
	 * Returns how many chunks high the [WorldJarBlockEntity] is. This always rounds up to include partial chunks.
	 * @return how many chunks high the [WorldJarBlockEntity] is.
	 * @author sylv
	 */
	fun getChunkHeight(): Int {
		return ceil(scale.toDouble() / 16.0).toInt()
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
	
	/**
	 * Initializes the chunks server-side.
	 * @author sylv
	 */
	private fun initializeChunks() {
		// initialize chunks
		chunkSections.clear()
		chunks.clear()
		val max = getChunkHeight() - 1
		val beginPos = BlockPos(0, 0, 0)
		val offset = BlockPos(max, max, max)
		for (chunkBlockPos in BlockPos.iterate(beginPos, offset)) { // dirty hack
			val sectionPos = ChunkSectionPos.from(chunkBlockPos.x, chunkBlockPos.y, chunkBlockPos.z)
			val chunkSection = JarChunkSection(sectionPos, false)
			val chunkPos = ChunkPos(sectionPos.x, sectionPos.z)
			val chunk = JarChunk(chunkPos, this)
			
			// put chunk
			chunkSections.put(sectionPos.asLong(), chunkSection)
			chunks.put(chunkPos.toLong(), chunk)
		}
	}
	
	companion object {
		/**
		 * The default scale of the [WorldJarBlockEntity].
		 * @author sylv
		 */
		const val DEFAULT_SCALE = 16
		
		/**
		 * The maximum scale of the [WorldJarBlockEntity].
		 * @author sylv
		 */
		const val MAX_SCALE = 64
		
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
