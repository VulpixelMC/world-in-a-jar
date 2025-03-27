package gay.sylv.wij.impl.client;

import gay.sylv.wij.impl.FabricWIJMain;
import gay.sylv.wij.impl.WIJMain;
import gay.sylv.wij.impl.client.platform.side.FabricClientSidedProvider;
import gay.sylv.wij.impl.platform.FabricPlatformProvider;
import gay.sylv.wij.impl.server.platform.side.FabricServerSidedProvider;
import gay.sylv.wij.impl.util.Initializable;
import net.fabricmc.api.ClientModInitializer;

public class FabricWIJClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		WIJMain.platformProvider = new FabricPlatformProvider(new FabricClientSidedProvider(), new FabricServerSidedProvider());
		Initializable.initialize(FabricWIJMain.class);
		Initializable.initialize(WIJClient.class);
	}
}
