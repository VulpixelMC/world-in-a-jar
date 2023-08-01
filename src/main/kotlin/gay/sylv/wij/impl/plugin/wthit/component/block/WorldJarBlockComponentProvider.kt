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
package gay.sylv.wij.impl.plugin.wthit.component.block

import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity
import mcp.mobius.waila.api.IBlockAccessor
import mcp.mobius.waila.api.IBlockComponentProvider
import mcp.mobius.waila.api.IPluginConfig
import mcp.mobius.waila.api.ITooltip
import net.minecraft.text.Text

class WorldJarBlockComponentProvider : IBlockComponentProvider {
	override fun appendBody(tooltip: ITooltip, accessor: IBlockAccessor, config: IPluginConfig) {
		val entity = accessor.getBlockEntity<WorldJarBlockEntity>()
		val scale = entity?.scale
		val subPos = entity?.subPos
		
		tooltip.addLine(Text.literal("Scale: $scale"))
		tooltip.addLine(Text.literal("Position: ${subPos?.toShortString()}"))
		
		if (accessor.player.hasPermissionLevel(2)) {
			tooltip.addLine(Text.literal("Locked: ${entity?.locked}"))
		}
	}
}
