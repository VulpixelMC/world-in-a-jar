package dev.cursedmc.wij.api.network.c2s

import dev.cursedmc.wij.api.Initializable
import dev.cursedmc.wij.api.block.entity.BlockEntityTypes
import dev.cursedmc.wij.impl.WIJConstants.id
import org.quiltmc.qsl.networking.api.ServerPlayNetworking

object C2SPackets : Initializable {
	val WORLD_JAR_UPDATE = id("world_jar_update")
	
	override fun initialize() {
		ServerPlayNetworking.registerGlobalReceiver(WORLD_JAR_UPDATE) { server, player, _, buffer, _ ->
			val pos = buffer.readBlockPos()
			val magnitude = buffer.readInt()
			val entityPos = buffer.readBlockPos()
			
			server.execute {
				if (player.world.getBlockEntity(entityPos)?.type == BlockEntityTypes.WORLD_JAR) {
					val entity = player.world.getBlockEntity(entityPos, BlockEntityTypes.WORLD_JAR).get()
					entity.subPos = pos.mutableCopy()
					entity.magnitude = magnitude
					entity.markDirty()
					entity.sync()
				}
				
				println(player.world.getBlockEntity(entityPos)?.toNbt())
			}
		}
	}
}
