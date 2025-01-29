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
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Holds a return dimension ({@link net.minecraft.resources.ResourceKey}&lt;{@link net.minecraft.world.level.Level}&gt;). A mixin implements this class on every {@link net.minecraft.world.entity.player.Player}.
 */
public interface PlayerWithReturnDim {
	@Nullable ResourceKey<Level> worldinajar$getReturnDimension(Networking.JarLocation jarLocation);
	void worldinajar$setReturnDimension(Networking.JarLocation jarLocation, ResourceKey<Level> dimension);
}
