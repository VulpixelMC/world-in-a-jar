/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package dev.cursedmc.wij.impl.plugin.wthit.component.block

import dev.cursedmc.wij.block.entity.WorldJarBlockEntity
import mcp.mobius.waila.api.IBlockAccessor
import mcp.mobius.waila.api.IBlockComponentProvider
import mcp.mobius.waila.api.IPluginConfig
import mcp.mobius.waila.api.ITooltip
import net.minecraft.text.Text

class WorldJarBlockComponentProvider : IBlockComponentProvider {
	override fun appendBody(tooltip: ITooltip, accessor: IBlockAccessor, config: IPluginConfig) {
		val scale = accessor.getBlockEntity<WorldJarBlockEntity>()?.magnitude
		val subPos = accessor.getBlockEntity<WorldJarBlockEntity>()?.subPos
		
		tooltip.addLine(Text.literal("Scale: $scale"))
		tooltip.addLine(Text.literal("Position: ${subPos?.toShortString()}"))
	}
}
