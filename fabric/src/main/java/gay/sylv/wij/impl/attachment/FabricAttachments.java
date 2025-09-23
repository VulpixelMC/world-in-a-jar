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
package gay.sylv.wij.impl.attachment;

import gay.sylv.wij.impl.network.Networking;
import gay.sylv.wij.impl.util.Initializable;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

import static gay.sylv.wij.impl.util.Constants.modId;

public final class FabricAttachments implements Initializable {
	public static final FabricAttachments INSTANCE = new FabricAttachments();

	public static AttachmentType<Vec3> PLAYER_RETURN_POS;
	public static AttachmentType<Networking.JarLocation> RETURN_JAR_LOCATION;
	public static AttachmentType<Networking.JarLocation> ENTERED_JAR_LOCATION;

	private FabricAttachments() {}

	@Override
	public void initialize() {
		PLAYER_RETURN_POS = register(
				"player_return_pos",
				builder -> builder
						.copyOnDeath()
						.persistent(Vec3.CODEC)
						.syncWith(Networking.Codecs.VEC3, AttachmentSyncPredicate.targetOnly())
		);
		RETURN_JAR_LOCATION = register(
				"return_jar_location",
				builder -> builder
						.copyOnDeath()
						.persistent(Networking.JarLocation.CODEC)
						.syncWith(Networking.JarLocation.STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
		);
		ENTERED_JAR_LOCATION = register(
				"entered_jar_location",
				builder -> builder
						.copyOnDeath()
						.persistent(Networking.JarLocation.CODEC)
						.syncWith(Networking.JarLocation.STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
		);
	}

	private static <A> AttachmentType<A> register(String name, Consumer<AttachmentRegistry.Builder<A>> consumer) {
		return AttachmentRegistry.create(modId(name), consumer);
	}
}
