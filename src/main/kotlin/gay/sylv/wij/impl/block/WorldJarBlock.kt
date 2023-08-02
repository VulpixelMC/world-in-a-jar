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
package gay.sylv.wij.impl.block

import gay.sylv.wij.impl.block.entity.BlockEntityTypes
import gay.sylv.wij.impl.gui.screen.WorldJarScreen
import gay.sylv.wij.impl.network.c2s.C2SPackets
import gay.sylv.wij.impl.network.c2s.WorldJarEnterC2SPacket
import gay.sylv.wij.impl.network.s2c.OpenJarScreenS2CPacket
import gay.sylv.wij.impl.network.s2c.S2CPackets
import gay.sylv.wij.impl.util.Permissions
import gay.sylv.wij.impl.util.playerEnterJar
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.quiltmc.qsl.networking.api.PacketByteBufs
import org.quiltmc.qsl.networking.api.ServerPlayNetworking
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking

class WorldJarBlock(settings: Settings?) : BlockWithEntity(settings) {
	override fun createBlockEntity(pos: BlockPos?, state: BlockState?): BlockEntity? {
		return BlockEntityTypes.WORLD_JAR.instantiate(pos, state)
	}
	
	@Deprecated("mojang", ReplaceWith("BlockRenderType.MODEL", "net.minecraft.block.BlockRenderType"))
	override fun getRenderType(state: BlockState?): BlockRenderType {
		return BlockRenderType.MODEL
	}
	
	@Deprecated("mojang")
	override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
		if (!world.isClient) {
			val entity = world.getBlockEntity(pos, BlockEntityTypes.WORLD_JAR).get()
			if (!entity.locked || Permissions.canLockJars(player)) {
				val buf = PacketByteBufs.create()
				val packet = OpenJarScreenS2CPacket(pos)
				packet.write(buf)
				ServerPlayNetworking.send(player as ServerPlayerEntity, S2CPackets.OPEN_JAR_SCREEN, buf)
			} else {
				playerEnterJar(world.server!!, player, entity.pos)
			}
		}
		
		return ActionResult.SUCCESS
	}
}
