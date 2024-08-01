package gay.sylv.wij.impl.client.render;

import com.mojang.blaze3d.vertex.VertexBuffer;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;

import java.lang.ref.Cleaner;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A 16x16x16 sub-chunk. This uses VBOs for rendering.
 * @author sylv
 */
public class JarLevelChunkSection {
	/**
	 * The bottom-south-west corner of the chunk. **Note**: This is automatically aligned to a 16x16x16 grid.
	 */
	private final BlockPos origin;
	@Environment(EnvType.CLIENT)
	private Cleaner.Cleanable cleanable;
	@Environment(EnvType.CLIENT)
	private Map<RenderType, VertexBuffer> vertexBuffers;
	@Environment(EnvType.CLIENT)
	private ReferenceArraySet<RenderType> renderedTypes = new ReferenceArraySet<>(RenderType.chunkBufferLayers().size());
	private boolean hasBuilt = false;
	private PalettedContainer<BlockState> blockStates;
	
	private static final Cleaner CLEANER = Cleaner.create();
	
	/**
	 * @param offset The position of the chunk in 3-dimensions.
	 */
	public JarLevelChunkSection(SectionPos offset, boolean isClient) {
		origin = new BlockPos(offset.multiply(16));
		if (isClient) {
			vertexBuffers = RenderType.chunkBufferLayers().stream()
					.collect(
							Collectors.toMap(
									key -> key,
									value -> new VertexBuffer(VertexBuffer.Usage.STATIC)
							)
					);
			this.cleanable = CLEANER.register(this, new ClientClean(vertexBuffers));
		}
	}
	
	/**
	 * Determines if any {@link BlockState}s adhere to the predicate.
	 * @author sylv
	 */
	public boolean maybeHas(Predicate<BlockState> predicate) {
		return blockStates.maybeHas(predicate);
	}
	
	/**
	 * @return the {@link BlockState} at the given {@link BlockPos}.
	 * @author sylv
	 */
	public BlockState getBlockState(int x, int y, int z) {
		return blockStates.get(x, y, z);
	}
	
	public void setBlockState(int x, int y, int z, BlockState state) {
		blockStates.set(x, y, z, state);
	}
	
	public boolean isHasBuilt() {
		return hasBuilt;
	}
	
	public void setHasBuilt(boolean hasBuilt) {
		this.hasBuilt = hasBuilt;
	}
	
	@Environment(EnvType.CLIENT)
	public ReferenceArraySet<RenderType> getRenderedTypes() {
		return renderedTypes;
	}
	
	@Environment(EnvType.CLIENT)
	public void setRenderedTypes(ReferenceArraySet<RenderType> renderedTypes) {
		this.renderedTypes = renderedTypes;
	}
	
	@Environment(EnvType.CLIENT)
	public Map<RenderType, VertexBuffer> getVertexBuffers() {
		return vertexBuffers;
	}
	
	public BlockPos getOrigin() {
		return origin;
	}
	
	public void setBlockStates(PalettedContainer<BlockState> blockStates) {
		this.blockStates = blockStates;
	}
	
	/**
	 * Prevents memory leaks by closing {@link VertexBuffer}s.
	 *
	 * @author SoniEx2
	 */
	@Environment(EnvType.CLIENT)
	private record ClientClean(Map<RenderType, VertexBuffer> vertexBuffers) implements Runnable {
		@Override
		public void run() {
			Minecraft.getInstance().execute(() -> {
				vertexBuffers.values().forEach(VertexBuffer::close);
			});
		}
	}
}
