package dev.cursedmc.wij.impl

import dev.cursedmc.wij.mixin.IdCountsStateAccessor
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.world.IdCountsState
import net.minecraft.world.World

fun MatrixStack.scale(scale: Float) {
	this.scale(scale, scale, scale)
}

fun nextJarId(world: World): Int {
	val idCounts = world.server?.overworld?.persistentStateManager?.getOrCreate(IdCountsState::fromNbt, ::IdCountsState, "idcounts")!!
	idCounts as IdCountsStateAccessor
	val id = idCounts.idCounts.getInt("jar") + 1
	idCounts.idCounts["jar"] = id
	idCounts.markDirty()
	return id
}
