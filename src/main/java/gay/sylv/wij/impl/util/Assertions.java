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

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public final class Assertions {
	private Assertions() {}
	
	public static void instanceOf(@Nullable Object object, Class<?> typeClass) {
		if (object != null && !typeClass.isInstance(object)) {
			throw new ClassCastException("Cannot cast object of type " + object.getClass().getName() + " to " + typeClass.getName());
		}
	}
	
	/**
	 * Checks if callers are permitted to call the method calling this method.
	 * @param allowedCallers Callers that are permitted to call the calling method.
	 * @throws IllegalAccessException Thrown if the caller is not permitted.   
	 */
	public static void checkCaller(Class<?>... allowedCallers) throws IllegalAccessException {
		if (allowedCallers == null || allowedCallers.length == 0) {
			return;
		}
		
		StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
		Class<?> caller = walker.walk(
				frame -> frame
						.sequential()
						.map(StackWalker.StackFrame::getDeclaringClass)
						.limit(3)
						.toList()
						.getLast()
		);
		boolean allowed = Arrays.asList(allowedCallers).contains(caller);
		
		if (!allowed) {
			throw new IllegalAccessException(caller.getName());
		}
	}
}
