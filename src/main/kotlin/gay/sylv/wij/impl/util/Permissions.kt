/**
 * World In a Jar
 * Copyright (C) 2023  Sylv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package gay.sylv.wij.impl.util

import gay.sylv.wij.impl.WIJConstants.MOD_ID
import gay.sylv.wij.impl.addon.Addons
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.entity.player.PlayerEntity

/**
 * A utility object used to determine if criteria for permissions has been met.
 * @author sylv
 */
object Permissions {
	private const val LOCK_JAR = "$MOD_ID.lock_jar"
	
	/**
	 * Checks if the player can lock/unlock jars.
	 * @author sylv
	 */
	fun canLockJars(player: PlayerEntity): Boolean {
		if (Addons.HAS_FABRIC_PERMS) {
			if (Permissions.check(player, LOCK_JAR)) {
				return true
			}
		} else if (player.hasPermissionLevel(2)) {
			return true
		}
		return false
	}
}
