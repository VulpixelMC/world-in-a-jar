package gay.sylv.wij.impl.platform;

import gay.sylv.wij.impl.platform.side.DistSide;
import gay.sylv.wij.impl.platform.side.SidedProvider;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

public class FabricPlatformProvider extends PlatformProvider {
	public FabricPlatformProvider(@Nullable SidedProvider clientSidedProvider, @Nullable SidedProvider serverSidedProvider) {
		super(clientSidedProvider, serverSidedProvider);
	}
	
	@Override
	public DistSide getDistSide() {
		return switch (FabricLoader.getInstance().getEnvironmentType()) {
			case CLIENT -> DistSide.CLIENT;
			case SERVER -> DistSide.DEDICATED;
		};
	}
}
