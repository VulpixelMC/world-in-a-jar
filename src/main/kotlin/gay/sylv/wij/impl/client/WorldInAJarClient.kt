/**
 * World In a Jar
 * Copyright (C) 2023  Sylv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
@file:Suppress("unused")
package gay.sylv.wij.impl.client

import gay.sylv.wij.impl.block.Blocks
import gay.sylv.wij.impl.block.entity.render.BlockEntityRenderers
import gay.sylv.wij.impl.client.network.ClientNetworking
import net.minecraft.client.render.RenderLayer
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class WorldInAJarClient : ClientModInitializer {
	override fun onInitializeClient(mod: ModContainer) {
		LOGGER.info("initialized client")
		
		// networking
		ClientNetworking.initialize()
		
		// rendering
		BlockEntityRenderers.initialize()
		BlockRenderLayerMap.put(RenderLayer.getTranslucent(), Blocks.WORLD_JAR)
		
		LOGGER.info("loaded client")
	}
	
	companion object {
		val LOGGER: Logger = LoggerFactory.getLogger("World In A Jar/Client")
	}
}
