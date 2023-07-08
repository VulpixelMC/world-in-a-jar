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

import gay.sylv.wij.impl.WIJConstants.id
import gay.sylv.wij.impl.block.entity.BlockEntityTypes
import org.quiltmc.loader.api.minecraft.ClientOnly
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking

/**
 * TODO: docs
 * @author sylv
 */
@ClientOnly
object S2CPackets : gay.sylv.wij.api.Initializable {
	internal val JAR_WORLD_BLOCK_UPDATE = id("jar_world_block_update")
	internal val JAR_WORLD_CHUNK_UPDATE = id("jar_world_chunk_update")
	
	override fun initialize() {
		ClientPlayNetworking.registerGlobalReceiver(JAR_WORLD_BLOCK_UPDATE) {
				client, _, buf, _ ->
			val packet = JarWorldBlockUpdateS2CPacket(buf)
			val pos = packet.pos
			val jarPos = packet.jarPos
			val entityOption = client.world?.getBlockEntity(jarPos, BlockEntityTypes.WORLD_JAR) ?: return@registerGlobalReceiver
			if (entityOption.isEmpty) return@registerGlobalReceiver
			val entity = entityOption.get()
			
			entity.setBlockState(pos, packet.state)
			client.execute { entity.statesChanged = true }
		}
		ClientPlayNetworking.registerGlobalReceiver(JAR_WORLD_CHUNK_UPDATE) {
				client, _, buf, _ ->
			val packet = JarWorldChunkUpdateS2CPacket(buf)
			val pos = packet.pos
			val blockStateContainer = packet.blockStateContainer
			val entityOption = client.world?.getBlockEntity(pos, BlockEntityTypes.WORLD_JAR)
			if (entityOption!!.isEmpty) { // if the jar entity doesn't exist, return
				return@registerGlobalReceiver
			}
			
			val entity = entityOption.get()
			entity.onChunkUpdate(client, blockStateContainer)
		}
	}
}
