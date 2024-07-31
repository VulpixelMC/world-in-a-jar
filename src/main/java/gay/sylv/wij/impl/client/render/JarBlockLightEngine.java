package gay.sylv.wij.impl.client.render;

import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightEngine;

public class JarBlockLightEngine extends BlockLightEngine {
	public JarBlockLightEngine(LightChunkGetter chunkSource) {
		super(chunkSource);
	}
}
