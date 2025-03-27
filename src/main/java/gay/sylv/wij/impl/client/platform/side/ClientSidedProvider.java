package gay.sylv.wij.impl.client.platform.side;

import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import gay.sylv.wij.impl.client.block.entity.ClientWorldJarBlockEntity;
import gay.sylv.wij.impl.platform.side.SidedProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;

public abstract class ClientSidedProvider implements SidedProvider {
	@Override
	public BlockEntityType.BlockEntitySupplier<WorldJarBlockEntity> makeWorldJarBlockEntity() {
		return ClientWorldJarBlockEntity::new;
	}
}
