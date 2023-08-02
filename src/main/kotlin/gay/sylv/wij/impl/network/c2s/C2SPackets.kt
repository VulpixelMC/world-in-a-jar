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

import gay.sylv.wij.impl.WIJConstants.MOD_ID
import gay.sylv.wij.impl.WIJConstants.id
import gay.sylv.wij.impl.block.entity.BlockEntityTypes
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity
import gay.sylv.wij.impl.dimension.DimensionTypes
import gay.sylv.wij.impl.server.command.Commands
import gay.sylv.wij.impl.server.network.ServerNetworking
import gay.sylv.wij.impl.util.Permissions
import gay.sylv.wij.impl.util.playerEnterJar
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.minecraft.world.TeleportTarget
import org.quiltmc.qsl.networking.api.ServerPlayNetworking
import org.quiltmc.qsl.worldgen.dimension.api.QuiltDimensions

/**
 * Registers receivers and handles server-side C2S-related packets.
 * @author sylv
 */
object C2SPackets : gay.sylv.wij.api.Initializable {
	internal val WORLD_JAR_ENTER = id("world_jar_enter")
	internal val WORLD_JAR_UPDATE = id("world_jar_update")
	internal val WORLD_JAR_LOCK = id("world_jar_lock")
	internal val WORLD_JAR_LOADED = id("world_jar_loaded")
	
	override fun initialize() {
		ServerPlayNetworking.registerGlobalReceiver(WORLD_JAR_ENTER) {
				server, player, _, buf, _ ->
			val packet = WorldJarEnterC2SPacket(buf)
			val pos = packet.pos
			
			server.execute {
				playerEnterJar(server, player, pos)
			}
		}
		ServerPlayNetworking.registerGlobalReceiver(WORLD_JAR_UPDATE) {
				server, player, _, buf, _ ->
			val subPos = buf.readBlockPos()
			val scale = buf.readInt()
			val entityPos = buf.readBlockPos()
			
			server.execute {
				if (player.world.getBlockEntity(entityPos)?.type == BlockEntityTypes.WORLD_JAR) {
					// update jar properties
					val entity = player.world.getBlockEntity(entityPos, BlockEntityTypes.WORLD_JAR).get()
					if (entity.locked && !Permissions.canLockJars(player)) return@execute
					entity.subPos = subPos.mutableCopy()
					entity.scale = scale
					entity.markDirty()
					entity.sync()
				}
			}
		}
		ServerPlayNetworking.registerGlobalReceiver(WORLD_JAR_LOCK) {
			server, player, _, buf, _ ->
			val pos = buf.readBlockPos()
			val locked = buf.readBoolean()
			
			server.execute {
				if (Permissions.canLockJars(player) && player.world.getBlockEntity(pos)?.type == BlockEntityTypes.WORLD_JAR) {
					val entity = player.world.getBlockEntity(pos, BlockEntityTypes.WORLD_JAR).get()
					entity.locked = locked
					entity.markDirty()
					entity.sync()
				}
			}
		}
		ServerPlayNetworking.registerGlobalReceiver(WORLD_JAR_LOADED) {
			server, player, _, buf, _ ->
			val pos = buf.readBlockPos()
			
			server.execute {
				val blockEntity = player.world.getBlockEntity(pos)
				if (blockEntity?.type != BlockEntityTypes.WORLD_JAR) return@execute // quick check if this is a world jar
				val jarEntity = blockEntity as WorldJarBlockEntity
				
				ServerNetworking.sendChunks(player.world, jarEntity)
			}
		}
	}
}
