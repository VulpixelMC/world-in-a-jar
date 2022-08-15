package dev.cursedmc.wij.api.block.entity

import dev.cursedmc.wij.api.Initializable
import dev.cursedmc.wij.api.block.Blocks
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder

object BlockEntityTypes : Initializable {
	val WORLD_JAR: BlockEntityType<WorldJarBlockEntity> = Registry.register(
		Registry.BLOCK_ENTITY_TYPE,
		Identifier("worldinajar", "world_jar"),
		QuiltBlockEntityTypeBuilder.create(::WorldJarBlockEntity, Blocks.WORLD_JAR).build(),
	)
}
