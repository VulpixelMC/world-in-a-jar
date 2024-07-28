package gay.sylv.wij.impl.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A grab-bag of utilities that can be safely mapped without javac throwing a fit about unhandled exceptions.
 * <p>
 * Note that most of these methods throw runtime exceptions. Check their implementation to be sure.
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
}
