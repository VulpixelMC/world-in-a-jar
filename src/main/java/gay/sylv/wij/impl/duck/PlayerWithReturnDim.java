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
package gay.sylv.wij.impl.duck;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

// todo create api
/**
 * Holds a return dimension ({@link RegistryKey}<{@link World}>). A mixin implements this class on every {@link net.minecraft.entity.player.PlayerEntity}.
 */
@SuppressWarnings("unused")
public interface PlayerWithReturnDim {
	RegistryKey<World> worldinajar$getReturnDim();
	void worldinajar$setReturnDim(RegistryKey<World> returnDim);
}
