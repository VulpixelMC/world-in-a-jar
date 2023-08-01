package gay.sylv.wij.impl.server.command

import gay.sylv.wij.api.Initializable
import gay.sylv.wij.impl.WIJConstants.MOD_ID
import gay.sylv.wij.impl.WorldInAJar
import gay.sylv.wij.impl.dimension.DimensionTypes
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.minecraft.world.TeleportTarget
import org.quiltmc.qkl.library.brigadier.argument.literal
import org.quiltmc.qkl.library.brigadier.util.sendFeedback
import org.quiltmc.qkl.library.brigadier.util.server
import org.quiltmc.qsl.command.api.CommandRegistrationCallback
import org.quiltmc.qsl.worldgen.dimension.api.QuiltDimensions

/**
 * Registration of commands.
 * @author sylv
 */
object Commands : Initializable {
	const val ROOT_COMMAND = MOD_ID
	private const val ROOT_ALIAS = "wij"
	const val EXIT_COMMAND = "exit"
	
	override fun initialize() {
		CommandRegistrationCallback.EVENT.register {
				dispatcher, _, _ ->
			val exitNode = literal<ServerCommandSource>(EXIT_COMMAND)
				.builder
				.executes {
					if (!it.source.isPlayer) {
						it.source.sendError(Text.translatable("command.${ROOT_COMMAND}.${EXIT_COMMAND}.error.source"))
						return@executes -1
					}
					val player = it.source.player
					if (player!!.world.registryKey != DimensionTypes.WORLD_JAR_WORLD) {
						it.source.sendError(Text.translatable("command.${ROOT_COMMAND}.${EXIT_COMMAND}.error.dimension"))
						return@executes -1
					}
					val returnPos = it.source.player as gay.sylv.wij.impl.duck.PlayerWithReturnPos
					val returnDim = it.source.player as gay.sylv.wij.impl.duck.PlayerWithReturnDim
					QuiltDimensions.teleport<ServerPlayerEntity>(player, it.server.getWorld(returnDim.`worldinajar$getReturnDim`()), TeleportTarget(returnPos.`worldinajar$getReturnPos`(), Vec3d.ZERO, 0f, 0f))
					return@executes 0
				}
				.build()
			
			val wijNode = dispatcher.register(
				literal<ServerCommandSource>(ROOT_COMMAND)
				.builder
				.then(exitNode))
			
			dispatcher.register(
				literal<ServerCommandSource>(ROOT_COMMAND)
				.builder
				.executes { context ->
					val usages = dispatcher.getSmartUsage(wijNode, context.source)
					
					WorldInAJar.LOGGER.info(context.nodes[0].node.toString())
					
					usages.forEach {
						context.sendFeedback(Text.literal("/${ROOT_COMMAND} ${it.key.usageText}"), false)
					}
					
					return@executes 0
				})
			
			dispatcher.register(
				literal<ServerCommandSource>(ROOT_ALIAS)
				.builder
				.executes(wijNode.command)
				.redirect(wijNode))
		}
	}
}
