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
package gay.sylv.wij.impl.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class BedrockPickaxeItem extends PickaxeItem {
	public BedrockPickaxeItem(Tier tier, Properties properties) {
		super(tier, properties);
	}
	
	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
		if (level instanceof ServerLevel serverLevel && state.is(Blocks.BEDROCK) && miningEntity instanceof Player player) {
			level.setBlockAndUpdate(pos, gay.sylv.wij.impl.block.Blocks.CRACKED_BEDROCK.block().defaultBlockState());
			LootParams lootParams = new LootParams.Builder(serverLevel)
					.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
					.withParameter(LootContextParams.TOOL, stack)
					.withParameter(LootContextParams.THIS_ENTITY, miningEntity)
					.withParameter(LootContextParams.BLOCK_STATE, state)
					.withLuck(player.getLuck())
					.create(LootContextParamSets.BLOCK);
			LootContext lootContext = new LootContext.Builder(lootParams).create(Optional.empty());
			Block.popResource(level, pos, new ItemStack(Items.BEDROCK_SHARD, UniformGenerator.between(0.0F, 2.0F).getInt(lootContext)));
			level.playSound(null, pos, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.BLOCKS, 3.0F, 1.0F);
		}
		return super.mineBlock(stack, level, state, pos, miningEntity);
	}
}
