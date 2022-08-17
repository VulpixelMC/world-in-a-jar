package dev.cursedmc.wij.api.block

import dev.cursedmc.wij.api.block.entity.BlockEntityTypes
import dev.cursedmc.wij.api.block.entity.WorldJarBlockEntity
import dev.cursedmc.wij.api.gui.screen.WorldJarScreen
import net.fabricmc.api.EnvType
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader

open class WorldJarBlock(settings: Settings?) : Block(settings), BlockEntityProvider {
	override fun createBlockEntity(pos: BlockPos?, state: BlockState?): BlockEntity {
		var world: World? = null
		if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT) {
			world = MinecraftClient.getInstance().world
		}
		return WorldJarBlockEntity(pos, state, world)
	}
	
	@Deprecated("Deprecated in Java")
	override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
		if (world.isClient) {
			MinecraftClient.getInstance().setScreen(WorldJarScreen(world.getBlockEntity(pos, BlockEntityTypes.WORLD_JAR).get()))
		}
		
		return ActionResult.SUCCESS
	}
}
