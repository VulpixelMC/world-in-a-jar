/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package gay.sylv.wij.impl.block.item

import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity.Companion.DEFAULT_MAGNITUDE
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
				nbt.putInt("magnitude", DEFAULT_MAGNITUDE)
			}
		}
		
		return super.use(world, user, hand)
	}
	
	override fun useOnBlock(context: ItemUsageContext): ActionResult {
		this.use(context.world!!, context.player!!, context.hand!!)
		
		return super.useOnBlock(context)
	}
}
