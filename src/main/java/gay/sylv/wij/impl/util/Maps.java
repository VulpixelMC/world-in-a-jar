package gay.sylv.wij.impl.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for working with {@link java.util.Map}s.
 */
public final class Maps {
	private Maps() {}
	
	@SafeVarargs
	public static <K, V> Map<K, V> hashMapOf(Pair<K, V>... pairs) {
		Map<K, V> map = new HashMap<>();
		Arrays.stream(pairs).map(pair -> new Pair<>(map, pair)).forEach(Maps::put);
		return map;
	}
	
	private static <K, V> void put(Pair<Map<K, V>, Pair<K, V>> mapPairPair) {
		mapPairPair.first.put(mapPairPair.second.first, mapPairPair.second.second);
	}
}
