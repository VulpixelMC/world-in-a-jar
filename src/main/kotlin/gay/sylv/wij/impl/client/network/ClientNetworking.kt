package gay.sylv.wij.impl.client.network

import gay.sylv.wij.api.Initializable
import gay.sylv.wij.impl.network.s2c.S2CPackets

/**
 * Stuff for client-side networking.
 * @author sylv
 */
object ClientNetworking : Initializable {
	override fun initialize() {
		// packets
		S2CPackets.initialize()
	}
}
