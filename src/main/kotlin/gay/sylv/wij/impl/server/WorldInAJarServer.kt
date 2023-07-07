/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package gay.sylv.wij.impl.server

import gay.sylv.wij.impl.WIJConstants.MOD_ID
import gay.sylv.wij.impl.dimension.DimensionTypes
import gay.sylv.wij.impl.network.ServerNetworking
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.minecraft.world.TeleportTarget
import org.quiltmc.qkl.library.brigadier.argument.literal
import org.quiltmc.qkl.library.brigadier.util.server
import org.quiltmc.qsl.command.api.CommandRegistrationCallback
import org.quiltmc.qsl.worldgen.dimension.api.QuiltDimensions

/**
 * Integrated/Dedicated server initialization
 */
object WorldInAJarServer : gay.sylv.wij.api.Initializable {
	private const val EXIT_COMMAND = "exit"
	
	override fun initialize() {
		// networking
		ServerNetworking.initialize()
		
		// commands
		CommandRegistrationCallback.EVENT.register {
				dispatcher, context, env ->
			val backNode = dispatcher.register(
				literal<ServerCommandSource>(EXIT_COMMAND)
					.builder
					.executes {
						if (!it.source.isPlayer) {
							it.source.sendError(Text.translatable("command.$MOD_ID.$EXIT_COMMAND.error.source"))
							return@executes -1
						}
						val player = it.source.player
						if (player!!.world.registryKey != DimensionTypes.WORLD_JAR_WORLD) {
							it.source.sendError(Text.translatable("command.$MOD_ID.$EXIT_COMMAND.error.dimension"))
							return@executes -1
						}
						val returnPos = it.source.player as gay.sylv.wij.impl.duck.PlayerWithReturnPos
						val returnDim = it.source.player as gay.sylv.wij.impl.duck.PlayerWithReturnDim
						QuiltDimensions.teleport<ServerPlayerEntity>(player, it.server.getWorld(returnDim.`worldinajar$getReturnDim`()), TeleportTarget(returnPos.`worldinajar$getReturnPos`(), Vec3d.ZERO, 0f, 0f))
						return@executes 0
					}
			)
			
			dispatcher.register(literal<ServerCommandSource>("$MOD_ID:$EXIT_COMMAND").builder.redirect(backNode))
		}
	}
}
