package dev.cursedmc.wij.api.item.group

import dev.cursedmc.wij.api.Initializable
import dev.cursedmc.wij.api.block.Blocks
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import org.quiltmc.qsl.item.group.api.QuiltItemGroup

object ItemGroups : Initializable {
	val WORLD_JAR: ItemGroup = QuiltItemGroup.builder(Identifier("worldinajar", "world_jar"))
		.icon { return@icon ItemStack(Blocks.WORLD_JAR_ITEM) }
		.build()
}
