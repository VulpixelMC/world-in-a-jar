package gay.sylv.wij.impl.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import gay.sylv.wij.impl.dimension.Dimensions;
import gay.sylv.wij.impl.duck.RendererWithExternalBlockStates;
import gay.sylv.wij.impl.util.Initializable;
import gay.sylv.wij.impl.util.Instantiation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Renders the inside of a jar dimension (i.e. the outside world displayed inward).
 */
@Environment(EnvType.CLIENT)
public final class JarInternalsRenderer implements Initializable, BlockAndTintGetter, RendererWithExternalBlockStates {
	public static final JarInternalsRenderer INSTANCE = new JarInternalsRenderer();
	// always reuse the same SectionBufferBuilderPack because it cannot be freed, so it's an instant memory leak.
	private static final SectionBufferBuilderPack BYTE_BUFFER_BUILDERS = new SectionBufferBuilderPack();
	private static final Map<RenderType, BufferBuilder> BUFFERS = new HashMap<>();
	/**
	 * A cache of biome colors for calculating biome color.
	 */
	private final Map<ColorResolver, BlockTintCache> tintCache = JarRenderChunkRegion.createTintCache(BiomeColors.GRASS_COLOR_RESOLVER, BiomeColors.FOLIAGE_COLOR_RESOLVER, BiomeColors.WATER_COLOR_RESOLVER);
	private PalettedContainer<BlockState> externalBlockStateContainer = Instantiation.blockStatePalettedContainer();
	private final Map<RenderType, VertexBuffer> vertexBuffers = RenderType.chunkBufferLayers().stream()
			.collect(
					Collectors.toMap(
							key -> key,
							renderType -> new VertexBuffer(VertexBuffer.Usage.STATIC)
					)
			);
	private final Set<RenderType> renderedTypes = new HashSet<>();
	private Vec3 jarCenter = new Vec3(0.0D, 0.0D, 0.0D);
	
	private boolean rebuild = false;
	
	private JarInternalsRenderer() {}
	
	@Override
	public void initialize() {
		WorldRenderEvents.AFTER_ENTITIES.register(JarInternalsRenderer::buildAndRenderExternal);
	}
	
	private static void buildAndRenderExternal(WorldRenderContext context) {
		Minecraft client = Minecraft.getInstance();
		
		if (client.player != null && client.player.level().dimension().equals(Dimensions.JAR)) {
			// Build external chunk
			BlockRenderDispatcher blockRenderer = client.getBlockRenderer();
			if (INSTANCE.rebuild) {
				INSTANCE.rebuild = false;
				buildExternal(client, blockRenderer, INSTANCE.externalBlockStateContainer);
			}
			
			Camera camera = client.gameRenderer.getMainCamera();
			// Render external chunk
			renderExternal(camera, INSTANCE.worldinajar$getCenterOfJar(), new PoseStack());
		}
	}
	
	private static void renderExternal(
			Camera camera,
			Vec3 jarCenter,
			PoseStack poseStack
	) {
		poseStack.pushPose();
		Vec3 distCenterToCamera = jarCenter.subtract(camera.getPosition());
		poseStack.translate(distCenterToCamera.x, distCenterToCamera.y, distCenterToCamera.z);
		poseStack.scale(64.0f, 64.0f, 64.0f);
		poseStack.translate(-8.5f, -8.5f, -8.5f); // translate to the jar's location
		poseStack.translate(0.0f, -0.025f, 0.0f); // prevent terrain from clipping inside the jar
		
		for (RenderType renderType : INSTANCE.renderedTypes) {
			renderType.setupRenderState();
			ShaderInstance shader = RenderSystem.getShader();
			Matrix4f frustumMatrix = poseStack.last().pose();
			Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
			matrix4fStack.pushMatrix();
			matrix4fStack.mul(frustumMatrix);
			RenderSystem.applyModelViewMatrix();
			
			VertexBuffer buffer = INSTANCE.vertexBuffers.get(renderType);
			buffer.bind();
			buffer.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), shader);
			VertexBuffer.unbind();
			
			matrix4fStack.popMatrix();
			
			renderType.clearRenderState();
		}
		
		poseStack.popPose();
	}
	
	private static void buildExternal(
			Minecraft client,
			BlockRenderDispatcher blockRenderer,
			PalettedContainer<BlockState> externalBlockStateContainer
	) {
		RandomSource randomSource = Objects.requireNonNull(client.level, "Cannot build jar internals when Minecraft#level == null.").getRandom();
		BlockPos origin = new BlockPos(0, 0, 0);
		BlockPos offset = new BlockPos(15, 15, 15);
		BlockPos center = new BlockPos(8, 8, 8);
		// The section's PoseStack
		PoseStack poseStack = new PoseStack();
		
		INSTANCE.renderedTypes.clear();
		
		// Build the section (excluding the jar model)
		for (BlockPos blockPos : BlockPos.betweenClosed(origin, offset)) {
			if (blockPos.equals(center)) continue;
			BlockState state = externalBlockStateContainer.get(blockPos.getX(), blockPos.getY(), blockPos.getZ());
			FluidState fluidState = state.getFluidState();
			
			if (!fluidState.isEmpty()) {
				RenderType renderType = ItemBlockRenderTypes.getRenderLayer(fluidState);
				INSTANCE.renderedTypes.add(renderType);
				BufferBuilder bufferBuilder = getOrSetBufferBuilder(renderType);
				
				blockRenderer.renderLiquid(blockPos, INSTANCE, bufferBuilder, state, fluidState);
			}
			
			if (state.getRenderShape() == RenderShape.MODEL) {
				RenderType renderType = ItemBlockRenderTypes.getChunkRenderType(state);
				INSTANCE.renderedTypes.add(renderType);
				BufferBuilder bufferBuilder = getOrSetBufferBuilder(renderType);
				
				poseStack.pushPose();
				poseStack.translate(
						blockPos.getX(),
						blockPos.getY(),
						blockPos.getZ()
				);
				blockRenderer.renderBatched(state, blockPos, INSTANCE, poseStack, bufferBuilder, true, randomSource);
				poseStack.popPose();
			}
		}
		
		// end building and upload vertex buffers
		for (RenderType renderType : INSTANCE.renderedTypes) {
			VertexBuffer buffer = INSTANCE.vertexBuffers.get(renderType);
			BufferBuilder bufferBuilder = BUFFERS.get(renderType);
			MeshData renderedBuffer = bufferBuilder.build();
			buffer.bind();
			buffer.upload(renderedBuffer);
			VertexBuffer.unbind();
		}
		
		// flush buffers
		BUFFERS.clear();
	}
	
	private static BufferBuilder getOrSetBufferBuilder(RenderType renderType) {
		return getOrSetBufferBuilder(renderType, BUFFERS, BYTE_BUFFER_BUILDERS);
	}
	
	public static BufferBuilder getOrSetBufferBuilder(RenderType renderType, Map<RenderType, BufferBuilder> buffers, SectionBufferBuilderPack byteBufferBuilders) {
		if (!buffers.containsKey(renderType)) {
			ByteBufferBuilder byteBufferBuilder = byteBufferBuilders.buffer(renderType);
			BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, renderType.mode(), renderType.format());
			buffers.put(renderType, bufferBuilder);
			return bufferBuilder;
		} else {
			return buffers.get(renderType);
		}
	}
	
	public static void setRebuild(boolean rebuild) {
		INSTANCE.rebuild = rebuild;
	}
	
	@Override
	public float getShade(Direction direction, boolean shade) {
		return Objects.requireNonNull(Minecraft.getInstance().level).getShade(direction, shade);
	}
	
	@Override
	public @NotNull LevelLightEngine getLightEngine() {
		return Objects.requireNonNull(Minecraft.getInstance().level).getLightEngine();
	}
	
	@Override
	public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
		return tintCache.get(colorResolver).getColor(blockPos);
	}
	
	@Override
	public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
		return null;
	}
	
	@Override
	public @NotNull BlockState getBlockState(BlockPos pos) {
		if (pos.getX() > 15 || pos.getX() < 0 || pos.getY() > 15 || pos.getY() < 0 || pos.getZ() > 15 || pos.getZ() < 0) return Blocks.AIR.defaultBlockState();
		return externalBlockStateContainer.get(pos.getX(), pos.getY(), pos.getZ());
	}
	
	@Override
	public @NotNull FluidState getFluidState(BlockPos pos) {
		if (pos.getX() > 15 || pos.getX() < 0 || pos.getY() > 15 || pos.getY() < 0 || pos.getZ() > 15 || pos.getZ() < 0) return Blocks.AIR.defaultBlockState().getFluidState();
		return externalBlockStateContainer.get(pos.getX(), pos.getY(), pos.getZ()).getFluidState();
	}
	
	@Override
	public int getHeight() {
		return 16;
	}
	
	@Override
	public int getMinBuildHeight() {
		return -64;
	}
	
	@Override
	public PalettedContainer<BlockState> worldinajar$getExternalBlockStateContainer() {
		return externalBlockStateContainer;
	}
	
	@Override
	public void worldinajar$setExternalBlockStateContainer(PalettedContainer<BlockState> externalBlockStateContainer) {
		this.externalBlockStateContainer = externalBlockStateContainer;
	}
	
	@Override
	public Vec3 worldinajar$getCenterOfJar() {
		return jarCenter;
	}
	
	@Override
	public void worldinajar$setCenterOfJar(Vec3 centerOfJar) {
		this.jarCenter = centerOfJar;
	}
}
