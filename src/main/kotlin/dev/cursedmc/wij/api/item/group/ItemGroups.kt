/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package dev.cursedmc.wij.api.item.group

import dev.cursedmc.wij.api.Initializable
import dev.cursedmc.wij.api.block.Blocks
import dev.cursedmc.wij.impl.WIJConstants.id
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import org.quiltmc.qsl.item.group.api.QuiltItemGroup

object ItemGroups : Initializable {
	val WORLD_JAR: ItemGroup = QuiltItemGroup.builder(id("world_jar"))
		.icon { return@icon ItemStack(Blocks.WORLD_JAR_ITEM) }
		.build()
}
