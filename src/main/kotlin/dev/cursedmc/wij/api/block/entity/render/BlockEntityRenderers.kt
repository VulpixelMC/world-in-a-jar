package dev.cursedmc.wij.api.block.entity.render

import dev.cursedmc.wij.api.Initializable
import dev.cursedmc.wij.api.block.entity.BlockEntityTypes
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry

object BlockEntityRenderers : Initializable {
	init {
		BlockEntityRendererRegistry.register(
			BlockEntityTypes.WORLD_JAR,
			::WorldJarBlockEntityRenderer,
		)
	}
}
