package gay.sylv.wij.api.block;

/**
 * A block that is used in a jar structure. These do not get rendered by the world jar by default.
 */
public interface JarContainmentBlock {
	default boolean renderInJar() {
		return false;
	}
}
