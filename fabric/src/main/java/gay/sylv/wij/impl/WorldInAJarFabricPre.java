package gay.sylv.wij.impl;

import gay.sylv.wij.impl.platform.WorldInAJarPlatformHelperFabric;
import net.fabricmc.api.ModInitializer;

public class WorldInAJarFabricPre implements ModInitializer {
	@Override
	public void onInitialize() {
		WorldInAJar.setHelper(new WorldInAJarPlatformHelperFabric());
	}
}
