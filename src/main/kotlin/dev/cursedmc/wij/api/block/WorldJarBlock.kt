package dev.cursedmc.wij.api.block

import dev.cursedmc.wij.api.block.entity.BlockEntityTypes
import dev.cursedmc.wij.api.block.entity.WorldJarBlockEntity
import dev.cursedmc.wij.api.block.entity.render.WorldJarBlockEntityRenderer
import dev.cursedmc.wij.api.dimension.DimensionTypes
import dev.cursedmc.wij.api.fake.FakeClientWorld
import dev.cursedmc.wij.api.gui.screen.WorldJarScreen
import net.fabricmc.api.EnvType
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.HolderLookup
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.DynamicRegistryManager
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import net.minecraft.world.dimension.DimensionType
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader

open class WorldJarBlock(settings: Settings?) : Block(settings), BlockEntityProvider {
	override fun createBlockEntity(pos: BlockPos?, state: BlockState?): BlockEntity? {
		val entity = BlockEntityTypes.WORLD_JAR.instantiate(pos, state)
		var subworld: World? = null
		if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT) {
			if (WorldJarBlock.subworld == null) {
				WorldJarBlock.subworld = MinecraftClient.getInstance().world
			}
			subworld = WorldJarBlock.subworld
		}
		entity?.subworld = subworld
		return entity
	}
	
	@Deprecated("Deprecated in Java")
	override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
		if (world.isClient) {
			MinecraftClient.getInstance().setScreen(WorldJarScreen(world.getBlockEntity(pos, BlockEntityTypes.WORLD_JAR).get()))
		}
		
		return ActionResult.SUCCESS
	}
	
	companion object {
		var subworld: ClientWorld? = null
	}
}
