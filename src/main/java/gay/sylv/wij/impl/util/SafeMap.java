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
