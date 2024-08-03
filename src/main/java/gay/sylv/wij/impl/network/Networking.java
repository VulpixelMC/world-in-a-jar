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
import gay.sylv.wij.impl.util.Initializable;
import gay.sylv.wij.impl.util.Pair;
import gay.sylv.wij.impl.util.SafeMap;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

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
		
		private Codecs() {}
	}
	
	public record JarLocation(BlockPos blockPos, ResourceKey<Level> dimension) {
		public static final StreamCodec<RegistryFriendlyByteBuf, JarLocation> STREAM_CODEC = new StreamCodec<>() {
			@Override
			public void encode(RegistryFriendlyByteBuf buf, JarLocation jarLocation) {
				buf.writeBlockPos(jarLocation.blockPos);
				buf.writeResourceKey(jarLocation.dimension);
			}
			
			@Override
			public @NotNull JarLocation decode(RegistryFriendlyByteBuf buf) {
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
		s2c(JarChunkUpdatePayload.class);
		c2s(JarEnterPayload.class);
		
		ServerPackets.INSTANCE.initialize();
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends CustomPacketPayload> Pair<CustomPacketPayload.Type<T>, StreamCodec<RegistryFriendlyByteBuf, T>> scanClass(Class<T> clazz) {
		AtomicReference<CustomPacketPayload.Type<T>> type = new AtomicReference<>();
		AtomicReference<StreamCodec<RegistryFriendlyByteBuf, T>> codec = new AtomicReference<>();
		Arrays.stream(clazz.getDeclaredFields())
				.filter(field -> SafeMap.isStaticAccessible(field, CustomPacketPayload.Type.class) || SafeMap.isStaticAccessible(field, StreamCodec.class))
				.limit(2)
				.forEach(field -> {
					try {
						Class<?> fieldType = field.getType();
						boolean isType = fieldType.isAssignableFrom(CustomPacketPayload.Type.class)
								&& Arrays.stream(fieldType.getTypeParameters())
								.allMatch(parameter -> parameter.getGenericDeclaration().isAssignableFrom(clazz));
						boolean isCodec = fieldType.isAssignableFrom(StreamCodec.class)
								&& Arrays.stream(fieldType.getTypeParameters())
								.allMatch(parameter -> {
									Class<?> generic = parameter.getGenericDeclaration();
									return generic.isAssignableFrom(clazz) || generic.isAssignableFrom(RegistryFriendlyByteBuf.class);
								});
						if (isType) {
							type.set((CustomPacketPayload.Type<T>) field.get(null));
						} else if (isCodec) {
							codec.set((StreamCodec<RegistryFriendlyByteBuf, T>) field.get(null));
						}
					} catch (IllegalAccessException | IllegalArgumentException e) {
						throw new RuntimeException(e);
					}
				});
		return Pair.of(Objects.requireNonNull(type.get()), Objects.requireNonNull(codec.get()));
	}
	
	private static <T extends CustomPacketPayload> void s2c(Class<T> clazz) {
		var scanned = scanClass(clazz);
		PayloadTypeRegistry.playS2C().register(scanned.first(), scanned.second());
	}
	
	private static <T extends CustomPacketPayload> void c2s(Class<T> clazz) {
		var scanned = scanClass(clazz);
		PayloadTypeRegistry.playC2S().register(scanned.first(), scanned.second());
	}
}
