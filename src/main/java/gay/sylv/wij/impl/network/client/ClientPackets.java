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

import gay.sylv.wij.impl.block.Blocks;
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import gay.sylv.wij.impl.network.JarChunkUpdatePayload;
import gay.sylv.wij.impl.util.Initializable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.io.IOException;
import java.util.Optional;

/**
 * Registers receivers and handles client-side server-to-client-related packets.
 * @author sylv
 */
@Environment(EnvType.CLIENT)
public final class ClientPackets implements Initializable {
	public static final ClientPackets INSTANCE = new ClientPackets();
	
	@Override
	public void initialize() {
		ClientPlayNetworking.registerGlobalReceiver(JarChunkUpdatePayload.TYPE, (payload, context) -> {
			// verify that the jar is in the current client level
			var level = context.player().level();
			ResourceKey<Level> dimension = level.dimension();
			if (dimension != payload.jarLocation().dimension()) return;
			Optional<WorldJarBlockEntity> optionalJar = level.getBlockEntity(payload.jarLocation().blockPos(), Blocks.WORLD_JAR.type());
			if (optionalJar.isEmpty()) return;
			WorldJarBlockEntity jar = optionalJar.get();
			jar.onChunkUpdate(context.client(), payload.sectionPos(), payload.blockStateContainer());
		});
	}
}
