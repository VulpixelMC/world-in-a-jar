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
