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
package gay.sylv.wij.impl.server.network

import gay.sylv.wij.api.Initializable
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity
import gay.sylv.wij.impl.network.c2s.C2SPackets
import gay.sylv.wij.impl.network.s2c.JarWorldChunkUpdateS2CPacket
import gay.sylv.wij.impl.network.s2c.S2CPackets
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.World
import org.quiltmc.qkl.library.networking.playersTracking
import org.quiltmc.qsl.networking.api.PacketByteBufs
import org.quiltmc.qsl.networking.api.ServerPlayNetworking

/**
 * Stuff for server-side networking.
 * @author sylv
 */
object ServerNetworking : Initializable {
	/**
	 * Sends the jar's chunks to players tracking the jar.
	 * @author sylv
	 */
	fun sendChunks(world: World, entity: WorldJarBlockEntity) {
		entity.updateBlockStates(world.server!!)
		
		for (player in entity.playersTracking) {
			entity.chunkSections.forEach {
				val sectionPos = ChunkSectionPos.from(it.key)
				val chunk = it.value
				
				val buf = PacketByteBufs.create()
				val packet = JarWorldChunkUpdateS2CPacket(entity.pos, sectionPos, chunk.blockStates)
				packet.write(buf)
				ServerPlayNetworking.send(player, S2CPackets.JAR_WORLD_CHUNK_UPDATE, buf)
			}
		}
	}
	
	override fun initialize() {
		// packets
		C2SPackets.initialize()
	}
}
