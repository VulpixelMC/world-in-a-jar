package gay.sylv.wij.impl.util;

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
}
