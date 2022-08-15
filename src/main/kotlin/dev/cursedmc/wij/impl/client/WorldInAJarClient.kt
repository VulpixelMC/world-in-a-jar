package dev.cursedmc.wij.impl.client

import dev.cursedmc.wij.api.block.Blocks
import dev.cursedmc.wij.api.block.entity.render.BlockEntityRenderers
import net.minecraft.client.render.RenderLayer
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap

class WorldInAJarClient : ClientModInitializer {
	override fun onInitializeClient(mod: ModContainer?) {
		BlockEntityRenderers.initialize()
		
		BlockRenderLayerMap.put(RenderLayer.getTranslucent(), Blocks.WORLD_JAR)
	}
}
