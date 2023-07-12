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
import com.mojang.blaze3d.vertex.BufferBuilder
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
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.random.RandomGenerator
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
		matrices.translate(0.001f, -0.001f, 0.001f)
		
		val cameraPos = ctx.renderDispatcher.camera.pos
		
		if (entity.statesChanged) {
			entity.statesChanged = false
			
			var sortState: BufferBuilder.SortState? = null
			
			entity.chunkSections.forEach {
				// build chunks
				val chunk = it.value
				val beginPos = chunk.origin
				val offset = BlockPos(15, 15, 15)
				val randomGenerator = RandomGenerator.createLegacy()
				
				// begin building each buffer
				RenderLayer.getBlockLayers().forEach { renderLayer ->
					val bufferBuilder = chunk.buffers.get(renderLayer)
					bufferBuilder.begin(renderLayer.drawMode, renderLayer.vertexFormat)
				}
				
				val chunkMatrices = MatrixStack() // the matrices for the chunks
				var hasTranslucent = false
				for (blockPos in BlockPos.iterate(beginPos, offset)) {
					val state = entity.getBlockState(blockPos)
					val fluidState = state.fluidState
					
					if (!fluidState.isEmpty) {
						val renderLayer = RenderLayers.getFluidLayer(fluidState)
						val bufferBuilder = chunk.buffers.get(renderLayer)
						hasTranslucent = true
						ctx.renderManager.renderFluid(blockPos, entity.renderRegion, bufferBuilder, state, fluidState)
					}
					
					if (state.renderType != BlockRenderType.INVISIBLE) {
						// render block states
						val renderLayer = RenderLayers.getBlockLayer(state)
						val bufferBuilder = chunk.buffers.get(renderLayer)
						
						chunkMatrices.push()
						chunkMatrices.translate(blockPos.x.toFloat(), blockPos.y.toFloat(), blockPos.z.toFloat())
						ctx.renderManager.renderBlock(state, blockPos, entity.renderRegion, chunkMatrices, bufferBuilder, true, randomGenerator)
						chunkMatrices.pop()
					}
				}
				
				if (hasTranslucent) {
					val bufferBuilder = chunk.buffers.get(RenderLayer.getTranslucent())
					if (!bufferBuilder.isCurrentBatchEmpty) {
						bufferBuilder.setQuadSorting(VertexSorting.byDistanceSquared(
							cameraPos.x.toFloat() - beginPos.x,
							cameraPos.y.toFloat() - beginPos.y,
							cameraPos.z.toFloat() - beginPos.z,
						))
						sortState = bufferBuilder.popState()
					}
				}
				
				// end building and upload vertex buffers
				chunk.vertexBuffers.forEach { (renderLayer, buffer) ->
					val bufferBuilder = chunk.buffers.get(renderLayer)
					val renderedBuffer = bufferBuilder.end()
					buffer.bind()
					buffer.upload(renderedBuffer)
					VertexBuffer.unbind()
				}
				
				// SortTask but crab
				if (sortState != null && hasTranslucent) {
					val renderLayer = RenderLayer.getTranslucent()
					val bufferBuilder = chunk.buffers.get(renderLayer)
					bufferBuilder.begin(renderLayer.drawMode, renderLayer.vertexFormat)
					bufferBuilder.restoreState(sortState)
					bufferBuilder.setQuadSorting(
						VertexSorting.byDistanceSquared(
							cameraPos.x.toFloat() - beginPos.x,
							cameraPos.y.toFloat() - beginPos.y,
							cameraPos.z.toFloat() - beginPos.z,
						)
					)
					sortState = bufferBuilder.popState()
					
					val renderedBuffer = bufferBuilder.end()
					val buffer = chunk.vertexBuffers[RenderLayer.getTranslucent()]!!
					buffer.bind()
					buffer.upload(renderedBuffer)
					VertexBuffer.unbind()
				}
			}
		}
		
		// render each chunk
		entity.chunkSections.forEach {
			val chunk = it.value
			
			// render each render layer
			BLOCK_LAYERS.forEach { renderLayer ->
				// set up shader
				renderLayer.startDrawing()
				val shader = RenderSystem.getShader()!!
				
				val buffer = chunk.vertexBuffers[renderLayer]!!
				buffer.bind()
				buffer.draw(matrices.peek().model, RenderSystem.getProjectionMatrix(), shader)
				
				VertexBuffer.unbind()
				renderLayer.endDrawing()
			}
		}
	}
	
	companion object {
		private val BLOCK_LAYERS: List<RenderLayer> = listOf(RenderLayer.getSolid(), RenderLayer.getCutoutMipped(), RenderLayer.getCutout(), RenderLayer.getTripwire(), RenderLayer.getTranslucent())
	}
}
