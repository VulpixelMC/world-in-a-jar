package gay.sylv.wij.impl.util;

import org.jetbrains.annotations.Nullable;

public final class Assertions {
	private Assertions() {}
	
	public static void instanceOf(@Nullable Object object, Class<?> typeClass) {
		if (object != null && !typeClass.isInstance(object)) {
			throw new ClassCastException("Cannot cast object of type " + object.getClass().getName() + " to " + typeClass.getName());
		}
	}
}
