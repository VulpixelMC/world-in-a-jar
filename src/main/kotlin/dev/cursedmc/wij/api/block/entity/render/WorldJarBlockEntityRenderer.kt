package dev.cursedmc.wij.api.block.entity.render

import dev.cursedmc.wij.api.block.entity.WorldJarBlockEntity
import dev.cursedmc.wij.impl.scale
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack

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
		matrices.translate(0.0, 1.0, 0.0)
		
		// THE MODFEST IS IN 3 MINUTES HELP WHY WON'T IT UPLOAD PLEASE
		
		for (x in 0..max) {
			for (y in 0 until max) {
				for (z in 0..max) {
					matrices.push()
					
					if (x == 0) matrices.translate(0.001, 0.0, 0.0) else if (x == max) matrices.translate(-0.001, 0.0 ,0.0)
					if (y == max - 1) matrices.translate(0.0, -0.001, 0.0)
					if (z == 0) matrices.translate(0.0, 0.0, 0.001) else if (z == max) matrices.translate(0.0, 0.0, -0.001)
					
					matrices.translate(x.toDouble(), y.toDouble(), z.toDouble())
					
					ctx.renderManager.renderBlockAsEntity(entity.subworld?.getBlockState(entity.subPos.add(x, y, z)), matrices, vertexConsumers, light, overlay)
					
					matrices.pop()
				}
			}
		}
	}
}
