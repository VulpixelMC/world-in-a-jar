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
package gay.sylv.wij.impl.util;

import gay.sylv.wij.impl.block.BlockHolder;
import gay.sylv.wij.impl.block.entity.type.BlockEntityHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class Conversions {
	private Conversions() {}
	
	public static <I extends Item, BE extends BlockEntity> BlockEntityHolder<I, BE> convert(BlockHolder<I> holder, BlockEntityType<BE> type) {
		return new BlockEntityHolder<>(holder.block(), holder.item(), type);
	}
}
