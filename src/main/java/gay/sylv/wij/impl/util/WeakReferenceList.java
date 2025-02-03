package gay.sylv.wij.impl.util;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * A type of {@link ArrayList} that automatically clears dangling {@link SoftReference}s.
 * @param <E>
 */
public class WeakReferenceList<E> extends ArrayList<SoftReference<E>> {
	public void addAuto(E item) {
		super.add(new SoftReference<>(item));
	}
	
	public void forEachAuto(Consumer<? super E> action) {
		super.forEach(item -> {
			if (item.get() == null) {
				this.remove(item);
			}
			
			action.accept(item.get());
		});
	}
}
