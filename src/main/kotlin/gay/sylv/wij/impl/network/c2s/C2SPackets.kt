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

import gay.sylv.wij.impl.WIJConstants.id
import gay.sylv.wij.impl.block.entity.BlockEntityTypes
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity
import gay.sylv.wij.impl.dimension.DimensionTypes
import gay.sylv.wij.impl.network.ServerNetworking
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d
import net.minecraft.world.TeleportTarget
import org.quiltmc.qsl.networking.api.ServerPlayNetworking
import org.quiltmc.qsl.worldgen.dimension.api.QuiltDimensions

/**
 * TODO: docs
 * @author sylv
 */
object C2SPackets : gay.sylv.wij.api.Initializable {
	internal val WORLD_JAR_ENTER = id("world_jar_enter")
	internal val WORLD_JAR_UPDATE = id("world_jar_update")
	internal val WORLD_JAR_LOADED = id("world_jar_loaded")
	
	override fun initialize() {
		ServerPlayNetworking.registerGlobalReceiver(WORLD_JAR_ENTER) {
				server, player, _, buf, _ ->
			val packet = WorldJarEnterC2SPacket(buf)
			val pos = packet.pos
			
			server.execute {
				val world = player.world
				val entityOption = world.getBlockEntity(pos, BlockEntityTypes.WORLD_JAR)
				if (entityOption.isEmpty) return@execute
				val entity = entityOption.get()
				
				val jarWorld = server.getWorld(DimensionTypes.WORLD_JAR_WORLD)
				
				val returnPos = player as gay.sylv.wij.impl.duck.PlayerWithReturnPos
				val returnDim = player as gay.sylv.wij.impl.duck.PlayerWithReturnDim
				returnPos.`worldinajar$setReturnPos`(player.pos)
				returnDim.`worldinajar$setReturnDim`(player.world.registryKey)
				
				QuiltDimensions.teleport<ServerPlayerEntity>(player, jarWorld, TeleportTarget(Vec3d.of(entity.subPos.add(0, 1, 0)), Vec3d.ZERO, 0f, 0f))
			}
		}
		ServerPlayNetworking.registerGlobalReceiver(WORLD_JAR_UPDATE) {
				server, player, _, buf, _ ->
			val pos = buf.readBlockPos()
			val magnitude = buf.readInt()
			val entityPos = buf.readBlockPos()
			
			server.execute {
				if (player.world.getBlockEntity(entityPos)?.type == BlockEntityTypes.WORLD_JAR) {
					// update jar properties
					val entity = player.world.getBlockEntity(entityPos, BlockEntityTypes.WORLD_JAR).get()
					entity.subPos = pos.mutableCopy()
					entity.magnitude = magnitude
					entity.markDirty()
					entity.sync()
					
					// send chunk update
					ServerNetworking.sendChunks(player.world, entity)
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
