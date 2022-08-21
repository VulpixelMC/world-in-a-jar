package dev.cursedmc.wij.impl

import dev.cursedmc.wij.api.block.Blocks
import dev.cursedmc.wij.api.block.entity.BlockEntityTypes
import dev.cursedmc.wij.api.dimension.DimensionTypes
import dev.cursedmc.wij.api.generator.VoidChunkGenerator
import dev.cursedmc.wij.api.item.group.ItemGroups
import dev.cursedmc.wij.api.network.c2s.C2SPackets
import dev.cursedmc.wij.api.network.s2c.S2CPackets
import dev.cursedmc.wij.impl.WIJConstants.id
import dev.cursedmc.wij.impl.server.WorldInAJarServer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
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
		
		C2SPackets.initialize()
		S2CPackets.initialize()
		
		WorldInAJarServer.initialize()
		
		DimensionTypes.initialize()
		
		Registry.register(Registry.CHUNK_GENERATOR, id("jar"), VoidChunkGenerator.CODEC)
		
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
		var HAS_SODIUM: Boolean = false
	}
}
