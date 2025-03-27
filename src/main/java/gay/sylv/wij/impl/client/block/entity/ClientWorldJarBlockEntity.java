package gay.sylv.wij.impl.client.block.entity;

import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import gay.sylv.wij.impl.network.c2s.JarLoadedPayload;
import gay.sylv.wij.impl.client.render.JarChunk;
import gay.sylv.wij.impl.client.render.JarLevelChunkSection;
import gay.sylv.wij.impl.client.render.JarLevelLightEngine;
import gay.sylv.wij.impl.client.render.JarRenderChunkRegion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;

public class ClientWorldJarBlockEntity extends WorldJarBlockEntity {
	@Environment(EnvType.CLIENT)
	private JarRenderChunkRegion renderChunkRegion;
	
	public ClientWorldJarBlockEntity(BlockPos pos, BlockState blockState) {
		super(pos, blockState);
	}
	
	public JarRenderChunkRegion getRenderChunkRegion() {
		return renderChunkRegion;
	}
	
	/**
	 * This method is called upon updating a chunk on the clientside. It first remaps {@link BlockState}s to the given {@link PalettedContainer}&lt;{@link BlockState}&gt;, then recreates the {@link JarLevelChunkSection}s, and finally marks {@code statesChanged} as {@code true}.
	 * @author sylv
	 */
	@Environment(EnvType.CLIENT)
	public void onChunkUpdate(Minecraft client, SectionPos sectionPos, PalettedContainer<BlockState> blockStateContainer) {
		client.execute(() -> {
			// put chunk
			JarLevelChunkSection chunkSection = this.getChunkSections().get(sectionPos.asLong());
			ChunkPos chunkPos = new ChunkPos(sectionPos.getX(), sectionPos.getZ());
			JarChunk chunk = this.getChunks().get(chunkPos.toLong());
			
			// remap block states
			if (chunkSection == null) {
				chunkSection = new JarLevelChunkSection(sectionPos, true, blockStateContainer);
			} else {
				chunkSection.setBlockStates(blockStateContainer);
			}
			
			this.getChunkSections().put(sectionPos.asLong(), chunkSection);
			this.getChunks().put(chunkPos.toLong(), chunk);
			
			this.setStatesChanged(true);
		});
	}
	
	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		
		JarLevelLightEngine lightEngine = new JarLevelLightEngine(this, true, true);
		renderChunkRegion = new JarRenderChunkRegion(this, lightEngine);
		ClientPlayNetworking.send(new JarLoadedPayload(this.getJarLocation()));
	}
}
