/**
 * World In a Jar
 * Copyright (C) 2023  Sylv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package gay.sylv.wij.impl.block.entity.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexBuffer
import com.mojang.blaze3d.vertex.VertexSorting
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity
import gay.sylv.wij.impl.mixin.client.Accessor_ChunkData
import gay.sylv.wij.impl.util.scale
import net.minecraft.block.BlockRenderType
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage
import net.minecraft.client.render.chunk.ChunkBuilder
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
				.pow(2.0f) + 0.085f * entity.scale - 1.083f // what the fuck https://www.wolframalpha.com/input?i=find+function+%282%2C+-1%29%2C+%2816%2C+0%29%2C+%2832%2C+1%29%2C+%2864%2C+2%29 FIXME: figure out why the stuff inside the jar is 1 block high when the scale is 2 and why it starts sinking by 1 block every 16 scale.
			matrices.scale(entity.visualScale - 0.001f) // scale + prevent z-fighting
			matrices.translate(0.001f, magic, 0.001f)
			
			val cameraPos = ctx.renderDispatcher.camera.pos
			
			if (entity.statesChanged) {
				entity.statesChanged = false
				
				entity.chunkSections.forEach {
					// build chunks
					val section = it.value
					val origin = section.origin
					val offset = BlockPos(15, 15, 15).add(origin)
					val randomGenerator = RandomGenerator.createLegacy()
					
					@Suppress("KotlinConstantConditions")
					section.data = ChunkBuilder.ChunkData() as Accessor_ChunkData
					section.data?.blockEntities?.clear()
					section.renderedLayers.clear()
					
					val chunkMatrices = MatrixStack() // the matrices for the chunks
					for (blockPos in BlockPos.iterate(origin, offset)) {
						val state = entity.getBlockState(blockPos)
						val fluidState = state.fluidState
						
						if (state.hasBlockEntity()) {
							val blockEntity = entity.renderRegion.getBlockEntity(blockPos)
							if (blockEntity != null) {
								section.data?.blockEntities?.add(blockEntity)
							}
						}
						
						if (!fluidState.isEmpty) {
							val renderLayer = RenderLayers.getFluidLayer(fluidState)
							val bufferBuilder = BUFFERS.get(renderLayer)
							if (section.renderedLayers.add(renderLayer)) {
								beginBufferBuilding(renderLayer)
							}
							
							ctx.renderManager.renderFluid(blockPos, entity.renderRegion, bufferBuilder, state, fluidState)
						}
						
						if (state.renderType != BlockRenderType.INVISIBLE) {
							// render block states
							val renderLayer = RenderLayers.getBlockLayer(state)
							val bufferBuilder = BUFFERS.get(renderLayer)
							if (section.renderedLayers.add(renderLayer)) {
								beginBufferBuilding(renderLayer)
							}
							
							chunkMatrices.push()
							chunkMatrices.translate(blockPos.x.toFloat(), blockPos.y.toFloat(), blockPos.z.toFloat())
							ctx.renderManager.renderBlock(state, blockPos, entity.renderRegion, chunkMatrices, bufferBuilder, true, randomGenerator)
							chunkMatrices.pop()
						}
					}
					
					if (section.renderedLayers.contains(RenderLayer.getTranslucent())) {
						val bufferBuilder = BUFFERS.get(RenderLayer.getTranslucent())
						if (!bufferBuilder.isCurrentBatchEmpty) {
							bufferBuilder.setQuadSorting(
								VertexSorting.byDistanceSquared(
									cameraPos.x.toFloat() - origin.x,
									cameraPos.y.toFloat() - origin.y,
									cameraPos.z.toFloat() - origin.z,
								)
							)
							section.data?.bufferState = bufferBuilder.popState()
						}
					}
					
					// end building and upload vertex buffers
					for (renderLayer in section.renderedLayers) {
						val buffer = section.vertexBuffers[renderLayer]!!
						val bufferBuilder = BUFFERS.get(renderLayer)
						val renderedBuffer = bufferBuilder.end()
						buffer.bind()
						buffer.upload(renderedBuffer)
						VertexBuffer.unbind()
					}
					
					// SortTask but crab
					if (section.data?.bufferState != null) {
						val renderLayer = RenderLayer.getTranslucent()
						val bufferBuilder = BUFFERS.get(renderLayer)
						bufferBuilder.begin(renderLayer.drawMode, renderLayer.vertexFormat)
						bufferBuilder.restoreState(section.data?.bufferState)
						bufferBuilder.setQuadSorting(
							VertexSorting.byDistanceSquared(
								cameraPos.x.toFloat() - origin.x,
								cameraPos.y.toFloat() - origin.y,
								cameraPos.z.toFloat() - origin.z,
							)
						)
						section.data?.bufferState = bufferBuilder.popState()
						
						val renderedBuffer = bufferBuilder.end()
						val buffer = section.vertexBuffers[RenderLayer.getTranslucent()]!!
						buffer.bind()
						buffer.upload(renderedBuffer)
						VertexBuffer.unbind()
					}
					
					section.hasBuilt = true
				}
			}
			
			// render each render layer
			BLOCK_LAYERS.forEach { renderLayer ->
				// set up shader
				renderLayer.startDrawing()
				val shader = RenderSystem.getShader()!!
				
				// render each chunk
				entity.chunkSections.forEach {
					val section = it.value
					
					if (section.hasBuilt && section.renderedLayers.contains(renderLayer)) {
						val buffer = section.vertexBuffers[renderLayer]!!
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
		
		private fun beginBufferBuilding(renderLayer: RenderLayer) {
			val bufferBuilder = BUFFERS.get(renderLayer)
			bufferBuilder.begin(renderLayer.drawMode, renderLayer.vertexFormat)
		}
	}
}
