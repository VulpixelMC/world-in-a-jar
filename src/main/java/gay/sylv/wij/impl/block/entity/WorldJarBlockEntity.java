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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.serialization.MapCodec;
import gay.sylv.wij.api.block.WorldJar;
import gay.sylv.wij.impl.block.Blocks;
import gay.sylv.wij.impl.client.render.JarChunk;
import gay.sylv.wij.impl.client.render.JarLevelChunkSection;
import gay.sylv.wij.impl.client.render.JarLevelLightEngine;
import gay.sylv.wij.impl.client.render.JarRenderChunkRegion;
import gay.sylv.wij.impl.component.Components;
import gay.sylv.wij.impl.dimension.Dimensions;
import gay.sylv.wij.impl.network.JarChunkUpdatePayload;
import gay.sylv.wij.impl.network.JarLoadedAckPayload;
import gay.sylv.wij.impl.network.Networking;
import gay.sylv.wij.impl.network.client.JarEnterPayload;
import gay.sylv.wij.impl.duck.PlayerWithReturn;
import gay.sylv.wij.impl.network.client.JarLoadedPayload;
import gay.sylv.wij.impl.util.Constants;
import gay.sylv.wij.impl.util.WeakReferenceList;
import gay.sylv.wij.impl.util.jar.JarEntry;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import java.util.*;

public class WorldJarBlockEntity extends BlockEntity implements LightChunkGetter, WorldJar {
	private int scale = DEFAULT_SCALE;
	private BlockPos internalSpawnPos = DEFAULT_SPAWN_POS;
	
	private static final int DEFAULT_SCALE = 64;
	private static final BlockPos DEFAULT_SPAWN_POS = new BlockPos(0, -64, 0);
	
	public static final WeakReferenceList<WorldJarBlockEntity> INSTANCES = new WeakReferenceList<>();
	
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
	 * The location of the target jar.
	 */
	@Nullable
	private Networking.JarLocation targetJarLocation;
	
	/**
	 * The {@link JarEntry} linking to the jar dimension.
	 */
	private JarEntry jarEntry = new JarEntry(-1);
	
	/**
	 * If the {@link BlockState}s in the jar have changed.
	 * <p>
	 * This is used in rendering to determine whether we need to rebuild the VBOs.
	 */
	private boolean statesChanged = false;
	
	/**
	 * True if loaded rather than placed.
	 */
	private boolean loadedNotPlaced = false;
	
	/**
	 * True if the client has received the jar's contents.
	 */
	private boolean clientJarChunksUpdated = false;
	
	public WorldJarBlockEntity(BlockPos pos, BlockState blockState) {
		super(Blocks.WORLD_JAR.type(), pos, blockState);
		INSTANCES.addAuto(this);
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
		if (section == null) {
			return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
		}
		return section.getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
	}
	
	/**
	 * Gets a {@link FluidState} from the specified position.
	 * @return a {@link FluidState} at the {@link BlockState} at the specified position.
	 */
	public FluidState getFluidState(BlockPos pos) {
		return getBlockState(pos).getFluidState();
	}
	
	public void updateBlockStates(MinecraftServer server) {
		Level level = server.getLevel(Dimensions.JAR);
		int max = scale;
		for (int x = 0; x < max; x++) {
			for (int y = 0; y < max; y++) {
				for (int z = 0; z < max; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					assert level != null;
					BlockState state = level.getBlockState(pos.offset(getInternalPos()));
					setBlockState(pos, state);
				}
			}
		}
	}
	
	public void updateSectionStates(MinecraftServer server, SectionPos sectionPos) {
		Level level = server.getLevel(Dimensions.JAR);
		int min = sectionPos.minBlockX();
		int max = sectionPos.maxBlockX();
		for (int x = min; x < max; x++) {
			for (int y = min; y < max; y++) {
				for (int z = min; z < max; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					assert level != null;
					BlockState state = level.getBlockState(pos.offset(getInternalPos()));
					setBlockState(pos, state);
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
	
	public void sendJarChunks(ServerPlayer player) {
		Networking.JarLocation jarLocation = getJarLocation();
		getChunkSections().forEach((pos, section) -> {
			SectionPos sectionPos = SectionPos.of(pos);
			PalettedContainer<BlockState> blockStates = section.getBlockStates();
			JarChunkUpdatePayload payload = new JarChunkUpdatePayload(jarLocation, sectionPos, blockStates);
			player.server.execute(() -> ServerPlayNetworking.send(player, payload));
		});
	}
	
	public void sendJarChunk(ServerPlayer player, SectionPos sectionPos) {
		Networking.JarLocation jarLocation = getJarLocation();
		JarLevelChunkSection section = getChunkSections().get(sectionPos.asLong());
		PalettedContainer<BlockState> blockStates = section.getBlockStates();
		JarChunkUpdatePayload payload = new JarChunkUpdatePayload(jarLocation, sectionPos, blockStates);
		player.server.execute(() -> ServerPlayNetworking.send(player, payload));
	}
	
	private Networking.JarLocation getJarLocation() {
		assert this.level != null;
		return new Networking.JarLocation(this.getBlockPos(), this.level.dimension());
	}
	
	public LightChunk getChunk(int chunkX, int chunkZ) {
		long chunkPos = ChunkPos.asLong(chunkX, chunkZ);
		return chunks.get(chunkPos);
	}
	
	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		loadedNotPlaced = true;
		CompoundTag modTag = tag.getCompound(Constants.COMPAT_MOD_ID);
		scale = modTag.getInt("scale");
		jarEntry = new JarEntry(modTag.getInt("id"));
		internalSpawnPos = jarEntry.chunkPos().getWorldPosition().above();
		targetJarLocation = Networking.JarLocation.CODEC.parse(NbtOps.INSTANCE, modTag.get("target_jar_location")).result().orElse(null);
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		CompoundTag modTag = new CompoundTag();
		if (scale != 0) modTag.putInt("scale", scale);
		modTag.putInt("id", jarEntry.id());
		if (targetJarLocation != null) Networking.JarLocation.CODEC.encodeStart(NbtOps.INSTANCE, targetJarLocation).result().ifPresent(target -> modTag.put("target_jar_location", target));
		tag.put(Constants.COMPAT_MOD_ID, modTag);
	}
	
	@Override
	public void setRemoved() {
		super.setRemoved();
		assert level != null;
	}
	
	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		if (level.dimension() == Dimensions.JAR) return;
		if (level.isClientSide) {
			lightEngine = new JarLevelLightEngine(this, true, true);
			renderChunkRegion = new JarRenderChunkRegion(this, lightEngine);
			ClientPlayNetworking.send(new JarLoadedPayload(getJarLocation()));
		} else {
			initializeServerChunks();
			// Send to tracking players when server loads placed jar.
			if (!loadedNotPlaced) {
				for (ServerPlayer player : PlayerLookup.tracking(this)) {
					ServerPlayNetworking.send(player, new JarLoadedAckPayload(getJarLocation()));
				}
			}
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
			if (chunkSection == null) {
				chunkSection = new JarLevelChunkSection(sectionPos, true, blockStateContainer);
			} else {
				chunkSection.setBlockStates(blockStateContainer);
			}
			
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
	
	public BlockPos getInternalPos() {
		return internalSpawnPos.below();
	}
	
	@Override
	public boolean hasBlockPos(BlockPos pos) {
		return pos.closerToCenterThan(internalSpawnPos.getCenter(), scale);
	}
	
	@Environment(EnvType.CLIENT)
	public static class WorldJarRenderer implements BlockEntityRenderer<WorldJarBlockEntity> {
		private final BlockEntityRendererProvider.Context context;
		// always reuse the same SectionBufferBuilderPack because it cannot be freed, so it's an instant memory leak.
		private static final SectionBufferBuilderPack BYTE_BUFFER_BUILDERS = new SectionBufferBuilderPack();
		private static final Map<RenderType, BufferBuilder> BUFFERS = new HashMap<>();
		
		public WorldJarRenderer(BlockEntityRendererProvider.Context context) {
			this.context = context;
		}
		
		@Override
		public void render(
				WorldJarBlockEntity jar,
				float partialTick,
				PoseStack poseStack,
				MultiBufferSource bufferSource,
				int packedLight,
				int packedOverlay
		) {
			if (jar.level != null && jar.level.dimension().equals(Dimensions.JAR)) return;
			poseStack.pushPose();
			// prevent z-fighting
			poseStack.scale(
					jar.getVisualScale() - 0.001f,
					jar.getVisualScale() - 0.001f,
					jar.getVisualScale() - 0.001f
			);
			poseStack.translate(
					0.001f,
					0.001f,
					0.001f
			);
			
			if (jar.statesChanged) {
				jar.statesChanged = false;
				buildJar(context, jar);
			}
			
			renderJar(jar, poseStack);
			poseStack.popPose();
		}
		
		public static void renderJar(
				WorldJarBlockEntity jar,
				PoseStack poseStack
		) {
			for (RenderType renderType : RenderType.chunkBufferLayers()) {
				renderType.setupRenderState();
				ShaderInstance shader = RenderSystem.getShader();
				Matrix4f frustumMatrix = poseStack.last().pose();
				Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
				matrix4fStack.pushMatrix();
				matrix4fStack.mul(frustumMatrix);
				RenderSystem.applyModelViewMatrix();
				
				jar.getChunkSections().forEach((pos, section) -> {
					if (section.isHasBuilt() && section.getRenderedTypes().contains(renderType)) {
						VertexBuffer buffer = section.getVertexBuffers().get(renderType);
						buffer.bind();
						buffer.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), shader);
						VertexBuffer.unbind();
					}
				});
				
				matrix4fStack.popMatrix();
				
				renderType.clearRenderState();
			}
		}
		
		public static void buildJar(
				BlockEntityRendererProvider.Context context,
				WorldJarBlockEntity jar
		) {
			Vec3 cameraPos = context.getBlockEntityRenderDispatcher().camera.getPosition();
			RandomSource randomSource = Objects.requireNonNull(jar.getLevel()).getRandom();
			// The sections' PoseStack
			PoseStack poseStack = new PoseStack();
			
			jar.getChunkSections().forEach((pos, section) -> {
				BlockPos origin = section.getOrigin();
				BlockPos offset = new BlockPos(15, 15, 15).offset(origin);
				
				section.getRenderedTypes().clear();
				
				for (BlockPos blockPos : BlockPos.betweenClosed(origin, offset)) {
					BlockState state = jar.getBlockState(blockPos);
					FluidState fluidState = state.getFluidState();
					
					if (!fluidState.isEmpty()) {
						RenderType renderType = ItemBlockRenderTypes.getRenderLayer(fluidState);
						section.getRenderedTypes().add(renderType);
						BufferBuilder bufferBuilder = getOrSetBufferBuilder(renderType);;
						
						context.getBlockRenderDispatcher().renderLiquid(blockPos, jar.renderChunkRegion, bufferBuilder, state, fluidState);
					}
					
					if (state.getRenderShape() == RenderShape.MODEL) {
						RenderType renderType = ItemBlockRenderTypes.getChunkRenderType(state);
						section.getRenderedTypes().add(renderType);
						BufferBuilder bufferBuilder = getOrSetBufferBuilder(renderType);
						
						poseStack.pushPose();
						poseStack.translate(
								blockPos.getX(),
								blockPos.getY(),
								blockPos.getZ()
						);
						context.getBlockRenderDispatcher().renderBatched(state, blockPos, jar.renderChunkRegion, poseStack, bufferBuilder, true, randomSource);
						poseStack.popPose();
					}
				}
				
				// end building and upload vertex buffers
				for (RenderType renderType : section.getRenderedTypes()) {
					VertexBuffer buffer = section.getVertexBuffers().get(renderType);
					BufferBuilder bufferBuilder = BUFFERS.get(renderType);
					MeshData renderedBuffer = bufferBuilder.build();
					buffer.bind();
					buffer.upload(renderedBuffer);
					VertexBuffer.unbind();
				}
				
				// flush buffers
				BUFFERS.clear();
				section.setHasBuilt(true);
			});
		}
		
		private static BufferBuilder getOrSetBufferBuilder(RenderType renderType) {
			if (!BUFFERS.containsKey(renderType)) {
				ByteBufferBuilder byteBufferBuilder = BYTE_BUFFER_BUILDERS.buffer(renderType);
				BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, renderType.mode(), renderType.format());
				BUFFERS.put(renderType, bufferBuilder);
				return bufferBuilder;
			} else {
				return BUFFERS.get(renderType);
			}
		}
	}
	
	public static class WorldJarBlock extends BaseEntityBlock {
		private static final MapCodec<WorldJarBlock> CODEC = simpleCodec(WorldJarBlock::new);
		
		public WorldJarBlock(Properties properties) {
			super(properties);
			PlayerBlockBreakEvents.BEFORE.register((Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) -> {
				// Prevent breaking/destruction in jar dimension
				return !(level.dimension().equals(Dimensions.JAR) && state.is(Blocks.WORLD_JAR.block()));
			});
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
		protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
			super.onPlace(state, level, pos, oldState, movedByPiston);
		}
		
		@Override
		protected @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
			List<ItemStack> drops = super.getDrops(state, params);
			BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
			if (blockEntity instanceof WorldJarBlockEntity jar) {
				drops.getFirst().set(Components.JAR_ENTRY_TYPE, jar.jarEntry);
			}
			return drops;
		}
		
		@Override
		protected @NotNull RenderShape getRenderShape(BlockState state) {
			return RenderShape.MODEL;
		}
		
		@Override
		protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
			// Prevent breaking in jar dimension
			if (!player.level().dimension().equals(Dimensions.JAR)) {
				return super.getDestroyProgress(state, player, level, pos);
			} else {
				return 0.0F;
			}
		}
		
		@Override
		protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
			Optional<WorldJarBlockEntity> optionalJar = level.getBlockEntity(pos, Blocks.WORLD_JAR.type());
			if (optionalJar.isEmpty()) return InteractionResult.FAIL;
			boolean isReturnJar = level.dimension().equals(Dimensions.JAR);
			
			if (level.isClientSide()) {
				if (!isReturnJar) {
					ClientPlayNetworking.send(new JarEnterPayload(new Networking.JarLocation(pos, level.dimension())));
					return InteractionResult.PASS;
				}
				
				return InteractionResult.SUCCESS;
			}
			if (!isReturnJar) return InteractionResult.SUCCESS;
			
			MinecraftServer server = level.getServer();
			assert server != null;
			
			Vec3 returnPos = ((PlayerWithReturn) player).worldinajar$getReturnPos();
			ResourceKey<Level> returnDim = ((PlayerWithReturn) player).worldinajar$getReturnDimension();
			if (returnDim == null || returnPos == null) {
				return InteractionResult.FAIL;
			}
			
			ServerLevel returnLevel = server.getLevel(returnDim);
			DimensionTransition transition = new DimensionTransition(returnLevel, returnPos, Vec3.ZERO, 0.0f, 0.0f, DimensionTransition.DO_NOTHING);
			player.changeDimension(transition);
			return InteractionResult.SUCCESS;
		}
	}
}
