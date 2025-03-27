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

import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import static gay.sylv.wij.impl.util.Instantiation.packetType;

/**
 * Signals that a block inside the {@link WorldJarBlockEntity} has updated. This sends the {@link Networking.JarLocation} of the jar as well as the {@link BlockState} and {@link net.minecraft.core.BlockPos} of the block.
 */
public record JarBlockUpdatePayload(Networking.JarLocation jarLocation, BlockPos blockPos, BlockState blockState) implements CustomPacketPayload {
	public static final Type<JarBlockUpdatePayload> TYPE = packetType("jar_block_update");
	public static final StreamCodec<RegistryFriendlyByteBuf, JarBlockUpdatePayload> CODEC = StreamCodec.composite(
			Networking.JarLocation.STREAM_CODEC, JarBlockUpdatePayload::jarLocation,
			BlockPos.STREAM_CODEC, JarBlockUpdatePayload::blockPos,
			Networking.Codecs.BLOCK_STATE, JarBlockUpdatePayload::blockState,
			JarBlockUpdatePayload::new
	);
	
	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
