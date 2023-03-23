/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package dev.cursedmc.wij.impl.plugin.wthit

import dev.cursedmc.wij.block.entity.WorldJarBlockEntity
import dev.cursedmc.wij.impl.plugin.wthit.component.block.WorldJarBlockComponentProvider
import mcp.mobius.waila.api.IRegistrar
import mcp.mobius.waila.api.IWailaPlugin
import mcp.mobius.waila.api.TooltipPosition

@Suppress("unused")
class WorldInAJarWailaPlugin : IWailaPlugin {
	override fun register(registrar: IRegistrar) {
		registrar.addComponent(WorldJarBlockComponentProvider(), TooltipPosition.BODY, WorldJarBlockEntity::class.java)
	}
}
