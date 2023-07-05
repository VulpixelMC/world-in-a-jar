/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package gay.sylv.wij.impl.block.entity.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity
import gay.sylv.wij.impl.scale
import net.minecraft.block.BlockRenderType
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.random.RandomGenerator
import org.joml.Matrix4f
import org.quiltmc.loader.api.minecraft.ClientOnly

@ClientOnly
class WorldJarBlockEntityRenderer(private val ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<WorldJarBlockEntity> {
	override fun render(
		entity: WorldJarBlockEntity,
		tickDelta: Float,
		matrices: MatrixStack,
		vertexConsumers: VertexConsumerProvider?,
		light: Int,
		overlay: Int
	) {
		matrices.scale(entity.scale - 0.001f) // scale + prevent z-fighting
		
		val projMat = matrices.peek().model
		loadProjectionMatrix(projMat)
		
		entity.chunks.forEach {
			val chunk = it.value
			if (entity.statesChanged) {
				entity.statesChanged = false
				
				// build chunks
				val beginPos = chunk.origin
				val offset = BlockPos(15, 15, 15)
				val randomGenerator = RandomGenerator.createLegacy()
				
				// begin building each buffer
				RenderLayer.getBlockLayers().forEach { renderLayer ->
					val bufferBuilder = chunk.buffers.get(renderLayer)
					beginBufferBuilding(bufferBuilder)
				}
				
				for (blockPos in BlockPos.iterate(beginPos, offset)) {
					val state = entity.blockStates[blockPos.asLong()]
					
					if (state != null && state.renderType != BlockRenderType.INVISIBLE) {
//						println("found block of state $state")
						// render block states
						val renderLayer = RenderLayers.getBlockLayer(state)
						val bufferBuilder = chunk.buffers.get(renderLayer)
						
						matrices.push()
						matrices.translate(blockPos.x.toFloat(), blockPos.y.toFloat(), blockPos.z.toFloat())
						ctx.renderManager.renderBlock(state, blockPos, entity.renderRegion, matrices, bufferBuilder, false, randomGenerator)
						matrices.pop()
					}
				}
				
				// end building and bind vertex buffers
				chunk.vertexBuffers.forEach { (renderLayer, buffer) ->
					val bufferBuilder = chunk.buffers.get(renderLayer)
					val renderedBuffer = bufferBuilder.endOrDiscard()
					if (renderedBuffer != null) {
						buffer.bind()
						buffer.upload(renderedBuffer)
						buffer.drawElements()
					}
				}
			}
			
			VertexBuffer.unbind()
		}
	}
	
	private fun beginBufferBuilding(bufferBuilder: BufferBuilder) {
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL)
	}
	
	private fun loadProjectionMatrix(matrix: Matrix4f) {
		RenderSystem.setProjectionMatrix(matrix, VertexSorting.DISTANCE_TO_ORIGIN)
	}
}
