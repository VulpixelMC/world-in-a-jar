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

import gay.sylv.wij.impl.WorldInAJar;
import gay.sylv.wij.impl.block.Blocks;
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import gay.sylv.wij.impl.dimension.Dimensions;
import gay.sylv.wij.impl.duck.PlayerWithReturn;
import gay.sylv.wij.impl.network.client.JarEnterPayload;
import gay.sylv.wij.impl.network.client.JarLoadedPayload;
import gay.sylv.wij.impl.util.Initializable;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Registers receivers and handles non-dedicated server-side Client-to-Server-related packets.
 * @author sylv
 */
public final class ServerPackets implements Initializable {
	public static final ServerPackets INSTANCE = new ServerPackets();
	
	@Override
	public void initialize() {
		ServerPlayNetworking.registerGlobalReceiver(JarEnterPayload.TYPE, (payload, context) -> {
			MinecraftServer server = context.server();
			ServerLevel level = server.getLevel(payload.jarLocation().dimension());
			assert level != null;
			Optional<WorldJarBlockEntity> optionalJar = level.getBlockEntity(payload.jarLocation().blockPos(), Blocks.WORLD_JAR.type());
			if (optionalJar.isEmpty()) return;
			WorldJarBlockEntity jar = optionalJar.get();
			
			ServerPlayer player = context.player();
			((PlayerWithReturn) player).worldinajar$setReturnLocation(
					new Networking.JarLocation(BlockPos.containing(player.position()), player.level().dimension())
			);
			ServerLevel targetLevel = Objects.requireNonNull(server.getLevel(Dimensions.JAR));
			DimensionTransition transition = new DimensionTransition(targetLevel, Vec3.atCenterOf(jar.getInternalSpawnPos()), Vec3.ZERO, 0.0f, 0.0f, DimensionTransition.DO_NOTHING);
			player.changeDimension(transition);
		});
		ServerPlayNetworking.registerGlobalReceiver(JarLoadedPayload.TYPE, (payload, context) -> {
			Networking.JarLocation jarLocation = payload.jarLocation();
			
			context.server().execute(() -> {
				try {
					WorldJarBlockEntity jar = Objects.requireNonNull(context.server().getLevel(jarLocation.dimension()))
							.getBlockEntity(jarLocation.blockPos(), Blocks.WORLD_JAR.type())
							.orElseThrow();
					jar.updateBlockStates(context.server());
					
					for (ServerPlayer player : PlayerLookup.tracking(jar)) {
						jar.sendJarChunks(player);
					}
				} catch (NullPointerException e) {
					throw new RuntimeException(e);
				} catch (NoSuchElementException ignored) {}
			});
		});
	}
}
