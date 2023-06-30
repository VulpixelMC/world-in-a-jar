/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
@file:Suppress("unused")
package gay.sylv.wij.impl.server.dedicated

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
