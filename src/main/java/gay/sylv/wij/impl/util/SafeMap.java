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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * A grab-bag of utilities that can be safely mapped without javac throwing a fit about unhandled exceptions.
 * <p>
 * These methods are guaranteed to be safe at runtime and do not throw any exceptions. Instead, they may print to
 * stderr.
 *
 * @see MapWithException
 */
public final class SafeMap {
	private SafeMap() {}

    public static boolean isStaticAccessible(Field field, Class<?> type) {
        final int modifiers = field.getModifiers();
        final boolean modifiersOk = Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers);
        final boolean typeOk = field.getType().isAssignableFrom(type);
        return modifiersOk && typeOk;
    }
}
