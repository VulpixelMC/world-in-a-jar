package dev.cursedmc.wij.api.fake

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.BufferBuilderStorage
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.entity.EntityRenderDispatcher

class FakeWorldRenderer(
	minecraftClient: MinecraftClient?,
	entityRenderDispatcher: EntityRenderDispatcher?,
	blockEntityRenderDispatcher: BlockEntityRenderDispatcher?
) : WorldRenderer(minecraftClient, entityRenderDispatcher, blockEntityRenderDispatcher, BufferBuilderStorage()) {
}
