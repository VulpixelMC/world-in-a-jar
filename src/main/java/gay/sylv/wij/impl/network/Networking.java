/**
 * World In a Jar
 * Copyright (C) 2024  VulpixelMC
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package gay.sylv.wij.impl.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gay.sylv.wij.impl.network.client.JarEnterPayload;
import gay.sylv.wij.impl.network.client.JarLoadedPayload;
import gay.sylv.wij.impl.util.Initializable;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static gay.sylv.wij.impl.util.Instantiation.blockStatePalettedContainer;

/**
 * Class for putting both-side networking-related things.
 */
public final class Networking implements Initializable {
	public static final Networking INSTANCE = new Networking();
	
	private Networking() {}
	
	public static class Codecs {
		public static final StreamCodec<RegistryFriendlyByteBuf, SectionPos> SECTION_POS = new StreamCodec<>() {
			@Override
			public void encode(RegistryFriendlyByteBuf buf, SectionPos sectionPos) {
				buf.writeLong(sectionPos.asLong());
			}
			
			@Override
			public @NotNull SectionPos decode(RegistryFriendlyByteBuf buf) {
				return SectionPos.of(buf.readLong());
			}
		};
		public static final StreamCodec<RegistryFriendlyByteBuf, PalettedContainer<BlockState>> BLOCK_STATE_PALETTED_CONTAINER = new StreamCodec<>() {
			@Override
			public void encode(RegistryFriendlyByteBuf buf, PalettedContainer<BlockState> blockStateContainer) {
				blockStateContainer.write(buf);
			}
			
			@Override
			public @NotNull PalettedContainer<BlockState> decode(RegistryFriendlyByteBuf buf) {
				PalettedContainer<BlockState> blockStateContainer = blockStatePalettedContainer();
				blockStateContainer.read(buf);
				return blockStateContainer;
			}
		};
		public static final StreamCodec<RegistryFriendlyByteBuf, BlockState> BLOCK_STATE = new StreamCodec<>() {
			@Override
			public void encode(RegistryFriendlyByteBuf buf, BlockState blockState) {
				buf.writeVarInt(Block.getId(blockState));
			}
			
			@Override
			public @NotNull BlockState decode(RegistryFriendlyByteBuf buf) {
				return Block.stateById(buf.readVarInt());
			}
		};
		public static final StreamCodec<RegistryFriendlyByteBuf, Vec3> VEC3 = StreamCodec.of(
				(buf, pos) -> {
					buf.writeDouble(pos.x());
					buf.writeDouble(pos.y());
					buf.writeDouble(pos.z());
				},
				buf -> new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
		);
		
		private Codecs() {}
	}
	
	public record JarLocation(BlockPos blockPos, ResourceKey<Level> dimension) {
		public static final StreamCodec<FriendlyByteBuf, JarLocation> STREAM_CODEC = new StreamCodec<>() {
			@Override
			public void encode(FriendlyByteBuf buf, JarLocation jarLocation) {
				buf.writeBlockPos(jarLocation.blockPos);
				buf.writeResourceKey(jarLocation.dimension);
			}
			
			@Override
			public @NotNull JarLocation decode(FriendlyByteBuf buf) {
				return new JarLocation(buf.readBlockPos(), buf.readResourceKey(Registries.DIMENSION));
			}
		};
		
		public static final Codec<JarLocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				BlockPos.CODEC.fieldOf("block_pos").forGetter(JarLocation::blockPos),
				ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(JarLocation::dimension)
		).apply(instance, JarLocation::new));
	}
	
	@Override
	public void initialize() {
		s2c(JarChunkUpdatePayload.TYPE, JarChunkUpdatePayload.CODEC);
		s2c(JarBlockUpdatePayload.TYPE, JarBlockUpdatePayload.CODEC);
		s2c(JarLoadedAckPayload.TYPE, JarLoadedAckPayload.CODEC);
		s2c(ExternalChunkUpdatePayload.TYPE, ExternalChunkUpdatePayload.CODEC);
		c2s(JarEnterPayload.TYPE, JarEnterPayload.CODEC);
		c2s(JarLoadedPayload.TYPE, JarLoadedPayload.CODEC);
		
		ServerPackets.INSTANCE.initialize();
	}
	
	private static <T extends CustomPacketPayload> void s2c(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
		PayloadTypeRegistry.playS2C().register(type, codec);
	}
	
	private static <T extends CustomPacketPayload> void c2s(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
		PayloadTypeRegistry.playC2S().register(type, codec);
	}
}
