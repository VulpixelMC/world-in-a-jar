package gay.sylv.wij.impl.server.block.entity;

import gay.sylv.wij.impl.WIJMain;
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import gay.sylv.wij.impl.client.render.JarChunk;
import gay.sylv.wij.impl.client.render.JarLevelChunkSection;
import gay.sylv.wij.impl.network.s2c.JarLoadedAckPayload;
import gay.sylv.wij.impl.platform.side.LogicalSide;
import gay.sylv.wij.impl.server.platform.side.ServerSidedProvider;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ServerWorldJarBlockEntity extends WorldJarBlockEntity {
	public ServerWorldJarBlockEntity(BlockPos pos, BlockState blockState) {
		super(pos, blockState);
	}
	
	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		if (isInJarDimension()) return;
		if (!level.isClientSide) {
			initializeServerChunks();
			// Send to tracking players when server loads placed jar.
			if (!this.loadedNotPlaced) {
				for (ServerPlayer player : PlayerLookup.tracking(this)) {
					WIJMain.platformProvider
							.<ServerSidedProvider>getSidedProvider(LogicalSide.SERVER)
							.sendCustomPacket(player, new JarLoadedAckPayload(getJarLocation()));
				}
			}
		}
	}
	
	/**
	 * Initializes the chunks server-side.
	 * @author sylv
	 */
	private void initializeServerChunks() {
		// initialize chunks
		this.getChunkSections().clear();
		this.getChunks().clear();
		int max = getChunkDiameter() - 1;
		for (int x = 0; x < max; x++) {
			for (int y = 0; y < max; y++) {
				for (int z = 0; z < max; z++) {
					var sectionPos = SectionPos.of(x, y, z);
					var chunkSection = new JarLevelChunkSection(sectionPos, false);
					var chunkPos = new ChunkPos(sectionPos.getX(), sectionPos.getZ());
					var chunk = new JarChunk(chunkPos, this);
					
					// put chunk
					this.getChunkSections().put(sectionPos.asLong(), chunkSection);
					this.getChunks().put(chunkPos.toLong(), chunk);
				}
			}
		}
	}
}
