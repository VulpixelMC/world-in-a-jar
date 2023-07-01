package gay.sylv.wij.impl.block.entity.render

import com.mojang.blaze3d.vertex.VertexBuffer
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import org.quiltmc.loader.api.minecraft.ClientOnly

/**
 * A 16x16x16 sub-chunk. This uses VBOs for rendering.
 * @param offset The chunk offset in 3-dimensions.
 */
@ClientOnly
class JarChunk(offset: ChunkSectionPos) {
	/**
	 * The bottom-south-west corner of the chunk. **Note**: This is automatically aligned to a 16x16x16 grid.
	 */
	val origin: BlockPos
	var vertices: VertexBuffer = VertexBuffer(VertexBuffer.Usage.DYNAMIC)
	var bufferBuilders: BlockBufferBuilderStorage = BlockBufferBuilderStorage()
	
	init {
		this.origin = offset.multiply(16) as BlockPos
	}
}
