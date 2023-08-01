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
package gay.sylv.wij.impl.block.entity.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexBuffer
import com.mojang.blaze3d.vertex.VertexSorting
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity
import gay.sylv.wij.impl.scale
import net.minecraft.block.BlockRenderType
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.random.RandomGenerator
import org.quiltmc.loader.api.minecraft.ClientOnly
import kotlin.math.pow

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
		try {
			if (isJarRendering) {
				return
			}
			isJarRendering = true
			val magic = -0.0006f * entity.scale.toFloat()
				.pow(2.0f) + 0.085f * entity.scale - 1.183f // what the fuck https://www.wolframalpha.com/input?i=find+function+%282%2C+-1%29%2C+%2816%2C+0%29%2C+%2832%2C+1%29%2C+%2864%2C+2%29 FIXME: figure out why the stuff inside the jar is 1 block high when the scale is 2 and why it starts sinking by 1 block every 16 scale.
			matrices.scale(entity.visualScale - 0.001f) // scale + prevent z-fighting
			matrices.translate(0.001f, magic, 0.001f)
			
			val cameraPos = ctx.renderDispatcher.camera.pos
			
			if (entity.statesChanged) {
				entity.statesChanged = false
				
				entity.chunkSections.forEach {
					// build chunks
					val chunk = it.value
					val origin = chunk.origin
					val offset = BlockPos(15, 15, 15).add(origin)
					val randomGenerator = RandomGenerator.createLegacy()
					
					// begin building each buffer
					RenderLayer.getBlockLayers().forEach { renderLayer ->
						val bufferBuilder = BUFFERS.get(renderLayer)
						bufferBuilder.begin(renderLayer.drawMode, renderLayer.vertexFormat)
					}
					
					val chunkMatrices = MatrixStack() // the matrices for the chunks
					var hasTranslucent = false
					for (blockPos in BlockPos.iterate(origin, offset)) {
						val state = entity.getBlockState(blockPos)
						val fluidState = state.fluidState
						
						if (!fluidState.isEmpty) {
							val renderLayer = RenderLayers.getFluidLayer(fluidState)
							val bufferBuilder = BUFFERS.get(renderLayer)
							hasTranslucent = true
							ctx.renderManager.renderFluid(blockPos, entity.renderRegion, bufferBuilder, state, fluidState)
						}
						
						if (state.renderType != BlockRenderType.INVISIBLE) {
							// render block states
							val renderLayer = RenderLayers.getBlockLayer(state)
							val bufferBuilder = BUFFERS.get(renderLayer)
							
							chunkMatrices.push()
							chunkMatrices.translate(blockPos.x.toFloat(), blockPos.y.toFloat(), blockPos.z.toFloat())
							ctx.renderManager.renderBlock(state, blockPos, entity.renderRegion, chunkMatrices, bufferBuilder, true, randomGenerator)
							chunkMatrices.pop()
						}
					}
					
					if (hasTranslucent) {
						val bufferBuilder = BUFFERS.get(RenderLayer.getTranslucent())
						if (!bufferBuilder.isCurrentBatchEmpty) {
							bufferBuilder.setQuadSorting(
								VertexSorting.byDistanceSquared(
									cameraPos.x.toFloat() - origin.x,
									cameraPos.y.toFloat() - origin.y,
									cameraPos.z.toFloat() - origin.z,
								)
							)
						}
					}
					
					// end building and upload vertex buffers
					chunk.vertexBuffers.forEach { (renderLayer, buffer) ->
						val bufferBuilder = BUFFERS.get(renderLayer)
						val renderedBuffer = bufferBuilder.end()
						buffer.bind()
						buffer.upload(renderedBuffer)
						VertexBuffer.unbind()
					}
					
					chunk.hasBuilt = true
				}
			}
			
			// render each render layer
			BLOCK_LAYERS.forEach { renderLayer ->
				// set up shader
				renderLayer.startDrawing()
				val shader = RenderSystem.getShader()!!
				
				// render each chunk
				entity.chunkSections.forEach {
					val chunk = it.value
					
					if (chunk.hasBuilt) {
						val buffer = chunk.vertexBuffers[renderLayer]!!
						buffer.bind()
						buffer.draw(matrices.peek().model, RenderSystem.getProjectionMatrix(), shader)
						
						VertexBuffer.unbind()
					}
				}
				
				renderLayer.endDrawing()
			}
		} finally {
			isJarRendering = false
		}
	}
	
	companion object {
		// always reuse the same BBBS because it cannot be freed, so it's an insta memleak.
		private val BUFFERS: BlockBufferBuilderStorage = BlockBufferBuilderStorage()
		private val BLOCK_LAYERS: List<RenderLayer> = listOf(RenderLayer.getSolid(), RenderLayer.getCutoutMipped(), RenderLayer.getCutout(), RenderLayer.getTripwire(), RenderLayer.getTranslucent())
		// this is serialized, so we use this to prevent rendering jars inside jars
		private var isJarRendering: Boolean = false
	}
}
