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
package gay.sylv.wij.api.block;

import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static gay.sylv.wij.impl.util.Constants.vanilla;

/**
 * A type of wood's log.
 * @param id The path to the block texture.
 */
public record WoodLogType(ResourceLocation id, boolean strippable) {
	public static final WoodLogType ACACIA = new WoodLogType(vanilla("block/acacia_log"));
	public static final WoodLogType BIRCH = new WoodLogType(vanilla("block/birch_log"));
	public static final WoodLogType CHERRY = new WoodLogType(vanilla("block/cherry_log"));
	public static final WoodLogType DARK_OAK = new WoodLogType(vanilla("block/dark_oak_log"));
	public static final WoodLogType JUNGLE = new WoodLogType(vanilla("block/jungle"));
	public static final WoodLogType MANGROVE = new WoodLogType(vanilla("block/mangrove_log"));
	public static final WoodLogType OAK = new WoodLogType(vanilla("block/oak_log"));
	public static final WoodLogType SPRUCE = new WoodLogType(vanilla("block/spruce_log"));
	public static final WoodLogType WARPED = new WoodLogType(vanilla("block/warped_stem"));
	public static final WoodLogType CRIMSON = new WoodLogType(vanilla("block/crimson_stem"));
	
	private static final List<WoodLogType> TYPES = new ArrayList<>();
	
	private WoodLogType(ResourceLocation id) {
		this(id, true);
	}
	
	/**
	 * Reflectively scans a class for public static {@link WoodLogType} fields to register.
	 * @param typeSpace The utility class to scan reflectively.
	 */
	public static void registerAll(Class<?> typeSpace) {
		Arrays.stream(typeSpace.getDeclaredFields())
				.filter(WoodLogType::fieldCriterion)
				.map(WoodLogType::convert)
				.forEach(WoodLogType::register);
	}
	
	public static void register(WoodLogType type) {
		if (type != null) {
			TYPES.add(type);
		}
	}
	
	public static Collection<WoodLogType> getTypes() {
		return TYPES;
	}
	
	private static boolean fieldCriterion(Field field) {
		final int modifiers = field.getModifiers();
		final boolean modifiersOk = Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers);
		final boolean typeOk = field.getType().isAssignableFrom(WoodLogType.class);
		return modifiersOk && typeOk;
	}
	
	private static WoodLogType convert(Field field) {
		try {
			return (WoodLogType) field.get(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	static {
		registerAll(WoodLogType.class);
	}
}
