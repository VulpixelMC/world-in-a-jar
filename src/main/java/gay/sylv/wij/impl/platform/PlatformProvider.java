package gay.sylv.wij.impl.platform;

import gay.sylv.wij.impl.platform.side.DistSide;
import gay.sylv.wij.impl.platform.side.LogicalSide;
import gay.sylv.wij.impl.platform.side.PlatformSide;
import gay.sylv.wij.impl.platform.side.SidedProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Provides loader-specific functionality.
 * <br>
 * Methods prefixed with {@code make} return constructors.
 */
public abstract class PlatformProvider {
	@Nullable
	private final SidedProvider clientSidedProvider;
	@Nullable
	private final SidedProvider serverSidedProvider;
	
	public PlatformProvider(@Nullable SidedProvider clientSidedProvider, @Nullable SidedProvider serverSidedProvider) {
		this.clientSidedProvider = clientSidedProvider;
		this.serverSidedProvider = serverSidedProvider;
	}
	
	/**
	 * @return The {@link SidedProvider} according to the {@link LogicalSide}.
	 * @throws NullPointerException If the {@link SidedProvider} is not present in this distribution side.
	 * @throws ClassCastException If the chosen type does not correspond to the {@link LogicalSide}.
	 */
	public SidedProvider getSidedProvider() throws NullPointerException, ClassCastException {
		return getSidedProvider(getDistSide().getDefaultLogicalSide());
	}
	
	/**
	 * @param side The "distribution side" (i.e. dedicated server vs. client).
	 * @return The {@link SidedProvider} according to the {@link LogicalSide}.
	 * @param <T> The {@link SidedProvider} corresponding to the {@link LogicalSide}.
	 * @throws NullPointerException If the {@link SidedProvider} is not present in this distribution side.
	 * @throws ClassCastException If the chosen type does not correspond to the {@link LogicalSide}.
	 */
	public <T extends SidedProvider> T getSidedProvider(DistSide side) throws NullPointerException, ClassCastException {
		return getSidedProvider(side.getDefaultLogicalSide());
	}
	
	/**
	 * @param side The "logical side" (i.e. dedicated/integrated server vs. client).
	 * @return The {@link SidedProvider} according to the {@link LogicalSide}.
	 * @param <T> The {@link SidedProvider} corresponding to the {@link LogicalSide}.
	 * @throws NullPointerException If the {@link SidedProvider} is not present in this distribution side.
	 * @throws ClassCastException If the chosen type does not correspond to the {@link LogicalSide}.
	 */
	public <T extends SidedProvider> T getSidedProvider(LogicalSide side) throws NullPointerException, ClassCastException {
		return getSidedProvider(side.getGeneralPlatformSide());
	}
	
	/**
	 * @param side The "platform side" (i.e. dedicated server vs. server vs. client).
	 * @return The {@link SidedProvider} according to the {@link PlatformSide}.
	 * @param <T> The {@link SidedProvider} corresponding to the {@link PlatformSide}.
	 * @throws NullPointerException If the {@link SidedProvider} is not present in this distribution side.
	 * @throws ClassCastException If the chosen type does not correspond to the {@link PlatformSide}.
	 */
	@SuppressWarnings("unchecked") // will result in a ClassCastException if the user doesn't pick the right type
	public <T extends SidedProvider> T getSidedProvider(PlatformSide side) throws NullPointerException, ClassCastException {
		switch (side) {
			case CLIENT -> {
				if (clientSidedProvider != null) {
					return (T) clientSidedProvider;
				} else {
					throw new NullPointerException("Attempted to access client-side logic on non-client distribution");
				}
			}
			case SERVER -> {
				if (serverSidedProvider != null) {
					return (T) serverSidedProvider;
				} else {
					throw new NullPointerException("Attempted to access server-side logic on non-server distribution");
				}
			}
			default -> throw new IllegalStateException("Unknown logical side" + side);
		}
	}
	
	public abstract DistSide getDistSide();
}
