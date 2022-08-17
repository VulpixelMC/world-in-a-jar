package dev.cursedmc.wij.impl.plugin.wthit.component.block

import dev.cursedmc.wij.api.block.entity.WorldJarBlockEntity
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
		tooltip.addLine(Text.literal("Position: $subPos"))
	}
}
