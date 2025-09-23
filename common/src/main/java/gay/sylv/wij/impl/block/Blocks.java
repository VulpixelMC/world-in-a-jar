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

import gay.sylv.wij.impl.WorldInAJar;
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import gay.sylv.wij.impl.block.entity.WorldJarRenderer;
import gay.sylv.wij.impl.block.entity.type.BlockEntityHolder;
import gay.sylv.wij.impl.block.item.CreativePlacedBlockItem;
import gay.sylv.wij.impl.block.item.WorldJarBlockItem;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import static gay.sylv.wij.impl.util.Constants.modId;

public final class Blocks implements Initializable {
	public static final Blocks INSTANCE = new Blocks();
	public static final float INFINITE_EXPLOSION_RESISTANCE = 3_600_000;

	public static BlockEntityHolder<BlockItem, WorldJarBlockEntity> WORLD_JAR;
	public static BlockHolder<BlockItem> SUSSYSTONE;
	public static BlockHolder<BlockItem> REINFORCED_ECHNOPLAST;
	public static BlockHolder<BlockItem> CORK_BLOCK;
	public static BlockHolder<BlockItem> CRACKED_BEDROCK;

	private Blocks() {}

	@Override
	public void initialize() {
		WORLD_JAR = registerBlockEntityItem(
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
				WorldJarBlockEntity::new,
				WorldJarBlockItem::new,
				new Item.Properties()
		);

		SUSSYSTONE = register(
				"sussystone",
				new Block(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.COBBLESTONE))
		);

		REINFORCED_ECHNOPLAST = registerCreativePlaced(
				"reinforced_echnoplast",
				new TransparentJarContainmentBlock(
						BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GLASS)
								.mapColor(MapColor.COLOR_GRAY)
								.strength(-1.0F, INFINITE_EXPLOSION_RESISTANCE)
				)
		);

		CORK_BLOCK = registerCreativePlaced(
				"cork_block",
				new TransparentJarContainmentBlock(
						BlockBehaviour.Properties.of()
								.mapColor(MapColor.WOOD)
								.sound(SoundType.NETHER_WOOD)
								.noOcclusion()
								.strength(REINFORCED_ECHNOPLAST.block().defaultDestroyTime(), REINFORCED_ECHNOPLAST.block().getExplosionResistance())
								.isRedstoneConductor(net.minecraft.world.level.block.Blocks::never)
				)
		);

		CRACKED_BEDROCK = register(
				"cracked_bedrock",
				new Block(
						BlockBehaviour.Properties.of()
								.mapColor(MapColor.STONE)
								.instrument(NoteBlockInstrument.BASEDRUM)
								.strength(-1.0F, 3600000.0F)
								.isValidSpawn(net.minecraft.world.level.block.Blocks::never)
				)
		);

		if (WorldInAJar.getHelper().) {
			BlockRendering.INSTANCE.initialize();
		}
	}

	private static Block registerBlock(@NotNull String id, Block block) {
		return Registry.register(BuiltInRegistries.BLOCK, modId(id), block);
	}

	private static BlockHolder<BlockItem> registerCreativePlaced(@NotNull String id, Block block) {
		return register(id, block, new CreativePlacedBlockItem(block, new Item.Properties()));
	}

	private static BlockHolder<BlockItem> register(@NotNull String id, Block block) {
		return register(id, block, new BlockItem(block, new Item.Properties()));
	}

	private static <I extends Item> BlockHolder<I> register(@NotNull String id, Block block, I item) {
		block = registerBlock(id, block);
		item = Registry.register(BuiltInRegistries.ITEM, modId(id), item);
		return new BlockHolder<>(block, item);
	}

	private static <I extends BlockItem, BE extends BlockEntity> BlockEntityHolder<I, BE> registerBlockEntityItem(@NotNull String id, Block block, BlockEntityType.BlockEntitySupplier<BE> supplier, BlockItemSupplier<I> item, Item.Properties itemProperties) {
		BlockHolder<I> holder = register(id, block, item.create(block, itemProperties));
		BlockEntityType<BE> type = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, modId(id), BlockEntityType.Builder.of(supplier, block).build());
		return Conversions.convert(holder, type);
	}

	private static <BE extends BlockEntity> BlockEntityHolder<BlockItem, BE> registerBlockEntityItem(@NotNull String id, Block block, BlockEntityType.BlockEntitySupplier<BE> supplier) {
		return registerBlockEntityItem(id, block, supplier, BlockItem::new, new Item.Properties());
	}

	public static final class BlockRendering implements Initializable {
		public static final BlockRendering INSTANCE = new BlockRendering();

		private BlockRendering() {}

		@Override
		public void initialize() {
			register(WORLD_JAR, WorldJarRenderer::new);
			addRenderType(WORLD_JAR.block(), RenderType.cutout());
			addRenderType(REINFORCED_ECHNOPLAST.block(), RenderType.translucent());
		}

		private static <I extends Item, BE extends BlockEntity> void register(@NotNull BlockEntityHolder<I, BE> holder, BlockEntityRendererProvider<BE> rendererProvider) {
			BlockEntityRenderers.register(holder.type(), rendererProvider);
		}

		private static void addRenderType(Block block, RenderType renderType) {
			BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
		}
	}

	@FunctionalInterface
	public interface BlockItemSupplier<T extends BlockItem> {
		T create(Block block, Item.Properties properties);
	}
}
