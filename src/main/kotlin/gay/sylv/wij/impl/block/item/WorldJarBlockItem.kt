/**
 * World In a Jar
 * Copyright (C) 2023  Sylv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package gay.sylv.wij.impl.block.item

import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity.Companion.DEFAULT_SCALE
import net.minecraft.block.Block
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class WorldJarBlockItem(block: Block?, settings: Settings?) : BlockItem(block, settings) {
	override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
		if (!world.isClient) {
			val item = user.getStackInHand(hand)
			val nbt = item.getOrCreateSubNbt("BlockEntityTag")
			
			if (nbt.getInt("magnitude") == 0) {
				nbt.putInt("magnitude", DEFAULT_SCALE)
			}
		}
		
		return super.use(world, user, hand)
	}
	
	override fun useOnBlock(context: ItemUsageContext): ActionResult {
		this.use(context.world!!, context.player!!, context.hand!!)
		
		return super.useOnBlock(context)
	}
}
