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
