package gay.sylv.wij.impl.block.entity.render

import com.mojang.blaze3d.vertex.VertexBuffer
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage
import net.minecraft.util.math.BlockPos

/**
 * A 16x16x16 sub-chunk. This contains a VBO for rendering.
 * @param pos The bottom-south-west corner of the chunk.
 */
class SubChunk {
	val pos: BlockPos
	var vertices: VertexBuffer = VertexBuffer(VertexBuffer.Usage.DYNAMIC)
	var bufferBuilders: BlockBufferBuilderStorage = BlockBufferBuilderStorage()
	
	constructor(pos: BlockPos) {
		this.pos = pos
	}
}
