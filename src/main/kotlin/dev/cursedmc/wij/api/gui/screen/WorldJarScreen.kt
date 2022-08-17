package dev.cursedmc.wij.api.gui.screen

import dev.cursedmc.wij.api.block.entity.WorldJarBlockEntity
import dev.cursedmc.wij.api.network.c2s.WorldJarUpdateC2SPacket
import dev.cursedmc.wij.impl.WIJConstants.MOD_ID
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos

open class WorldJarScreen<BE : WorldJarBlockEntity>(text: Text?, var blockEntity: BE) : Screen(text) {
	protected open lateinit var inputX: TextFieldWidget
	protected open lateinit var inputY: TextFieldWidget
	protected open lateinit var inputZ: TextFieldWidget
	protected open lateinit var inputScale: TextFieldWidget
	protected open lateinit var applyButton: ButtonWidget
	
	constructor(blockEntity: BE) : this(SCREEN_TEXT, blockEntity)
	
	override fun init() {
		this.inputX = TextFieldWidget(this.textRenderer, this.width / 4 - 176, 80, INPUT_WIDTH, INPUT_HEIGHT, X_POS_TEXT)
		this.inputX.setMaxLength(15)
		this.inputX.text = blockEntity.subPos.x.toString()
		this.addSelectableChild(inputX)
		this.inputY = TextFieldWidget(this.textRenderer, this.width / 4 - 96, 80, INPUT_WIDTH, INPUT_HEIGHT, Y_POS_TEXT)
		this.inputY.setMaxLength(15)
		this.inputY.text = blockEntity.subPos.y.toString()
		this.addSelectableChild(inputY)
		this.inputZ = TextFieldWidget(this.textRenderer, this.width / 4 - 16, 80, INPUT_WIDTH, INPUT_HEIGHT, Z_POS_TEXT)
		this.inputZ.setMaxLength(15)
		this.inputZ.text = blockEntity.subPos.z.toString()
		this.addSelectableChild(inputZ)
		this.inputScale = TextFieldWidget(this.textRenderer, this.width / 4 + 64, 120, INPUT_WIDTH, INPUT_HEIGHT, SCALE_TEXT)
		this.inputScale.setMaxLength(4)
		this.inputScale.text = blockEntity.scale.toString()
		this.addSelectableChild(this.inputScale)
		this.applyButton = this.addDrawableChild(ButtonWidget(this.width / 4 - 256, 160, BUTTON_WIDTH, BUTTON_HEIGHT, APPLY_TEXT) {
			this.updateWorldJar()
		})
	}
	
	fun updateWorldJar() {
		this.client?.networkHandler?.sendPacket(
			WorldJarUpdateC2SPacket(
				BlockPos(inputX.text.toInt(), inputY.text.toInt(), inputZ.text.toInt()),
				inputScale.text.toInt(),
			)
		)
	}
	
	override fun tick() {
		this.inputX.tick()
		this.inputY.tick()
		this.inputZ.tick()
		this.inputScale.tick()
	}
	
	override fun resize(client: MinecraftClient?, width: Int, height: Int) {
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
		const val INPUT_WIDTH: Int = 80
		const val INPUT_HEIGHT: Int = 20
		const val BUTTON_WIDTH: Int = 80
		const val BUTTON_HEIGHT: Int = 20
	}
}
