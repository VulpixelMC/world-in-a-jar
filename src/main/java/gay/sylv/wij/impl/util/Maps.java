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
