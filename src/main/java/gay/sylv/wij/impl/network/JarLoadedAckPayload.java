package gay.sylv.wij.impl.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static gay.sylv.wij.impl.util.Instantiation.packetType;

/**
 * Sent to clients when the jar has loaded server-side.
 */
public record JarLoadedAckPayload(Networking.JarLocation jarLocation) implements CustomPacketPayload {
	public static final Type<JarLoadedAckPayload> TYPE = packetType("jar_loaded_ack");
	public static final StreamCodec<RegistryFriendlyByteBuf, JarLoadedAckPayload> CODEC = StreamCodec.composite(
			Networking.JarLocation.STREAM_CODEC, JarLoadedAckPayload::jarLocation,
			JarLoadedAckPayload::new
	);
	
	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
