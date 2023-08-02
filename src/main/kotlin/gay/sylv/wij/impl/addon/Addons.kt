/**
 * World In a Jar
 * Copyright (C) 2023  Sylv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package gay.sylv.wij.impl.addon

import gay.sylv.wij.api.Initializable
import gay.sylv.wij.impl.addon.fpa.FabricPermsAddon
import org.quiltmc.loader.api.QuiltLoader

/**
 * A handler for a collection of addons (mod integrations) and contains data about addons.
 * @author sylv
 */
object Addons : Initializable {
	val HAS_FABRIC_PERMS: Boolean
		get() = hasFabricPerms
	
	private var hasFabricPerms: Boolean = false
	
	override fun initialize() {
		if (QuiltLoader.isModLoaded("fabric-permissions-api-v0")) {
			hasFabricPerms = true
			FabricPermsAddon.initialize()
		}
	}
}
