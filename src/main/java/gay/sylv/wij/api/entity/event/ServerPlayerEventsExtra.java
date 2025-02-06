package gay.sylv.wij.api.entity.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;

/**
 * A collection of extra events missing in {@link net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents}.
 */
public final class ServerPlayerEventsExtra {
	/**
	 * An event that is called after a new player is placed in the world. This event is useful for sending packets using player data.
	 */
	public static final Event<AfterSpawn> AFTER_SPAWN = EventFactory.createArrayBacked(AfterSpawn.class, listeners -> (connection, player,  cookie) -> {
		for (AfterSpawn listener : listeners) {
			listener.afterSpawn(connection, player, cookie);
		}
	});
	
	@FunctionalInterface
	public interface AfterSpawn {
		/**
		 * Called after a new player is placed.
		 * @param connection The player's connection.
		 * @param player The player entity that spawned.
		 * @param cookie The player's cookie.
		 */
		void afterSpawn(Connection connection, ServerPlayer player, CommonListenerCookie cookie);
	}
}
