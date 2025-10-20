package gay.sylv.wij.impl.client.block.entity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import gay.sylv.wij.api.block.JarContainmentBlock;
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import gay.sylv.wij.impl.block.tag.BlockTags;
import gay.sylv.wij.impl.client.render.JarInternalsRenderer;
import gay.sylv.wij.impl.dimension.Dimensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// todo: rewrite this fuckass code
public class WorldJarRenderer implements BlockEntityRenderer<WorldJarBlockEntity> {
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
		if (jar.getLevel() != null && jar.getLevel().dimension().equals(Dimensions.JAR)) return;
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
			Matrix4f modelView = new Matrix4f(matrix4fStack);
			
			jar.getChunkSections().forEach((pos, section) -> {
				if (section.isHasBuilt() && section.getRenderedTypes().contains(renderType)) {
					VertexBuffer buffer = section.getVertexBuffers().get(renderType);
					buffer.bind();
					buffer.drawWithShader(modelView, RenderSystem.getProjectionMatrix(), shader);
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
		RandomSource randomSource = Objects.requireNonNull(jar.getLevel()).getRandom();
		// The sections' PoseStack
		PoseStack poseStack = new PoseStack();
		
		jar.getChunkSections().forEach((pos, section) -> {
			BlockPos origin = section.getOrigin();
			BlockPos offset = new BlockPos(15, 15, 15).offset(origin);
			
			section.getRenderedTypes().clear();
			
			for (BlockPos blockPos : BlockPos.betweenClosed(origin, offset)) {
				BlockState state = jar.getBlockState(blockPos);
				if (state.getBlock() instanceof JarContainmentBlock containmentBlock && !containmentBlock.renderInJar())
					continue; // Don't render jar container blocks.
				FluidState fluidState = state.getFluidState();
				
				if (state.is(BlockTags.NAUGHTY_BLOCKS)) continue;
				
				if (!fluidState.isEmpty()) {
					RenderType renderType = ItemBlockRenderTypes.getRenderLayer(fluidState);
					section.getRenderedTypes().add(renderType);
					BufferBuilder bufferBuilder = getOrSetBufferBuilder(renderType);
					
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
		return JarInternalsRenderer.getOrSetBufferBuilder(renderType, BUFFERS, BYTE_BUFFER_BUILDERS);
	}
}
