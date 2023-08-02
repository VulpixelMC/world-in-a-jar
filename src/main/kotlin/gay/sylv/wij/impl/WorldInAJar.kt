/**
 * World In a Jar
 * Copyright (C) 2023  Sylv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
@file:Suppress("unused")
package gay.sylv.wij.impl

import gay.sylv.wij.impl.WIJConstants.id
import gay.sylv.wij.impl.addon.Addons
import gay.sylv.wij.impl.block.Blocks
import gay.sylv.wij.impl.block.entity.BlockEntityTypes
import gay.sylv.wij.impl.dimension.DimensionTypes
import gay.sylv.wij.impl.generator.VoidChunkGenerator
import gay.sylv.wij.impl.item.group.ItemGroups
import gay.sylv.wij.impl.network.Networking
import gay.sylv.wij.impl.server.WorldInAJarServer
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class WorldInAJar : ModInitializer {
	override fun onInitialize(mod: ModContainer) {
		LOGGER.info("loading {}", mod.metadata().name())
		
		// integration
		Addons.initialize()
		
		// networking
		Networking.initialize()
		
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
	}
}
