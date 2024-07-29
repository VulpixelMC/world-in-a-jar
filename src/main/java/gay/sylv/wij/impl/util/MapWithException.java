package gay.sylv.wij.impl.util;

import gay.sylv.wij.api.block.BarkType;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A grab-bag of utilities that can be mapped without javac throwing a fit about unhandled exceptions.
 * <p>
 * These methods have no guarantee of safety at runtime and may throw runtime exceptions.
 *
 * @see SafeMap
 */
public final class MapWithException {
	private MapWithException() {}
	
	public static InputStream newInputStream(Path path) {
		try {
			return Files.newInputStream(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static BarkType convert(Field field) {
		try {
			return Conversions.convert(field);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
