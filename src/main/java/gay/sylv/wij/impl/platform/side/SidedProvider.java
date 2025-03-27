package gay.sylv.wij.impl.platform.side;

import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * <h1>Sided Providers</h1>
 * Provides constructors and objects with definitions or behaviors specific to a logical and distribution side.
 * This class also provides logic specific to a mod loader <i>and</i> a side.
 * 
 * <h2>Terminology</h2>
 * "Client" refers to the logical and client distribution.
 * The client logical and distribution sides are one and the same.
 * <br>
 * <br>
 * "Server" refers to the logical server. This is included in the client and dedicated server distributions.
 * <br>
 * <br>
 * "Dedicated" or "Dedicated Server" refers to the dedicated server distribution.
 * This includes itself and the logical server's sided providers.
 * 
 * <h2>Notes</h2>
 * Methods prefixed with {@code make} return constructors.
 * 
 * @see DistSide
 * @see LogicalSide
 * @see PlatformSide
 */
public interface SidedProvider {
	BlockEntityType.BlockEntitySupplier<WorldJarBlockEntity> makeWorldJarBlockEntity();
}
