/**
 * World In a Jar
 * Copyright (C) 2023  Sylv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package gay.sylv.wij.impl.item.group

import gay.sylv.wij.impl.WIJConstants.MOD_ID
import gay.sylv.wij.impl.WIJConstants.id
import gay.sylv.wij.impl.block.Blocks
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text

object ItemGroups : gay.sylv.wij.api.Initializable {
	val WORLD_JAR: ItemGroup = FabricItemGroup.builder()
		.name(Text.translatable("${MOD_ID}.world_jar_group"))
		.icon { return@icon ItemStack(Blocks.WORLD_JAR_ITEM) }
		.entries { _, collector ->
			collector.addItem(Blocks.WORLD_JAR_ITEM)
			collector.addItem(Blocks.SUSSYSTONE_ITEM)
		}
		.build()
	
	override fun initialize() {
		Registry.register(Registries.ITEM_GROUP, id("world_jar"), WORLD_JAR)
	}
}
