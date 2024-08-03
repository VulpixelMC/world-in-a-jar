package gay.sylv.wij.impl.network.client;

import gay.sylv.wij.impl.network.Networking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static gay.sylv.wij.impl.util.Instantiation.packetType;

/**
 * A request from the client to enter a {@link gay.sylv.wij.impl.block.entity.WorldJarBlockEntity}.
 * @param jarLocation the location of the jar.
 */
public record JarEnterPayload(Networking.JarLocation jarLocation) implements CustomPacketPayload {
	public static final Type<JarEnterPayload> TYPE = packetType("jar_enter");
	public static final StreamCodec<RegistryFriendlyByteBuf, JarEnterPayload> CODEC = StreamCodec.composite(
			Networking.JarLocation.STREAM_CODEC, JarEnterPayload::jarLocation,
			JarEnterPayload::new
	);
	
	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
