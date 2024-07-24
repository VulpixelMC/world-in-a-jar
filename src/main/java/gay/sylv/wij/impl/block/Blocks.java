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
package gay.sylv.wij.impl.block;

import gay.sylv.wij.impl.Main;
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import gay.sylv.wij.impl.block.entity.type.BlockEntityHolder;
import gay.sylv.wij.impl.util.Conversions;
import gay.sylv.wij.impl.util.Initializable;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import static gay.sylv.wij.impl.util.Constants.modId;

public final class Blocks implements Initializable {
	public static final Blocks INSTANCE = new Blocks();
	
	public static final BlockEntityHolder<BlockItem, WorldJarBlockEntity> WORLD_JAR = registerBlockEntityItem(
			"world_jar",
			new WorldJarBlockEntity.WorldJarBlock(
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.STONE)
							.strength(3.0F)
							.sound(SoundType.METAL)
							.noOcclusion()
							.isRedstoneConductor(net.minecraft.world.level.block.Blocks::never)
							.isViewBlocking(net.minecraft.world.level.block.Blocks::never)
			),
			WorldJarBlockEntity::new
	);
	
	public static final BlockHolder<BlockItem> SUSSYSTONE = registerItem(
			"sussystone",
			new Block(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.COBBLESTONE))
	);
	
	public static final BlockHolder<BlockItem> REINFORCED_ECHNOPLAST = registerItem(
			"reinforced_echnoplast",
			new TransparentBlock(
					BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GLASS)
							.mapColor(MapColor.COLOR_GRAY)
							.strength(42.0F, (float) Math.pow(6.0D, 12.0D))
			)
	);
	
	public static final BlockHolder<BlockItem> CORK_BLOCK = registerItem(
			"cork_block",
			new Block(
					BlockBehaviour.Properties.of()
							.mapColor(MapColor.WOOD)
							.sound(SoundType.NETHER_WOOD)
							.strength(REINFORCED_ECHNOPLAST.block().defaultDestroyTime(), REINFORCED_ECHNOPLAST.block().getExplosionResistance())
							.isRedstoneConductor(net.minecraft.world.level.block.Blocks::never)
			)
	);
	
	private Blocks() {}
	
	private static Block registerBlock(@NotNull String id, Block block) {
		return Registry.register(BuiltInRegistries.BLOCK, modId(id), block);
	}
	
	private static BlockHolder<BlockItem> registerItem(@NotNull String id, Block block) {
		return registerItem(id, block, new BlockItem(block, new Item.Properties()));
	}
	
	private static <I extends Item> BlockHolder<I> registerItem(@NotNull String id, Block block, I item) {
		block = registerBlock(id, block);
		item = Registry.register(BuiltInRegistries.ITEM, modId(id), item);
		return new BlockHolder<>(block, item);
	}
	
	private static <I extends Item, BE extends BlockEntity> BlockEntityHolder<I, BE> registerBlockEntityItem(@NotNull String id, Block block, BlockEntityType.BlockEntitySupplier<BE> supplier, I item) {
		BlockHolder<I> holder = registerItem(id, block, item);
		BlockEntityType<BE> type = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, modId(id), BlockEntityType.Builder.of(supplier, block).build());
		return Conversions.convert(holder, type);
	}
	
	private static <BE extends BlockEntity> BlockEntityHolder<BlockItem, BE> registerBlockEntityItem(@NotNull String id, Block block, BlockEntityType.BlockEntitySupplier<BE> supplier) {
		return registerBlockEntityItem(id, block, supplier, new BlockItem(block, new Item.Properties()));
	}
	
	@Override
	public void initialize() {
		if (Main.isClient()) {
			BlockRendering.INSTANCE.initialize();
		}
	}
	
	public static final class BlockRendering implements Initializable {
		public static final BlockRendering INSTANCE = new BlockRendering();
		
		private BlockRendering() {}
		
		@Override
		public void initialize() {
			register(WORLD_JAR, WorldJarBlockEntity.WorldJarRenderer::new);
			addCutout(WORLD_JAR.block());
			addCutout(REINFORCED_ECHNOPLAST.block());
		}
		
		private static <I extends Item, BE extends BlockEntity> void register(@NotNull BlockEntityHolder<I, BE> holder, BlockEntityRendererProvider<BE> rendererProvider) {
			BlockEntityRenderers.register(holder.type(), rendererProvider);
		}
		
		private static void addCutout(Block block) {
			BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout());
		}
	}
}
