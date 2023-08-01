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
@file:Suppress("MemberVisibilityCanBePrivate")

package gay.sylv.wij.impl.block

import gay.sylv.wij.impl.WIJConstants.id
import gay.sylv.wij.impl.block.item.WorldJarBlockItem
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.item.BlockItem
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Rarity
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings

object Blocks : gay.sylv.wij.api.Initializable {
	val WORLD_JAR: WorldJarBlock = Registry.register(
		Registries.BLOCK,
		id("world_jar"),
		WorldJarBlock(
			QuiltBlockSettings
				.copyOf(Blocks.BEACON)
				.requiresTool()
				.strength(1.0F, 4.0F)
				.luminance(0),
		),
	)
	
	val WORLD_JAR_ITEM: WorldJarBlockItem = Registry.register(
		Registries.ITEM,
		id("world_jar"),
		WorldJarBlockItem(
			WORLD_JAR,
			QuiltItemSettings()
				.rarity(Rarity.UNCOMMON)
				.maxCount(1),
		),
	)
	
	val SUSSYSTONE: Block = Registry.register(
		Registries.BLOCK,
		id("sussystone"),
		Block(
			QuiltBlockSettings
				.create()
				.requiresTool()
				.strength(2.0F, 6.0F)
				.sounds(BlockSoundGroup.STONE),
		),
	)
	
	val SUSSYSTONE_ITEM: BlockItem = Registry.register(
		Registries.ITEM,
		Registries.BLOCK.getId(SUSSYSTONE),
		BlockItem(
			SUSSYSTONE,
			QuiltItemSettings()
		)
	)
}
