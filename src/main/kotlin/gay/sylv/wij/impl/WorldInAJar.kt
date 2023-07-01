/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
@file:Suppress("unused")
package gay.sylv.wij.impl

import gay.sylv.wij.impl.WIJConstants.id
import gay.sylv.wij.impl.block.Blocks
import gay.sylv.wij.impl.block.entity.BlockEntityTypes
import gay.sylv.wij.impl.dimension.DimensionTypes
import gay.sylv.wij.impl.generator.VoidChunkGenerator
import gay.sylv.wij.impl.item.group.ItemGroups
import gay.sylv.wij.impl.network.Networking
import gay.sylv.wij.impl.network.c2s.C2SPackets
import gay.sylv.wij.impl.server.WorldInAJarServer
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.loader.api.QuiltLoader
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class WorldInAJar : ModInitializer {
	override fun onInitialize(mod: ModContainer) {
		LOGGER.info("loading {}", mod.metadata().name())
		
		if (QuiltLoader.isModLoaded("sodium")) {
			HAS_SODIUM = true
		}
		
		// networking
		Networking.initialize()
		C2SPackets.initialize()
		
		// initialize server
		WorldInAJarServer.initialize()
		
		// initialize dimensions & chunk generators
		DimensionTypes.initialize()
		Registry.register(Registries.CHUNK_GENERATOR, id("jar"), VoidChunkGenerator.CODEC)
		
		// blocks/items
		Blocks.initialize()
		BlockEntityTypes.initialize()
		ItemGroups.initialize()
		
		LOGGER.info("successfully loaded {}", mod.metadata().name())
	}

	companion object {
		// This logger is used to write text to the console and the log file.
		// It is considered best practice to use your mod name as the logger's name.
		// That way, it's clear which mod wrote info, warnings, and errors.
		val LOGGER: Logger = LoggerFactory.getLogger("World In A Jar")
		var HAS_SODIUM: Boolean = false
	}
}
