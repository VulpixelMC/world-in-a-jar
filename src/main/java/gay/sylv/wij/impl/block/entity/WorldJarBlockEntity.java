/**
 * World In a Jar
 * Copyright (C) 2024  VulpixelMC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package gay.sylv.wij.impl.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import gay.sylv.wij.impl.block.Blocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldJarBlockEntity extends BlockEntity {
	public WorldJarBlockEntity(BlockPos pos, BlockState blockState) {
		super(Blocks.WORLD_JAR.type(), pos, blockState);
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
