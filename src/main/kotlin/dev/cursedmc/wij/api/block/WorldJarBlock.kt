package dev.cursedmc.wij.api.block

import dev.cursedmc.wij.api.block.entity.WorldJarBlockEntity
import net.fabricmc.api.EnvType
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings

object WorldJarBlock : Block(
	QuiltBlockSettings
		.copyOf(Blocks.BEACON)
		.luminance(0)
), BlockEntityProvider {
	override fun createBlockEntity(pos: BlockPos?, state: BlockState?): BlockEntity {
		var world: World? = null
		if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT) {
			world = MinecraftClient.getInstance().world
		}
		return WorldJarBlockEntity(pos, state, world)
	}
}