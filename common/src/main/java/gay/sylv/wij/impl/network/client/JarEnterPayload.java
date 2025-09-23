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
