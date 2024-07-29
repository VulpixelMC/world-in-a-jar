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
package gay.sylv.wij.impl.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

public final class Constants {
	public static final String MOD_ID = "worldinajar";
	public static final String MOD_NAME = "World In a Jar";
	/**
	 * destroy speed for unbreakable blocks
	 */
	public static final float UNBREAKABLE_DESTROY_SPEED = 75.0F;
	/**
	 * The color of the default texture mask used.
	 */
	public static final int TEXTURE_MASK = FastColor.ABGR32.color(255, 255, 0, 255);
	
	private Constants() {}
	
	public static ResourceLocation modId(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
	
	public static ResourceLocation vanilla(String path) {
		return ResourceLocation.withDefaultNamespace(path);
	}
}
