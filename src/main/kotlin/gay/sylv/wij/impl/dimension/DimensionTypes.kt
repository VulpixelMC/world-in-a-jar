/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package gay.sylv.wij.impl.dimension

import gay.sylv.wij.impl.WIJConstants.id
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.World
import net.minecraft.world.dimension.DimensionOptions
import net.minecraft.world.dimension.DimensionType

object DimensionTypes : gay.sylv.wij.api.Initializable {
	lateinit var WORLD_JAR: RegistryKey<DimensionOptions>
	lateinit var WORLD_JAR_WORLD: RegistryKey<World>
	@Suppress("MemberVisibilityCanBePrivate")
	lateinit var WORLD_JAR_TYPE_KEY: RegistryKey<DimensionType>
	
	override fun initialize() {
		WORLD_JAR = RegistryKey.of(RegistryKeys.DIMENSION, id("jar"))
		WORLD_JAR_WORLD = RegistryKey.of(RegistryKeys.WORLD, id("jar"))
		WORLD_JAR_TYPE_KEY = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, id("jar_type"))
	}
}
