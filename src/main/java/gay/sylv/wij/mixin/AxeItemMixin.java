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
package gay.sylv.wij.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gay.sylv.wij.api.block.BarkType;
import gay.sylv.wij.impl.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;
import java.util.Optional;

@Mixin(AxeItem.class)
public final class AxeItemMixin {
	@WrapOperation(
			method = "useOn",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/AxeItem;evaluateNewBlockState(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"
			)
	)
	private Optional<BlockState> onStrip(AxeItem instance, Level level, BlockPos pos, @Nullable Player player, BlockState state, Operation<Optional<BlockState>> original) {
		Optional<BlockState> newState = original.call(instance, level, pos, player, state);
		
		if (newState.isPresent() && level instanceof ServerLevel serverLevel && player != null) {
			BarkType.getTypes().stream()
					.filter(type -> state.is(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(type.logTextureId().getNamespace(), type.logTextureId().getPath().replaceFirst("block/", "")))))
					.forEach(type -> {
						LootParams lootParams = new LootParams.Builder(serverLevel)
								.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
								.withParameter(LootContextParams.TOOL, player.getMainHandItem())
								.withParameter(LootContextParams.THIS_ENTITY, player)
								.withParameter(LootContextParams.BLOCK_STATE, state)
								.withLuck(player.getLuck())
								.create(LootContextParamSets.BLOCK);
						LootContext lootContext = new LootContext.Builder(lootParams).create(Optional.empty());
						Block.popResource(level, player.blockPosition(), new ItemStack(Objects.requireNonNull(Items.BARK.get(type)), UniformGenerator.between(1.0F, 4.0F).getInt(lootContext)));
					});
		}
		
		return newState;
	}
}
