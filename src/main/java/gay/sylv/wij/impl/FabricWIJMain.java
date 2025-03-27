package gay.sylv.wij.impl;

import gay.sylv.wij.api.entity.event.ServerPlayerEventsExtra;
import gay.sylv.wij.impl.util.Initializable;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class FabricWIJMain implements Initializable {
	@Override
	public void initialize() {
		Initializable.initialize(WIJMain.class);
		
		ServerLifecycleEvents.SERVER_STARTED.register(WIJMain::onServerStart);
		ServerLifecycleEvents.SERVER_STOPPED.register(WIJMain::onServerStop);
		
		ServerPlayerEventsExtra.AFTER_SPAWN.register(
				(connection, player1, cookie) ->
						WIJMain.afterPlayerSpawn(player1)
		);
		
		ServerPlayConnectionEvents.DISCONNECT.register(WIJMain::onPlayerDisconnect);
		
		ServerLifecycleEvents.SERVER_STOPPING.register(WIJMain::onServerStopping);
		
		PlayerBlockBreakEvents.BEFORE.register(
				(level, player, pos, state, blockEntity) ->
						WIJMain.beforePlayerBlockBreak(level, player, pos, state)
		);
	}
}
