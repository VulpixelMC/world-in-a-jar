package gay.sylv.wij.impl.block.entity.type;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Fabric-based extensions for {@link net.minecraft.world.level.block.entity.BlockEntityType}.
 * <p>
 * Implementations are provided in mixin.
 */
public interface WorldInAJarBlockEntityType {
	/**
	 * Extensions for {@link BlockEntityType.Builder}.
	 * <p>
	 * Implementations are provided in mixin.
	 * @param <T> The {@link BlockEntity} this type corresponds to.
	 */
	interface Builder<T extends BlockEntity> {
		/**
		 * @return the {@link BlockEntityType}.
		 * @see
		 */
		default BlockEntityType<T> build() {
			throw new AssertionError("Implemented in Mixin");
		}
	}
}
