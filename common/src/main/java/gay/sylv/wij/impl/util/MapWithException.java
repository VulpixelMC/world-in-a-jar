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

import gay.sylv.wij.api.block.BarkType;

import java.lang.reflect.Field;

/**
 * A grab-bag of utilities that can be mapped without javac throwing a fit about unhandled exceptions.
 * <p>
 * These methods have no guarantee of safety at runtime and may throw runtime exceptions.
 *
 * @see SafeMap
 */
public final class MapWithException {
	private MapWithException() {}
	
	public static BarkType convert(Field field) {
		try {
			return Conversions.convert(field);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
