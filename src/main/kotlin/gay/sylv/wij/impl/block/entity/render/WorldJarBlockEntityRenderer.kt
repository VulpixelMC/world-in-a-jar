/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package gay.sylv.wij.impl.block.entity.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.VertexBuffer
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.VertexFormats
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity
import gay.sylv.wij.impl.scale
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack

class WorldJarBlockEntityRenderer(private val ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<WorldJarBlockEntity> {
	private val subChunks: ObjectArrayList<SubChunk> = ObjectArrayList()
	
	override fun render(
		entity: WorldJarBlockEntity,
		tickDelta: Float,
		matrices: MatrixStack,
		vertexConsumers: VertexConsumerProvider?,
		light: Int,
		overlay: Int
	) {
		matrices.scale(entity.scale - 0.001f) // scale + prevent z-fighting
		
		subChunks.forEach {
			chunk ->
			if (entity.statesChanged) {
				entity.statesChanged = false
				
				chunk.bufferBuilders.get(RenderLayer.getSolid()).begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
			}
			
			chunk.vertices.bind()
			chunk.vertices.upload(chunk.bufferBuilders.get(RenderLayer.getSolid()).end())
			VertexBuffer.unbind()
		}
	}
}
