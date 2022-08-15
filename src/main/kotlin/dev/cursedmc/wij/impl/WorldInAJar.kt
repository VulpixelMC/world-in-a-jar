package dev.cursedmc.wij.impl

import dev.cursedmc.wij.api.block.Blocks
import dev.cursedmc.wij.api.block.entity.BlockEntityTypes
import dev.cursedmc.wij.api.generator.VoidChunkGenerator
import dev.cursedmc.wij.api.item.group.ItemGroups
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class WorldInAJar : ModInitializer {
	override fun onInitialize(mod: ModContainer) {
		LOGGER.info("loading {}", mod.metadata().name())
		
		Registry.register(Registry.CHUNK_GENERATOR, Identifier("worldinajar", "void"), VoidChunkGenerator.CODEC)
		
		Blocks.initialize()
		BlockEntityTypes.initialize()
		ItemGroups.initialize()
		
		LOGGER.info("hi yes we have loaded {}", mod.metadata().name())
	}

	companion object {
		// This logger is used to write text to the console and the log file.
		// It is considered best practice to use your mod name as the logger's name.
		// That way, it's clear which mod wrote info, warnings, and errors.
		val LOGGER: Logger = LoggerFactory.getLogger("World In A Jar")
	}
}
