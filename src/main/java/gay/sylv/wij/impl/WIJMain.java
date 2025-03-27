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

import gay.sylv.wij.impl.attachment.Attachments;
import gay.sylv.wij.impl.block.Blocks;
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import gay.sylv.wij.impl.block.tag.BlockTags;
import gay.sylv.wij.impl.component.Components;
import gay.sylv.wij.impl.datagen.DynamicDataGenerator;
import gay.sylv.wij.impl.dimension.Dimensions;
import gay.sylv.wij.impl.duck.PlayerWithEnteredJar;
import gay.sylv.wij.impl.gui.creative_tab.CreativeModeTabs;
import gay.sylv.wij.impl.item.BedrockPickaxeItem;
import gay.sylv.wij.impl.item.Items;
import gay.sylv.wij.impl.item.tag.ItemTags;
import gay.sylv.wij.impl.network.Networking;
import gay.sylv.wij.impl.platform.PlatformProvider;
import gay.sylv.wij.impl.platform.side.DistSide;
import gay.sylv.wij.impl.util.Constants;
import gay.sylv.wij.impl.util.Initializable;
import gay.sylv.wij.impl.util.jar.JarPlacer;
import gay.sylv.wij.impl.worldgen.JarChunkGenerator;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static gay.sylv.wij.impl.util.Constants.modId;

public final class WIJMain implements Initializable {
	public static PlatformProvider platformProvider;
	
	private static final Logger LOGGER = getLogger(WIJMain.class);
	
	@Override
	public void initialize() {
		LOGGER.info("Initializing {}", Constants.MOD_NAME);
		
		Components.INSTANCE.initialize();
		
		Attachments.INSTANCE.initialize();
		
		Blocks.INSTANCE.initialize();
		Items.INSTANCE.initialize();
		DynamicDataGenerator.INSTANCE.initialize();
		CreativeModeTabs.INSTANCE.initialize();
		
		Networking.INSTANCE.initialize();
		
		Registry.register(BuiltInRegistries.CHUNK_GENERATOR, modId("jar"), JarChunkGenerator.CODEC);
		
		LOGGER.info("Finished loading {}", Constants.MOD_NAME);
	}
	
	public static void createFakePlayer(ServerLevel jarLevel, ServerPlayer player) {
		((PlayerWithEnteredJar) player).worldinajar$getJarLocation().ifPresent(jarLocation -> {
			ServerLevel outsideJarLevel = Objects.requireNonNull(Objects.requireNonNull(jarLevel.getServer()).getLevel(jarLocation.dimension()));
			Optional<WorldJarBlockEntity> optionalJar = outsideJarLevel.getBlockEntity(jarLocation.blockPos(), Blocks.WORLD_JAR.type());
			if (optionalJar.isEmpty()) return;
			WorldJarBlockEntity jar = optionalJar.get();
			FakePlayer fakePlayer = jar.getOrCreateFakePlayer(outsideJarLevel, player);
			
			AttributeInstance attribute = Objects.requireNonNull(fakePlayer.getAttribute(Attributes.SCALE));
			attribute.removeModifier(modId("tiny"));
			AttributeModifier modifier = new AttributeModifier(modId("tiny"), jar.getVisualScale() - 1.0d, AttributeModifier.Operation.ADD_VALUE);
			attribute.addTransientModifier(modifier);
			
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
		removeKnownFakePlayerWithJar(jar, outsideJarLevel, fakePlayer);
	}
	
	public static void removeKnownFakePlayerWithJar(WorldJarBlockEntity jar, ServerLevel outsideJarLevel, FakePlayer fakePlayer) {
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
	
	public static boolean isDistSide(DistSide side) {
		return platformProvider.getDistSide() == side;
	}
	
	public static boolean isClient() {
		return isDistSide(DistSide.CLIENT);
	}
	
	public static ModContainer getModContainer() {
		return FabricLoader.getInstance().getModContainer(Constants.MOD_ID).orElseThrow();
	}
	
	public static Logger getLogger(Class<?> clazz) {
		return LoggerFactory.getLogger(Constants.MOD_NAME + "/" + clazz.getName());
	}
	
	public static void onServerStart(MinecraftServer server) {
		try {
			JarPlacer.initialize(server);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void onServerStop(MinecraftServer server) {
		try {
			JarPlacer.clear();
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean beforePlayerBlockBreak(Level level, Player player, BlockPos pos, BlockState state) {
		if (player.getMainHandItem().is(ItemTags.CHIPS_OR_DESTROYS_UNBREAKABLE) && BedrockPickaxeItem.isBedrockMineable(level, state, player)) {
			BedrockPickaxeItem.dropBedrockShard(player.getMainHandItem(), level, state, pos, player);
		}
		
		return !state.is(BlockTags.UNBREAKABLE) || player.getAbilities().instabuild;
	}
	
	public static void onServerStopping(MinecraftServer server) {
		server.getPlayerList().getPlayers().forEach(player -> {
			if (!(player instanceof FakePlayer) && player.level().dimension().equals(Dimensions.JAR)) {
				removeFakePlayer(player.serverLevel(), player);
			}
		});
	}
	
	public static void onPlayerDisconnect(ServerGamePacketListenerImpl handler, MinecraftServer server) {
		if (!(handler.getPlayer() instanceof FakePlayer) && handler.getPlayer().level().dimension().equals(Dimensions.JAR)) {
			removeFakePlayer(handler.getPlayer().serverLevel(), handler.getPlayer());
		}
	}
	
	public static void afterPlayerSpawn(ServerPlayer player) {
		if (!(player instanceof FakePlayer) && player.level().dimension().equals(Dimensions.JAR)) {
			createFakePlayer((ServerLevel) player.level(), player);
		}
		
		if (!(player instanceof FakePlayer) && !player.level().dimension().equals(Dimensions.JAR)) {
			Objects.requireNonNull(player.getAttribute(Attributes.SCALE)).removeModifier(modId("tiny"));
		}
	}
}
