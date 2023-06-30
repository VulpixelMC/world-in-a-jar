/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package gay.sylv.wij.impl.block.entity

import gay.sylv.wij.impl.WIJConstants.id
import gay.sylv.wij.impl.block.Blocks
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder

object BlockEntityTypes : gay.sylv.wij.api.Initializable {
	val WORLD_JAR: BlockEntityType<WorldJarBlockEntity> = Registry.register(
		Registries.BLOCK_ENTITY_TYPE,
		id("world_jar"),
		QuiltBlockEntityTypeBuilder.create(::WorldJarBlockEntity, Blocks.WORLD_JAR).build(),
	)
}
