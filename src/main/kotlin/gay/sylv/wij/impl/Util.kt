/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package gay.sylv.wij.impl

import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.palette.PalettedContainer

fun MatrixStack.scale(scale: Float) {
	this.scale(scale, scale, scale)
}

fun Long.toBlockPos(): BlockPos {
	return BlockPos.fromLong(this)
}

fun Long2ObjectMap<BlockState>.toPalettedContainer(): PalettedContainer<BlockState> {
	val blockStateContainer = PalettedContainer(Block.STATE_IDS, Blocks.AIR.defaultState, PalettedContainer.PaletteProvider.BLOCK_STATE)
	this.forEach {
			(posLong, state) ->
		val pos = posLong.toBlockPos()
		blockStateContainer.set(pos.x, pos.y, pos.z, state)
	}
	return blockStateContainer
}
