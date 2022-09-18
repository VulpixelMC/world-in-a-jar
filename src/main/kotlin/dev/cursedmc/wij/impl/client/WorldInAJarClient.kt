/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package dev.cursedmc.wij.impl.client

import dev.cursedmc.wij.api.block.Blocks
import dev.cursedmc.wij.api.block.entity.render.BlockEntityRenderers
import dev.cursedmc.wij.api.network.s2c.S2CPackets
import net.minecraft.client.render.RenderLayer
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap

class WorldInAJarClient : ClientModInitializer {
	override fun onInitializeClient(mod: ModContainer?) {
		S2CPackets.initialize()
		
		BlockEntityRenderers.initialize()
		
		BlockRenderLayerMap.put(RenderLayer.getTranslucent(), Blocks.WORLD_JAR)
	}
}
