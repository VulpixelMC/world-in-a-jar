/**
 * Copyright (c) 2022 CursedMC. All rights reserved.
 *
 * World In A Jar is common software: you can redistribute it and/or modify it under the terms of the Commons Protection License as published by the Revolutionary Technical Committee.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Commons Protection License for more details.
 */
package dev.cursedmc.wij.gui.screen

import dev.cursedmc.wij.block.entity.WorldJarBlockEntity
import dev.cursedmc.wij.network.c2s.C2SPackets
import dev.cursedmc.wij.network.c2s.WorldJarEnterC2SPacket
import dev.cursedmc.wij.network.c2s.WorldJarLoadedC2SPacket
import dev.cursedmc.wij.network.c2s.WorldJarUpdateC2SPacket
import dev.cursedmc.wij.impl.WIJConstants.MOD_ID
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import org.quiltmc.qsl.networking.api.PacketByteBufs
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking

open class WorldJarScreen<BE : WorldJarBlockEntity>(text: Text?, private var blockEntity: BE) : Screen(text) {
	protected open lateinit var inputX: TextFieldWidget
	protected open lateinit var inputY: TextFieldWidget
	protected open lateinit var inputZ: TextFieldWidget
	protected open lateinit var inputScale: TextFieldWidget
	protected open lateinit var applyButton: ButtonWidget
	protected open lateinit var enterButton: ButtonWidget
	
	constructor(blockEntity: BE) : this(SCREEN_TEXT, blockEntity)
	
	override fun init() {
		this.inputX = TextFieldWidget(this.textRenderer, this.width / 2 - 88, 80, INPUT_WIDTH, INPUT_HEIGHT, X_POS_TEXT)
		this.inputX.setMaxLength(15)
		this.inputX.text = blockEntity.subPos.x.toString()
		this.addSelectableChild(inputX)
		this.inputY = TextFieldWidget(this.textRenderer, this.width / 2 - 48, 80, INPUT_WIDTH, INPUT_HEIGHT, Y_POS_TEXT)
		this.inputY.setMaxLength(15)
		this.inputY.text = blockEntity.subPos.y.toString()
		this.addSelectableChild(inputY)
		this.inputZ = TextFieldWidget(this.textRenderer, this.width / 2 - 8, 80, INPUT_WIDTH, INPUT_HEIGHT, Z_POS_TEXT)
		this.inputZ.setMaxLength(15)
		this.inputZ.text = blockEntity.subPos.z.toString()
		this.addSelectableChild(inputZ)
		this.inputScale = TextFieldWidget(this.textRenderer, this.width / 2 + 32, 120, INPUT_WIDTH, INPUT_HEIGHT, SCALE_TEXT)
		this.inputScale.setMaxLength(4)
		this.inputScale.text = blockEntity.magnitude.toString()
		this.addSelectableChild(this.inputScale)
		this.applyButton = this.addDrawableChild(
			ButtonWidget.builder(APPLY_TEXT) {
				this.updateWorldJar()
			}
				.positionAndSize(this.width / 2 - 128, 160, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build()
		)
		this.enterButton = this.addDrawableChild(
			ButtonWidget.builder(ENTER_TEXT) {
				this.enterWorldJar()
			}
				.positionAndSize(this.width / 2, 160, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build()
		)
	}
	
	private fun updateWorldJar() {
		try {
			if (inputScale.text.toInt() > 16) {
				inputScale.text = "16"
			}

			ClientPlayNetworking.send(
				C2SPackets.WORLD_JAR_UPDATE,
				WorldJarUpdateC2SPacket.buf(
					BlockPos(
						this.inputX.text.toInt(),
						this.inputY.text.toInt(),
						this.inputZ.text.toInt()
					), this.inputScale.text.toInt(), this.blockEntity.pos
				)
			)
			val buf = PacketByteBufs.create()
			val packet = WorldJarLoadedC2SPacket(this.blockEntity.pos)
			packet.write(buf)
			ClientPlayNetworking.send(C2SPackets.WORLD_JAR_LOADED, buf)
		} catch (ex: java.lang.NumberFormatException) {
			return
		}
	}
	
	private fun enterWorldJar() {
		val buf = PacketByteBufs.create()
		val packet = WorldJarEnterC2SPacket(this.blockEntity.pos)
		packet.write(buf)
		ClientPlayNetworking.send(C2SPackets.WORLD_JAR_ENTER, buf)
	}
	
	override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
		this.renderBackground(graphics)
		
		this.inputX.render(graphics, mouseX, mouseY, delta)
		this.inputY.render(graphics, mouseX, mouseY, delta)
		this.inputZ.render(graphics, mouseX, mouseY, delta)
		this.inputScale.render(graphics, mouseX, mouseY, delta)
		
		super.render(graphics, mouseX, mouseY, delta)
	}

	override fun tick() {
		this.inputX.tick()
		this.inputY.tick()
		this.inputZ.tick()
		this.inputScale.tick()
	}
	
	override fun resize(client: MinecraftClient, width: Int, height: Int) {
		val inputX = this.inputX.text
		val inputY = this.inputY.text
		val inputZ = this.inputZ.text
		val inputScale = this.inputScale.text
		
		super.resize(client, width, height)
		
		this.inputX.text = inputX
		this.inputY.text = inputY
		this.inputZ.text = inputZ
		this.inputScale.text = inputScale
	}
	
	override fun isPauseScreen(): Boolean {
		return false
	}
	
	companion object {
		val SCREEN_TEXT: Text = Text.translatable("$MOD_ID.world_jar.screen")
		val X_POS_TEXT: Text = Text.translatable("$MOD_ID.world_jar.x_pos")
		val Y_POS_TEXT: Text = Text.translatable("$MOD_ID.world_jar.y_pos")
		val Z_POS_TEXT: Text = Text.translatable("$MOD_ID.world_jar.z_pos")
		val SCALE_TEXT: Text = Text.translatable("$MOD_ID.world_jar.scale")
		val APPLY_TEXT: Text = Text.translatable("$MOD_ID.world_jar.apply")
		val ENTER_TEXT: Text = Text.translatable("$MOD_ID.world_jar.enter")
		const val INPUT_WIDTH: Int = 40
		const val INPUT_HEIGHT: Int = 20
		const val BUTTON_WIDTH: Int = 80
		const val BUTTON_HEIGHT: Int = 20
	}
}
