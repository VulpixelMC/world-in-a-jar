package dev.cursedmc.wij.api

import net.minecraft.world.World

interface WorldContainer {
	fun getWorld(): World
}
