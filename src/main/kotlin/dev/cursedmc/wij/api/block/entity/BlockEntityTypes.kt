/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package dev.cursedmc.wij.api.block.entity

import dev.cursedmc.wij.api.Initializable
import dev.cursedmc.wij.api.block.Blocks
import dev.cursedmc.wij.impl.WIJConstants.id
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.registry.Registry
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder

object BlockEntityTypes : Initializable {
	val WORLD_JAR: BlockEntityType<WorldJarBlockEntity> = Registry.register(
		Registry.BLOCK_ENTITY_TYPE,
		id("world_jar"),
		QuiltBlockEntityTypeBuilder.create(::WorldJarBlockEntity, Blocks.WORLD_JAR).build(),
	)
}
