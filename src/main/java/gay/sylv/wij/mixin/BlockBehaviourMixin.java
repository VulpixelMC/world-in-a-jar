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
import com.llamalad7.mixinextras.sugar.Local;
import gay.sylv.wij.impl.block.tag.BlockTags;
import gay.sylv.wij.impl.item.tag.ItemTags;
import gay.sylv.wij.impl.util.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
	private BlockBehaviourMixin() {}
	
	@WrapOperation(
			method = "getDestroyProgress",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroySpeed(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"
			)
	)
	private float bypassUnbreakability(BlockState state, BlockGetter level, BlockPos pos, Operation<Float> original, @Local(argsOnly = true) Player player) {
		if (state.is(BlockTags.UNBREAKABLE) && player.getMainHandItem().is(ItemTags.DESTROYS_UNBREAKABLE) && pos.getY() > level.getMinBuildHeight()) {
			return Constants.UNBREAKABLE_DESTROY_SPEED;
		} else {
			return original.call(state, level, pos);
		}
	}
}
