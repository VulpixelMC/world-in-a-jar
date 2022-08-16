package dev.cursedmc.wij.api.block

import dev.cursedmc.wij.api.Initializable
import dev.cursedmc.wij.api.WIJConstants.id
import dev.cursedmc.wij.api.block.item.WorldJarBlockItem
import dev.cursedmc.wij.api.item.group.ItemGroups
import net.minecraft.block.Blocks
import net.minecraft.util.Rarity
import net.minecraft.util.registry.Registry
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings

object Blocks : Initializable {
	val WORLD_JAR: WorldJarBlock = Registry.register(
		Registry.BLOCK,
		id("world_jar"),
		WorldJarBlock(
			QuiltBlockSettings
				.copyOf(Blocks.BEACON)
				.luminance(0),
		),
	)
	
	val WORLD_JAR_ITEM: WorldJarBlockItem = Registry.register(
		Registry.ITEM,
		id("world_jar"),
		WorldJarBlockItem(
			WORLD_JAR,
			QuiltItemSettings()
				.group(ItemGroups.WORLD_JAR)
				.rarity(Rarity.UNCOMMON)
				.maxCount(1),
		),
	)
}
