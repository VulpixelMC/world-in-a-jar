package gay.sylv.wij.impl.block.entity.render

import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.light.LightingProvider

class JarLightingProvider(val entity: WorldJarBlockEntity, hasBlockLight: Boolean, hasSkyLight: Boolean) :
	LightingProvider(entity, hasBlockLight, hasSkyLight) {
	override fun getLight(pos: BlockPos?, ambientDarkness: Int): Int {
		return 11
	}
}
