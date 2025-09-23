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
package gay.sylv.wij.impl.duck;

import gay.sylv.wij.impl.network.Networking;

import java.util.Optional;

/**
 * Holds the location of the jar which the player entered. A mixin implements this interface on all {@link net.minecraft.world.entity.player.Player}s.
 */
public interface PlayerWithEnteredJar {
	Optional<Networking.JarLocation> worldinajar$getJarLocation();
	void worldinajar$setJarLocation(Networking.JarLocation location);
}
