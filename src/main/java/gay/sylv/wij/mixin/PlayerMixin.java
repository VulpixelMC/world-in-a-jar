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
package gay.sylv.wij.mixin;

import gay.sylv.wij.impl.network.Networking;
import gay.sylv.wij.impl.duck.PlayerWithReturnDim;
import gay.sylv.wij.impl.duck.PlayerWithReturnPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(Player.class)
public class PlayerMixin implements PlayerWithReturnPos, PlayerWithReturnDim {
	@Unique
	private Map<Networking.JarLocation, ResourceKey<Level>> returnDimensions;
	/**
	 * 🥺
	 */
	@Unique
	private Map<Networking.JarLocation, Vec3> returnPossies;
	
	@Override
	public ResourceKey<Level> worldinajar$getReturnDimension(Networking.JarLocation jarLocation) {
		return returnDimensions.get(jarLocation);
	}
	
	@Override
	public void worldinajar$setReturnDimension(Networking.JarLocation jarLocation, ResourceKey<Level> dimension) {
		returnDimensions.put(jarLocation, dimension);
	}
	
	@Override
	public Vec3 worldinajar$getReturnPos(Networking.JarLocation jarLocation) {
		return returnPossies.get(jarLocation);
	}
	
	@Override
	public void worldinajar$setReturnPos(Networking.JarLocation jarLocation, Vec3 returnPos) {
		returnPossies.put(jarLocation, returnPos);
	}
}
