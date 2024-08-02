/**
 * World In a Jar
 * Copyright (C) 2024  VulpixelMC
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package gay.sylv.wij.impl.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.MapCodec;
import gay.sylv.wij.impl.block.Blocks;
import gay.sylv.wij.impl.client.render.JarChunk;
import gay.sylv.wij.impl.client.render.JarLevelChunkSection;
import gay.sylv.wij.impl.client.render.JarLevelLightEngine;
import gay.sylv.wij.impl.client.render.JarRenderChunkRegion;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WorldJarBlockEntity extends BlockEntity implements LightChunkGetter {
	public static final List<WorldJarBlockEntity> INSTANCES = new ArrayList<>();
	public static final Long2ObjectMap<List<WorldJarBlockEntity>> INSTANCE_MAP = new Long2ObjectOpenHashMap<>();
	
	private int scale = 64;
	private BlockPos internalSpawnPos = new BlockPos(0, -64, 0);
	
	/**
	 * {@link JarLevelChunkSection}s that are loaded in the {@link WorldJarBlockEntity}.
	 */
	private final Long2ObjectMap<JarLevelChunkSection> chunkSections = new Long2ObjectOpenHashMap<>();
	
	/**
	 * The full versions of chunks that are loaded in the {@link WorldJarBlockEntity}. This is used in lighting.
	 */
	private final Long2ObjectMap<JarChunk> chunks = new Long2ObjectOpenHashMap<>();
	
	@Environment(EnvType.CLIENT)
	private JarLevelLightEngine lightEngine;
	
	@Environment(EnvType.CLIENT)
	private JarRenderChunkRegion renderChunkRegion;
	
	/**
	 * If the {@link BlockState}s in the jar have changed.
	 * <p>
	 * This is used in rendering to determine whether we need to rebuild the VBOs.
	 */
	private boolean statesChanged = false;
	
	public WorldJarBlockEntity(BlockPos pos, BlockState blockState) {
		super(Blocks.WORLD_JAR.type(), pos, blockState);
	}
	
	public float getVisualScale() {
		return 1.0f / scale;
	}
	
	public int getScale() {
		return scale;
	}
	
	public void setScale(int scale) {
		this.scale = scale;
	}
	
	/**
	 * Sets a {@link BlockState} at the specified position.
	 * @author sylv
	 */
	public void setBlockState(BlockPos pos, BlockState state) {
		var sectionPos = SectionPos.of(pos);
		var section = chunkSections.get(sectionPos.asLong());
		section.setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
	}
	
	/**
	 * Gets a {@link BlockState} from the specified position.
	 * @return {@link BlockState}
	 * @author sylv
	 */
	public BlockState getBlockState(BlockPos pos) {
		var sectionPos = SectionPos.of(pos);
		var section = chunkSections.get(sectionPos.asLong());
		return section.getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
	}
	
	/**
	 * Gets a {@link FluidState} from the specified position.
	 * @return a {@link FluidState} at the {@link BlockState} at the specified position.
	 */
	public FluidState getFluidState(BlockPos pos) {
		return getBlockState(pos).getFluidState();
	}
	
	/**
	 * Initializes the chunks server-side.
	 * @author sylv
	 */
	private void initializeServerChunks() {
		// initialize chunks
		chunkSections.clear();
		chunks.clear();
		int max = getChunkDiameter() - 1;
		for (int x = 0; x < max; x++) {
			for (int y = 0; y < max; y++) {
				for (int z = 0; z < max; z++) {
					var sectionPos = SectionPos.of(x, y, z);
					var chunkSection = new JarLevelChunkSection(sectionPos, false);
					var chunkPos = new ChunkPos(sectionPos.getX(), sectionPos.getZ());
					var chunk = new JarChunk(chunkPos, this);
					
					// put chunk
					chunkSections.put(sectionPos.asLong(), chunkSection);
					chunks.put(chunkPos.toLong(), chunk);
				}
			}
		}
	}
	
	public LightChunk getChunk(int chunkX, int chunkZ) {
		long chunkPos = ChunkPos.asLong(chunkX, chunkZ);
		return chunks.get(chunkPos);
	}
	
	/**
	 * Puts the current instance in [INSTANCE_MAP] and [INSTANCES].
	 * @author sylv
	 */
	private void mapInstance() {
		if (INSTANCES.contains(this)) return;
		
		// map
		var chunkPos = ChunkPos.asLong(getBlockPos());
		var list = INSTANCE_MAP.get(chunkPos);
		if (list != null) {
			list.add(this);
		} else {
			list = new ArrayList<>();
			list.add(this);
			INSTANCE_MAP.put(chunkPos, list);
		}
		
		// put
		INSTANCES.add(this);
	}
	
	/**
	 * Removes the current instance from [INSTANCE_MAP] and [INSTANCES].
	 * @author sylv
	 */
	private void unmapInstance() {
		// map
		var chunkPos = ChunkPos.asLong(getBlockPos());
		var list = INSTANCE_MAP.get(chunkPos);
		list.remove(this);
		if (list.isEmpty()) {
			INSTANCE_MAP.remove(chunkPos);
		}
		
		// put
		INSTANCES.remove(this);
	}
	
	@Override
	public void setRemoved() {
		super.setRemoved();
		assert level != null;
		if (!level.isClientSide) {
			unmapInstance();
		}
	}
	
	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		if (!level.isClientSide) {
			if (!INSTANCES.contains(this)) {
				mapInstance();
			}
		} else {
			lightEngine = new JarLevelLightEngine(this, true, true);
			renderChunkRegion = new JarRenderChunkRegion(this, lightEngine);
		}
	}
	
	@SuppressWarnings("NullableProblems")
	@Nullable
	@Override
	public Level getLevel() {
		return super.getLevel();
	}
	
	/**
	 * This method is called upon updating a chunk on the clientside. It first remaps {@link BlockState}s to the given {@link PalettedContainer}&lt;{@link BlockState}&gt;, then recreates the {@link JarLevelChunkSection}s, and finally marks {@code statesChanged} as {@code true}.
	 * @author sylv
	 */
	@Environment(EnvType.CLIENT)
	public void onChunkUpdate(Minecraft client, SectionPos sectionPos, PalettedContainer<BlockState> blockStateContainer) {
		client.execute(() -> {
			// put chunk
			JarLevelChunkSection chunkSection = chunkSections.get(sectionPos.asLong());
			ChunkPos chunkPos = new ChunkPos(sectionPos.getX(), sectionPos.getZ());
			JarChunk chunk = chunks.get(chunkPos.toLong());
			
			// remap block states
			chunkSection.setBlockStates(blockStateContainer);
			
			chunkSections.put(sectionPos.asLong(), chunkSection);
			chunks.put(chunkPos.toLong(), chunk);
			
			statesChanged = true;
		});
	}
	
	/**
	 * Returns how many chunks high/wide the {@link WorldJarBlockEntity} is. This always rounds up to include partial chunks.
	 * @return how many chunks high/wide the {@link WorldJarBlockEntity} is.
	 * @author sylv
	 */
	public int getChunkDiameter() {
		return SectionPos.posToSectionCoord(scale) + 1;
	}
	
	public Long2ObjectMap<JarLevelChunkSection> getChunkSections() {
		return chunkSections;
	}
	
	@Nullable
	@Override
	public LightChunk getChunkForLighting(int chunkX, int chunkZ) {
		return getChunk(chunkX, chunkZ);
	}
	
	public BlockPos getInternalSpawnPos() {
		return internalSpawnPos;
	}
	
	public void setInternalSpawnPos(BlockPos pos) {
		this.internalSpawnPos = pos;
	}
	
	@Environment(EnvType.CLIENT)
	public static class WorldJarRenderer implements BlockEntityRenderer<WorldJarBlockEntity> {
		private final BlockRenderDispatcher blockRenderDispatcher;
		
		public WorldJarRenderer(BlockEntityRendererProvider.Context context) {
			blockRenderDispatcher = context.getBlockRenderDispatcher();
		}
		
		@Override
		public void render(
				WorldJarBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
		) {
			VertexConsumer buffer = bufferSource.getBuffer(RenderType.solid());
			poseStack.pushPose();
			poseStack.scale(0.001f, 0.001f, 0.001f);
			blockRenderDispatcher.renderBatched(net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(), BlockPos.ZERO, blockEntity.getLevel(), poseStack, buffer, false, RandomSource.create());
			poseStack.popPose();
		}
	}
	
	public static class WorldJarBlock extends BaseEntityBlock {
		private static final MapCodec<WorldJarBlock> CODEC = simpleCodec(WorldJarBlock::new);
		
		public WorldJarBlock(Properties properties) {
			super(properties);
		}
		
		@Override
		protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
			return CODEC;
		}
		
		@Nullable
		@Override
		public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
			return new WorldJarBlockEntity(pos, state);
		}
		
		@Override
		protected @NotNull RenderShape getRenderShape(BlockState state) {
			return RenderShape.MODEL;
		}
	}
}
