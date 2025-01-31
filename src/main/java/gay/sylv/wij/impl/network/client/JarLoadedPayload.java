package gay.sylv.wij.impl.network.client;

import gay.sylv.wij.impl.network.Networking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static gay.sylv.wij.impl.util.Instantiation.packetType;

/**
 * A request from the client to send a {@link gay.sylv.wij.impl.network.JarChunkUpdatePayload}.
 * @param jarLocation the location of the jar.
 */
public record JarLoadedPayload(Networking.JarLocation jarLocation) implements CustomPacketPayload {
	public static final Type<JarLoadedPayload> TYPE = packetType("jar_loaded");
	public static final StreamCodec<RegistryFriendlyByteBuf, JarLoadedPayload> CODEC = StreamCodec.composite(
			Networking.JarLocation.STREAM_CODEC, JarLoadedPayload::jarLocation,
			JarLoadedPayload::new
	);
	
	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
