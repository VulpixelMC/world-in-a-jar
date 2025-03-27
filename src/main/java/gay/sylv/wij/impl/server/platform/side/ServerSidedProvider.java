package gay.sylv.wij.impl.server.platform.side;

import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import gay.sylv.wij.impl.platform.side.SidedProvider;
import gay.sylv.wij.impl.server.block.entity.ServerWorldJarBlockEntity;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;

public abstract class ServerSidedProvider implements SidedProvider {
	@Override
	public BlockEntityType.BlockEntitySupplier<WorldJarBlockEntity> makeWorldJarBlockEntity() {
		return ServerWorldJarBlockEntity::new;
	}
	
	public abstract void sendCustomPacket(ServerPlayer player, CustomPacketPayload payload);
}
