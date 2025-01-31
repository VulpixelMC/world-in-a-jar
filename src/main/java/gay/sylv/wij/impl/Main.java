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

import gay.sylv.wij.impl.block.Blocks;
import gay.sylv.wij.impl.item.Items;
import gay.sylv.wij.impl.network.Networking;
import gay.sylv.wij.impl.util.Constants;
import gay.sylv.wij.impl.worldgen.JarChunkGenerator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static gay.sylv.wij.impl.util.Constants.modId;

public final class Main implements ModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_NAME);
	private static EnvType environment;
	
	@Override
	public void onInitialize() {
		LOGGER.info("Initializing {}", Constants.MOD_NAME);
		environment = FabricLoader.getInstance().getEnvironmentType();
		
		Blocks.INSTANCE.initialize();
		
		Items.INSTANCE.initialize();
		
		Networking.INSTANCE.initialize();
		
		Registry.register(BuiltInRegistries.CHUNK_GENERATOR, modId("jar"), JarChunkGenerator.CODEC);
		
		LOGGER.info("Finished loading {}", Constants.MOD_NAME);
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
}
