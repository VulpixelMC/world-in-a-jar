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
package gay.sylv.wij.impl.datagen;

import gay.sylv.wij.impl.block.Blocks;
import gay.sylv.wij.impl.block.tag.BlockTags;
import gay.sylv.wij.impl.item.Items;
import gay.sylv.wij.impl.item.tag.ItemTags;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public final class DataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(BlockTagGenerator::new);
		pack.addProvider(ItemTagGenerator::new);
		pack.addProvider(BlockLootTableGenerator::new);
		pack.addProvider(RecipeGenerator::new);
	}
	
	private static final class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {
		public BlockTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, registriesFuture);
		}
		
		@Override
		protected void addTags(HolderLookup.Provider wrapperLookup) {
			getOrCreateTagBuilder(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE)
					.add(Blocks.WORLD_JAR.block())
					.add(Blocks.SUSSYSTONE.block())
					.add(net.minecraft.world.level.block.Blocks.BEDROCK);
			
			getOrCreateTagBuilder(net.minecraft.tags.BlockTags.NEEDS_IRON_TOOL)
					.add(Blocks.WORLD_JAR.block())
					.add(Blocks.SUSSYSTONE.block());
			getOrCreateTagBuilder(BlockTags.NEEDS_BEDROCK_TOOL)
					.add(net.minecraft.world.level.block.Blocks.BEDROCK);
			
			getOrCreateTagBuilder(BlockTags.UNBREAKABLE)
					.add(net.minecraft.world.level.block.Blocks.BEDROCK);
			
			getOrCreateTagBuilder(net.minecraft.tags.BlockTags.INCORRECT_FOR_WOODEN_TOOL)
					.addTag(BlockTags.NEEDS_BEDROCK_TOOL);
			getOrCreateTagBuilder(net.minecraft.tags.BlockTags.INCORRECT_FOR_STONE_TOOL)
					.addTag(BlockTags.NEEDS_BEDROCK_TOOL);
			getOrCreateTagBuilder(net.minecraft.tags.BlockTags.INCORRECT_FOR_IRON_TOOL)
					.addTag(BlockTags.NEEDS_BEDROCK_TOOL);
			getOrCreateTagBuilder(net.minecraft.tags.BlockTags.INCORRECT_FOR_GOLD_TOOL)
					.addTag(BlockTags.NEEDS_BEDROCK_TOOL);
			getOrCreateTagBuilder(net.minecraft.tags.BlockTags.INCORRECT_FOR_DIAMOND_TOOL)
					.addTag(BlockTags.NEEDS_BEDROCK_TOOL);
			getOrCreateTagBuilder(net.minecraft.tags.BlockTags.INCORRECT_FOR_NETHERITE_TOOL)
					.addTag(BlockTags.NEEDS_BEDROCK_TOOL);
			getOrCreateTagBuilder(BlockTags.INCORRECT_FOR_BEDROCK_TOOL);
		}
	}
	
	private static final class ItemTagGenerator extends FabricTagProvider.ItemTagProvider {
		public ItemTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
			super(output, completableFuture);
		}
		
		@Override
		protected void addTags(HolderLookup.Provider wrapperLookup) {
			getOrCreateTagBuilder(ItemTags.DESTROYS_UNBREAKABLE)
					.add(Items.BEDROCK_PICKAXE);
			
			getOrCreateTagBuilder(ItemTags.CHIPS_UNBREAKABLE)
					.add(net.minecraft.world.item.Items.NETHERITE_PICKAXE);
			
			getOrCreateTagBuilder(ItemTags.CHIPS_OR_DESTROYS_UNBREAKABLE)
					.addTag(ItemTags.DESTROYS_UNBREAKABLE)
					.addTag(ItemTags.CHIPS_UNBREAKABLE);
			
			getOrCreateTagBuilder(net.minecraft.tags.ItemTags.PICKAXES)
					.add(Items.BEDROCK_PICKAXE);
		}
	}
	
	private static final class BlockLootTableGenerator extends FabricBlockLootTableProvider {
		public BlockLootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
			super(dataOutput, registryLookup);
		}
		
		@Override
		public void generate() {
			dropSelf(Blocks.CORK_BLOCK.block());
			dropSelf(Blocks.WORLD_JAR.block());
		}
	}
	
	private static final class RecipeGenerator extends FabricRecipeProvider {
		
		public RecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, registriesFuture);
		}
		
		@Override
		public void buildRecipes(RecipeOutput exporter) {
			ShapedRecipeBuilder
					.shaped(RecipeCategory.MISC, Blocks.WORLD_JAR.item())
					.unlockedBy("has_ender_eye", has(net.minecraft.world.item.Items.ENDER_EYE))
					.unlockedBy("has_reinforced_echnoplast", has(Blocks.REINFORCED_ECHNOPLAST.item()))
					.unlockedBy("has_cork_block", has(Blocks.CORK_BLOCK.item()))
					.define('o', net.minecraft.world.item.Items.ENDER_EYE)
					.define('O', Blocks.REINFORCED_ECHNOPLAST.item())
					.define('#', Blocks.CORK_BLOCK.item())
					.pattern("O#O")
					.pattern("OoO")
					.pattern("OOO")
					.save(exporter);
			ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.CORK_BLOCK.item())
					.unlockedBy("has_bark", has(ItemTags.BARK))
					.define('/', ItemTags.BARK)
					.pattern("///")
					.pattern("///")
					.pattern("///")
					.save(exporter);
			ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.REINFORCED_ECHNOPLAST.item())
					.unlockedBy("has_bedrock_shard", has(Items.BEDROCK_SHARD))
					.unlockedBy("has_amethyst_shard", has(net.minecraft.world.item.Items.AMETHYST_SHARD))
					.define('%', Items.BEDROCK_SHARD)
					.define('/', net.minecraft.world.item.Items.AMETHYST_SHARD)
					.define('O', net.minecraft.world.item.Items.GLASS)
					.pattern("%/%")
					.pattern("/O/")
					.pattern("%/%")
					.save(exporter);
			SmithingTransformRecipeBuilder.smithing(
					Ingredient.of(net.minecraft.world.item.Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
					Ingredient.of(net.minecraft.world.item.Items.NETHERITE_PICKAXE),
					Ingredient.of(Items.BEDROCK_SHARD),
					RecipeCategory.TOOLS,
					Items.BEDROCK_PICKAXE
			)
					.unlocks("has_bedrock_shard", has(Items.BEDROCK_SHARD))
					.unlocks("has_netherite_pickaxe", has(net.minecraft.world.item.Items.NETHERITE_PICKAXE))
					.save(exporter, BuiltInRegistries.ITEM.getKey(Items.BEDROCK_PICKAXE));
		}
	}
}
