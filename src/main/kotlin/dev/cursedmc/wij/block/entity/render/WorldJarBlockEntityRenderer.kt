/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package dev.cursedmc.wij.block.entity.render

import dev.cursedmc.wij.block.entity.WorldJarBlockEntity
import dev.cursedmc.wij.impl.scale
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos

open class WorldJarBlockEntityRenderer(private val ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<WorldJarBlockEntity> {
	override fun render(
		entity: WorldJarBlockEntity,
		tickDelta: Float,
		matrices: MatrixStack,
		vertexConsumers: VertexConsumerProvider?,
		light: Int,
		overlay: Int
	) {
		val max = entity.magnitude - 1
		
		matrices.scale(entity.scale)
		
		// THE MODFEST IS IN 3 MINUTES HELP WHY WON'T IT UPLOAD PLEASE
		
		for (x in 0..max) {
			for (y in 1..max) {
				for (z in 0..max) {
					matrices.push()
					
					if (x == 0) matrices.translate(0.001, 0.0, 0.0) else if (x == max) matrices.translate(-0.001, 0.0 ,0.0)
					if (y == max - 1) matrices.translate(0.0, -0.001, 0.0)
					if (z == 0) matrices.translate(0.0, 0.0, 0.001) else if (z == max) matrices.translate(0.0, 0.0, -0.001)
					
					matrices.translate(x.toDouble(), y.toDouble(), z.toDouble())
					
					val pos = BlockPos(x, y, z)
					val state = entity.blockStates[pos.asLong()]
					
					if (state != null) {
						ctx.renderManager.renderBlockAsEntity(state, matrices, vertexConsumers, light, overlay)
					}
					
					matrices.pop()
				}
			}
		}
	}
}
