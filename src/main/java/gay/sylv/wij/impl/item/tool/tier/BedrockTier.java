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
package gay.sylv.wij.impl.item.tool.tier;

import gay.sylv.wij.impl.block.tag.BlockTags;
import gay.sylv.wij.impl.item.Items;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public final class BedrockTier implements Tier {
	public static final Tier INSTANCE = new BedrockTier();
	
	@Override
	public int getUses() {
		// 2031 is the netherite tier uses in 1.21
		return 2031 * 3;
	}
	
	@Override
	public float getSpeed() {
		return 10.0F;
	}
	
	@Override
	public float getAttackDamageBonus() {
		return 3.5F;
	}
	
	@Override
	public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
		return BlockTags.INCORRECT_FOR_BEDROCK_TOOL;
	}
	
	@Override
	public int getEnchantmentValue() {
		return 10;
	}
	
	@Override
	public @NotNull Ingredient getRepairIngredient() {
		return Ingredient.of(Items.BEDROCK_SHARD);
	}
}
