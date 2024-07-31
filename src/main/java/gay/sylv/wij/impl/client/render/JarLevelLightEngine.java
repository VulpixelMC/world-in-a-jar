package gay.sylv.wij.impl.client.render;

import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class JarLevelLightEngine extends LevelLightEngine {
	public JarLevelLightEngine(LightChunkGetter lightChunkGetter, boolean blockLight, boolean skyLight) {
		super(lightChunkGetter, blockLight, skyLight);
	}
}
