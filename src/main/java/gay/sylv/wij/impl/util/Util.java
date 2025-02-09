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

/**
 * Generalized utilities.
 */
public final class Util {
	private Util() {}
	
	/**
	 * Retrieves the caller of a method.
	 * @param callerDepth The depth of the caller up the call stack. This should be 1 if the caller of the current method is desired, 2 for the caller of the caller, etc.
	 * @return The caller of the method at the specified {@code callerDepth}.
	 */
	public static Class<?> getCaller(long callerDepth) {
		StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
		return walker.walk(
				frame -> frame
						.sequential()
						.limit(callerDepth + 2) // e.g. move depth up from getCaller (1) to requested caller of caller of getCaller (1 + 2)
						.map(StackWalker.StackFrame::getDeclaringClass)
						.toList()
						.getLast()
		);
	}
}
