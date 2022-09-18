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
