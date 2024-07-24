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
package gay.sylv.wij.impl.util

import gay.sylv.wij.impl.WIJConstants
import gay.sylv.wij.impl.block.entity.BlockEntityTypes
import gay.sylv.wij.impl.dimension.DimensionTypes
import gay.sylv.wij.impl.server.command.Commands
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.TeleportTarget
import org.quiltmc.qsl.worldgen.dimension.api.QuiltDimensions

fun MatrixStack.scale(scale: Float) {
	this.scale(scale, scale, scale)
}

fun Long.toBlockPos(): BlockPos {
	return BlockPos.fromLong(this)
}

fun playerEnterJar(server: MinecraftServer, player: PlayerEntity, pos: BlockPos) {
	val world = player.world
	val entityOption = world.getBlockEntity(pos, BlockEntityTypes.WORLD_JAR)
	if (entityOption.isEmpty) return
	val entity = entityOption.get()
	
	val jarWorld = server.getWorld(DimensionTypes.WORLD_JAR_WORLD)
	
	val returnPos = player as gay.sylv.wij.impl.duck.PlayerWithReturnPos
	val returnDim = player as gay.sylv.wij.impl.duck.PlayerWithReturnDim
	returnPos.`worldinajar$setReturnPos`(player.pos)
	returnDim.`worldinajar$setReturnDim`(player.world.registryKey)
	val middle = entity.scale / 2.0 // the middle-point where the player spawns
	
	// teleport the player in the middle of the jar two-and-a-half blocks above the Y of the sub-position (subPos)
	QuiltDimensions.teleport<ServerPlayerEntity>(player, jarWorld, TeleportTarget(Vec3d.of(entity.subPos)?.add(middle, 2.5, middle), Vec3d.ZERO, 0f, 0f))
	// tell the player that they can exit the jar using /worldinajar exit
	// TODO: make a way for the player to exit the jar without using a command
	player.sendSystemMessage(Text.translatable("${WIJConstants.MOD_ID}.world_jar.enter_message", Commands.ROOT_COMMAND as Any, Commands.EXIT_COMMAND))
}
