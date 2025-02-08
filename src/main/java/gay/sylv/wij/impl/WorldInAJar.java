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
package gay.sylv.wij.impl;

import gay.sylv.wij.api.entity.event.ServerPlayerEventsExtra;
import gay.sylv.wij.impl.attachment.Attachments;
import gay.sylv.wij.impl.block.Blocks;
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import gay.sylv.wij.impl.component.Components;
import gay.sylv.wij.impl.dimension.Dimensions;
import gay.sylv.wij.impl.duck.PlayerWithEnteredJar;
import gay.sylv.wij.impl.item.Items;
import gay.sylv.wij.impl.network.Networking;
import gay.sylv.wij.impl.util.Constants;
import gay.sylv.wij.impl.util.jar.JarPlacer;
import gay.sylv.wij.impl.worldgen.JarChunkGenerator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static gay.sylv.wij.impl.util.Constants.modId;

public final class WorldInAJar implements ModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_NAME);
	private static EnvType environment;
	
	@Override
	public void onInitialize() {
		LOGGER.info("Initializing {}", Constants.MOD_NAME);
		environment = FabricLoader.getInstance().getEnvironmentType();
		
		Components.INSTANCE.initialize();
		
		Attachments.INSTANCE.initialize();
		
		Blocks.INSTANCE.initialize();
		
		Items.INSTANCE.initialize();
		
		Networking.INSTANCE.initialize();
		
		Registry.register(BuiltInRegistries.CHUNK_GENERATOR, modId("jar"), JarChunkGenerator.CODEC);
		
		ServerLifecycleEvents.SERVER_STARTED.register(WorldInAJar::onServerStart);
		ServerLifecycleEvents.SERVER_STOPPED.register(WorldInAJar::onServerStop);
		
		ServerPlayerEventsExtra.AFTER_SPAWN.register((connection, player, cookie) -> {
			if (!(player instanceof FakePlayer) && player.level().dimension().equals(Dimensions.JAR)) {
				createFakePlayer((ServerLevel) player.level(), player);
			}
		});
		
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			if (!(handler.getPlayer() instanceof FakePlayer) && handler.getPlayer().level().dimension().equals(Dimensions.JAR)) {
				removeFakePlayer(handler.getPlayer().serverLevel(), handler.getPlayer());
			}
		});
		
		LOGGER.info("Finished loading {}", Constants.MOD_NAME);
	}
	
	public static void createFakePlayer(ServerLevel jarLevel, ServerPlayer player) {
		((PlayerWithEnteredJar) player).worldinajar$getJarLocation().ifPresent(jarLocation -> {
			ServerLevel outsideJarLevel = Objects.requireNonNull(Objects.requireNonNull(jarLevel.getServer()).getLevel(jarLocation.dimension()));
			Optional<WorldJarBlockEntity> optionalJar = outsideJarLevel.getBlockEntity(jarLocation.blockPos(), Blocks.WORLD_JAR.type());
			if (optionalJar.isEmpty()) return;
			WorldJarBlockEntity jar = optionalJar.get();
			FakePlayer fakePlayer = jar.getOrCreateFakePlayer(outsideJarLevel, player);
			Objects.requireNonNull(fakePlayer.getAttribute(Attributes.SCALE)).setBaseValue(jar.getVisualScale());
			fakePlayer.setServerLevel(outsideJarLevel);
			PlayerList playerList = jarLevel
					.getServer()
					.getPlayerList();
			playerList.broadcastAll(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(fakePlayer)));
			outsideJarLevel.addNewPlayer(fakePlayer);
		});
	}
	
	public static void removeFakePlayerWithJar(WorldJarBlockEntity jar, ServerLevel outsideJarLevel, ServerPlayer player) {
		Optional<FakePlayer> optionalFakePlayer = getFakePlayer(jar, player);
		if (optionalFakePlayer.isEmpty()) return;
		FakePlayer fakePlayer = optionalFakePlayer.get();
		PlayerList playerList = outsideJarLevel
				.getServer()
				.getPlayerList();
		jar.getFakePlayers().remove(fakePlayer.getUUID());
		playerList.remove(fakePlayer);
	}
	
	public static void removeFakePlayer(ServerLevel jarLevel, ServerPlayer player) {
		jarLevel.getServer().execute(() -> ((PlayerWithEnteredJar) player).worldinajar$getJarLocation().ifPresent(jarLocation -> {
			ServerLevel outsideJarLevel = Objects.requireNonNull(Objects.requireNonNull(jarLevel.getServer()).getLevel(jarLocation.dimension()));
			Optional<WorldJarBlockEntity> optionalJar = outsideJarLevel.getBlockEntity(jarLocation.blockPos(), Blocks.WORLD_JAR.type());
			if (optionalJar.isEmpty()) return;
			WorldJarBlockEntity jar = optionalJar.get();
			removeFakePlayerWithJar(jar, outsideJarLevel, player);
		}));
	}
	
	public static Optional<FakePlayer> getFakePlayer(WorldJarBlockEntity jar, ServerPlayer player) {
		UUID uuid = UUID.nameUUIDFromBytes(player.getName().getString().getBytes(StandardCharsets.UTF_8));
		return Optional.ofNullable(jar.getFakePlayers().get(uuid));
	}
	
	public static Optional<EnvType> getEnvironment() {
		return Optional.ofNullable(environment);
	}
	
	public static boolean isEnvType(EnvType type) {
		return getEnvironment().orElseThrow() == type;
	}
	
	public static boolean isClient() {
		return isEnvType(EnvType.CLIENT);
	}
	
	public static ModContainer getModContainer() {
		return FabricLoader.getInstance().getModContainer(Constants.MOD_ID).orElseThrow();
	}
	
	public static Logger getLogger(Class<?> clazz) {
		return LoggerFactory.getLogger(Constants.MOD_NAME + "/" + clazz.getName());
	}
	
	private static void onServerStart(MinecraftServer server) {
		try {
			JarPlacer.initialize(server);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void onServerStop(MinecraftServer server) {
		try {
			JarPlacer.clear();
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
