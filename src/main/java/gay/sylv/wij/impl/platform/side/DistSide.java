package gay.sylv.wij.impl.platform.side;

/**
 * The "distribution side" (dedicated vs. client) as opposed to the logical side (server vs. client).
 */
public enum DistSide {
	CLIENT,
	DEDICATED;
	
	public LogicalSide getDefaultLogicalSide() {
		return switch (this) {
			case CLIENT -> LogicalSide.CLIENT;
			case DEDICATED -> LogicalSide.SERVER;
		};
	}
}
