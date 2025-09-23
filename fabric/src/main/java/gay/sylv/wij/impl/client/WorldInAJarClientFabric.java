package gay.sylv.wij.impl.client;

import gay.sylv.wij.impl.client.render.JarInternalsRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;

public class WorldInAJarClientFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		WorldInAJarClient.init();

		CoreShaderRegistrationCallback.EVENT.register(context -> {
			JarInternalsRenderer.INSTANCE.initialize();
		});
	}
}
