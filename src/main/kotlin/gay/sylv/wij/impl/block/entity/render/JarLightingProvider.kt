package gay.sylv.wij.impl.block.entity.render

import net.minecraft.world.chunk.ChunkProvider
import net.minecraft.world.chunk.light.LightingProvider

class JarLightingProvider(chunkProvider: ChunkProvider, hasBlockLight: Boolean, hasSkyLight: Boolean) :
	LightingProvider(chunkProvider, hasBlockLight, hasSkyLight) {
}
