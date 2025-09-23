/**
 * World In a Jar
 * Copyright (C) 2024  VulpixelMC
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package gay.sylv.wij.api.entity.event;

import dev.yumi.commons.event.Event;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;

import static gay.sylv.wij.api.event.Events.EVENT_MANAGER;

/**
 * A collection of extra events missing in Fabric's {@code ServerPlayerEvents}.
 */
public final class ServerPlayerEventsExtra {
	/**
	 * An event that is called after a new player is placed in the world. This event is useful for sending packets using player data.
	 */
	public static final Event<String, AfterSpawn> AFTER_SPAWN = EVENT_MANAGER.create(AfterSpawn.class);

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
