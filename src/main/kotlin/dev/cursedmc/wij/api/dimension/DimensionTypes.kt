/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package dev.cursedmc.wij.api.dimension

import dev.cursedmc.wij.api.Initializable
import dev.cursedmc.wij.api.generator.VoidChunkGenerator
import dev.cursedmc.wij.impl.WIJConstants.id
import net.minecraft.tag.BlockTags
import net.minecraft.util.Holder
import net.minecraft.util.Identifier
import net.minecraft.util.math.intprovider.ConstantIntProvider
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import net.minecraft.world.dimension.DimensionOptions
import net.minecraft.world.dimension.DimensionType
import java.util.*

object DimensionTypes : Initializable {
	lateinit var WORLD_JAR: RegistryKey<DimensionOptions>
	lateinit var WORLD_JAR_WORLD: RegistryKey<World>
	lateinit var WORLD_JAR_TYPE_KEY: RegistryKey<DimensionType>
	
	override fun initialize() {
		WORLD_JAR = RegistryKey.of(Registry.DIMENSION_KEY, id("jar"))
		WORLD_JAR_WORLD = RegistryKey.of(Registry.WORLD_KEY, id("jar"))
		WORLD_JAR_TYPE_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, id("jar_type"))
	}
}
