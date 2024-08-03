package gay.sylv.wij.impl.network;

import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.jetbrains.annotations.NotNull;

import static gay.sylv.wij.impl.util.Instantiation.packetType;

/**
 * Signals that a chunk inside the {@link WorldJarBlockEntity} has updated. This sends the {@link gay.sylv.wij.impl.network.Networking.JarLocation} of the jar as well as the {@link PalettedContainer}&lt;{@link BlockState}&gt; of the chunk.
 */
public record JarChunkUpdatePayload(Networking.JarLocation jarLocation, SectionPos sectionPos, PalettedContainer<BlockState> blockStateContainer) implements CustomPacketPayload {
	public static final Type<JarChunkUpdatePayload> TYPE = packetType("jar_chunk_update");
	public static final StreamCodec<RegistryFriendlyByteBuf, JarChunkUpdatePayload> CODEC = StreamCodec.composite(
			Networking.JarLocation.STREAM_CODEC, JarChunkUpdatePayload::jarLocation,
			Networking.Codecs.SECTION_POS, JarChunkUpdatePayload::sectionPos,
			Networking.Codecs.BLOCK_STATE_PALETTED_CONTAINER, JarChunkUpdatePayload::blockStateContainer,
			JarChunkUpdatePayload::new
	);
	
	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
