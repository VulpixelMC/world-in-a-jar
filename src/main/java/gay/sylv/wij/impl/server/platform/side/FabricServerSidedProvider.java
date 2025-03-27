package gay.sylv.wij.impl.server.platform.side;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class FabricServerSidedProvider extends ServerSidedProvider {
	@Override
	public void sendCustomPacket(ServerPlayer player, CustomPacketPayload payload) {
		ServerPlayNetworking.send(player, payload);
	}
}
