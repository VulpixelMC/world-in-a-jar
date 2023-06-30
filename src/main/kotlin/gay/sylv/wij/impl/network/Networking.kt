package gay.sylv.wij.impl.network

import gay.sylv.wij.api.Initializable
import gay.sylv.wij.impl.WIJConstants.id
import gay.sylv.wij.impl.network.s2c.JarWorldBlockUpdateS2CPacket
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking

object Networking : Initializable {
	override fun initialize() {
		ClientPlayNetworking.registerGlobalReceiver(id("block_update")) { client, handler, buf, responseSender ->
			// TODO: check if this is the right packet
			val packet = JarWorldBlockUpdateS2CPacket(buf)
			client.world!!.getBlockEntity() // TODO: update the jar's `statesChanged` field
		}
	}
}
