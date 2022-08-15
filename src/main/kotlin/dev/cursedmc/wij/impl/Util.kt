package dev.cursedmc.wij.impl

import net.minecraft.client.util.math.MatrixStack

fun MatrixStack.scale(scale: Float) {
	this.scale(scale, scale, scale)
}
