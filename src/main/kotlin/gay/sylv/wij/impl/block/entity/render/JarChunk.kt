package gay.sylv.wij.impl.block.entity.render

import com.mojang.blaze3d.vertex.VertexBuffer
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.chunk.palette.PalettedContainer
import org.quiltmc.loader.api.minecraft.ClientOnly
import java.util.stream.Collectors

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
	var buffers: BlockBufferBuilderStorage = BlockBufferBuilderStorage()
	var vertexBuffers: Map<RenderLayer, VertexBuffer> = RenderLayer.getBlockLayers()
		.stream()
		.collect(
			Collectors.toMap(
				{ key: RenderLayer -> key },
				{ VertexBuffer(VertexBuffer.Usage.STATIC) }
			)
		)
//	var blockStates: PalettedContainer<BlockState> = PalettedContainer(Block.STATE_IDS, Blocks.AIR.defaultState, PalettedContainer.PaletteProvider.BLOCK_STATE)
	
	init {
		this.origin = BlockPos(offset.multiply(16))
	}
}
