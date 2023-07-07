package gay.sylv.wij.impl.network

import gay.sylv.wij.api.Initializable
import gay.sylv.wij.impl.block.entity.BlockEntityTypes
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity
import gay.sylv.wij.impl.network.s2c.JarWorldChunkUpdateS2CPacket
import gay.sylv.wij.impl.network.s2c.S2CPackets
import gay.sylv.wij.impl.toPalettedContainer
import net.minecraft.world.World
import org.quiltmc.qkl.library.networking.playersTracking
import org.quiltmc.qsl.networking.api.EntityTrackingEvents
import org.quiltmc.qsl.networking.api.PacketByteBufs
import org.quiltmc.qsl.networking.api.ServerPlayNetworking

/**
 * Stuff for server-side networking.
 * @author sylv
 */
object ServerNetworking : Initializable {
	/**
	 * Sends the jar's chunks to players tracking the jar.
	 * @author sylv
	 */
	fun sendChunks(world: World, entity: WorldJarBlockEntity) {
		world.server?.let { entity.updateBlockStates(it) }
		
		for (player in entity.playersTracking) {
			val buf = PacketByteBufs.create()
			val packet = JarWorldChunkUpdateS2CPacket(entity.pos, entity.blockStates.toPalettedContainer())
			packet.write(buf)
			ServerPlayNetworking.send(player, S2CPackets.JAR_WORLD_CHUNK_UPDATE, buf)
		}
	}
}
