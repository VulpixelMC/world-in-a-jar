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
package gay.sylv.wij.mixin.duck;

import gay.sylv.wij.impl.network.Networking;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Holds a return position. A mixin implements this interface on all {@link net.minecraft.world.entity.player.Player}s.
 */
public interface PlayerWithReturnPos {
	@Nullable Vec3 worldinajar$getReturnPos(Networking.JarLocation jarLocation);
	void worldinajar$setReturnPos(Networking.JarLocation jarLocation, Vec3 returnPos);
}
