package dev.cursedmc.wij.impl.plugin.wthit

import dev.cursedmc.wij.api.block.entity.WorldJarBlockEntity
import dev.cursedmc.wij.impl.plugin.wthit.component.block.WorldJarBlockComponentProvider
import mcp.mobius.waila.api.IRegistrar
import mcp.mobius.waila.api.IWailaPlugin
import mcp.mobius.waila.api.TooltipPosition

class WorldInAJarWailaPlugin : IWailaPlugin {
	override fun register(registrar: IRegistrar) {
		registrar.addComponent(WorldJarBlockComponentProvider(), TooltipPosition.BODY, WorldJarBlockEntity::class.java)
	}
}
