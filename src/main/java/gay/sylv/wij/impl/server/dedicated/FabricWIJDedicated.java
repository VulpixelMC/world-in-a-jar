package gay.sylv.wij.impl.server.dedicated;

import gay.sylv.wij.impl.FabricWIJMain;
import gay.sylv.wij.impl.WIJMain;
import gay.sylv.wij.impl.platform.FabricPlatformProvider;
import gay.sylv.wij.impl.server.platform.side.FabricServerSidedProvider;
import gay.sylv.wij.impl.util.Initializable;
import net.fabricmc.api.DedicatedServerModInitializer;

public class FabricWIJDedicated implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		WIJMain.platformProvider = new FabricPlatformProvider(null, new FabricServerSidedProvider());
		Initializable.initialize(FabricWIJMain.class);
		Initializable.initialize(WIJDedicated.class);
	}
}
