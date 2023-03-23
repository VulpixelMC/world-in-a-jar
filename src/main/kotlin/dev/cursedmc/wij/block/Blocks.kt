/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package dev.cursedmc.wij.block

import dev.cursedmc.wij.api.Initializable
import dev.cursedmc.wij.block.item.WorldJarBlockItem
import dev.cursedmc.wij.impl.WIJConstants.id
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.Material
import net.minecraft.item.BlockItem
import net.minecraft.registry.Registries
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Rarity
import net.minecraft.registry.Registry
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings

object Blocks : Initializable {
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
				.of(Material.STONE)
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