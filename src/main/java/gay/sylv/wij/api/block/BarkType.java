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

import gay.sylv.wij.api.datagen.GroupedIdentifier;
import gay.sylv.wij.impl.datagen.DynamicDataGenerator;
import gay.sylv.wij.impl.util.MapWithException;
import gay.sylv.wij.impl.util.SafeMap;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static gay.sylv.wij.impl.util.Constants.vanilla;

/**
 * A type of wood's bark.
 * @param logTextureId The path to the block's log side / bark texture.
 * @param woodName The name of the wood (without the log/stem part).
 * @param strippable If the wood can be stripped with an axe or should be treated as such.
 */
public record BarkType(ResourceLocation logTextureId, String woodName, boolean strippable) implements GroupedIdentifier {
	public static final BarkType ACACIA = new BarkType(vanilla("block/acacia_log"));
	public static final BarkType BIRCH = new BarkType(vanilla("block/birch_log"));
	public static final BarkType CHERRY = new BarkType(vanilla("block/cherry_log"));
	public static final BarkType DARK_OAK = new BarkType(vanilla("block/dark_oak_log"));
	public static final BarkType JUNGLE = new BarkType(vanilla("block/jungle_log"));
	public static final BarkType MANGROVE = new BarkType(vanilla("block/mangrove_log"));
	public static final BarkType OAK = new BarkType(vanilla("block/oak_log"));
	public static final BarkType SPRUCE = new BarkType(vanilla("block/spruce_log"));
	public static final BarkType WARPED = new BarkType(vanilla("block/warped_stem"), false);
	public static final BarkType CRIMSON = new BarkType(vanilla("block/crimson_stem"), false);
	
	private static final List<BarkType> TYPES = new ArrayList<>();
	private static final FileToIdConverter PNG_LISTER = new FileToIdConverter("textures", ".png");
	
	private BarkType(ResourceLocation id) {
		this(id, true);
	}
	
	private BarkType(ResourceLocation id, boolean strippable) {
		this(id, defaultWoodName(id), strippable);
	}
	
	/**
	 * Uses a {@link FileToIdConverter} to convert a resource ID to a fully qualified resource path.
	 * @return a fully qualified {@link ResourceLocation}.
	 */
	public ResourceLocation toFilePath() {
		return PNG_LISTER.idToFile(logTextureId);
	}
	
	@Override
	public String getGroup() {
		return "bark";
	}
	
	@Override
	public String getNamespace() {
		return logTextureId.getNamespace();
	}
	
	@Override
	public String getPath() {
		return woodName();
	}
	
	/**
	 * Reflectively scans a class for public static {@link BarkType} fields to register.
	 * @param utilityClass The utility class to scan reflectively.
	 */
	public static void registerAll(Class<?> utilityClass) {
		Arrays.stream(utilityClass.getDeclaredFields())
				.filter(field -> SafeMap.isStaticAccessible(field, BarkType.class))
				.map(MapWithException::convert)
				.peek(BarkType::register)
				.filter(BarkType::strippable)
				.peek(DynamicDataGenerator.ItemGenerator::generateModel)
				.peek(DynamicDataGenerator.ItemGenerator::generateBarkTag)
				.forEach(DynamicDataGenerator.ItemGenerator::registerBark);
	}
	
	public static void register(BarkType type) {
		if (type != null) {
			TYPES.add(type);
		}
	}
	
	public static Collection<BarkType> getTypes() {
		return TYPES;
	}
	
	private static String defaultWoodName(ResourceLocation id) {
		String path = id.getPath();
		int logIndex = path.lastIndexOf("_log");
		int stemIndex = path.lastIndexOf("_stem");
		return path.substring(0, logIndex != -1 ? logIndex : stemIndex).replace("block/", "");
	}
}
