/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package gay.sylv.wij.impl.block.entity.render

import gay.sylv.wij.impl.block.entity.BlockEntityTypes
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import org.quiltmc.loader.api.minecraft.ClientOnly

@ClientOnly
object BlockEntityRenderers : gay.sylv.wij.api.Initializable {
	init {
		BlockEntityRendererFactories.register(
			BlockEntityTypes.WORLD_JAR,
			::WorldJarBlockEntityRenderer,
		)
	}
}
