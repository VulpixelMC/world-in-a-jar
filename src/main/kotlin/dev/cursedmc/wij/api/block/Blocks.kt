package dev.cursedmc.wij.api.block

import dev.cursedmc.wij.api.Initializable
import dev.cursedmc.wij.api.WIJConstants.id
import dev.cursedmc.wij.api.item.group.ItemGroups
import net.minecraft.item.BlockItem
import net.minecraft.util.Rarity
import net.minecraft.util.registry.Registry
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings

object Blocks : Initializable {
	val WORLD_JAR: WorldJarBlock = Registry.register(
		Registry.BLOCK,
		id("world_jar"),
		WorldJarBlock,
	)

	val WORLD_JAR_ITEM: BlockItem = Registry.register(
		Registry.ITEM,
		id("world_jar"),
		BlockItem(
			WorldJarBlock,
			QuiltItemSettings()
				.group(ItemGroups.WORLD_JAR)
				.rarity(Rarity.UNCOMMON),
		),
	)
}
