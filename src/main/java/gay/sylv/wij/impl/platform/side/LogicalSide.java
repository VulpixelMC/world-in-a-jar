package gay.sylv.wij.impl.platform.side;

/**
 * The "logical side" (server vs. client) as opposed to the distribution side (dedicated vs. client).
 */
public enum LogicalSide {
	CLIENT,
	SERVER;
	
	public PlatformSide getGeneralPlatformSide() {
		return switch (this) {
			case CLIENT -> PlatformSide.CLIENT;
			case SERVER -> PlatformSide.SERVER;
		};
	}
}
