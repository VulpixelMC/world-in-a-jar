package dev.cursedmc.wij.impl.server.dedicated

import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.server.DedicatedServerModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class WorldInAJarDedicatedServer : DedicatedServerModInitializer {
	override fun onInitializeServer(mod: ModContainer) {
		LOGGER.info("initialized server")
		
		LOGGER.info("loaded server")
	}
	
	companion object {
		// This logger is used to write text to the console and the log file.
		// It is considered best practice to use your mod name as the logger's name.
		// That way, it's clear which mod wrote info, warnings, and errors.
		val LOGGER: Logger = LoggerFactory.getLogger("World In A Jar/DedicatedServer")
	}
}
