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
 * @param fullWoodName The name of the fully bark-sided wood, usually the "wood" variant of a log.
 */
public record BarkType(ResourceLocation logTextureId, String woodName, String fullWoodName, int frames) implements GroupedIdentifier {
	public static final BarkType ACACIA = new BarkType(vanilla("acacia_log"));
	public static final BarkType BIRCH = new BarkType(vanilla("birch_log"));
	public static final BarkType CHERRY = new BarkType(vanilla("cherry_log"));
	public static final BarkType DARK_OAK = new BarkType(vanilla("dark_oak_log"));
	public static final BarkType JUNGLE = new BarkType(vanilla("jungle_log"));
	public static final BarkType MANGROVE = new BarkType(vanilla("mangrove_log"));
	public static final BarkType OAK = new BarkType(vanilla("oak_log"));
	public static final BarkType SPRUCE = new BarkType(vanilla("spruce_log"));
	public static final BarkType WARPED = new BarkType(vanilla("warped_stem"), "hyphae", 5);
	public static final BarkType CRIMSON = new BarkType(vanilla("crimson_stem"), "hyphae", 5);
	
	private static final List<BarkType> TYPES = new ArrayList<>();
	private static final FileToIdConverter PNG_LISTER = new FileToIdConverter("textures/block", ".png");
	
	private BarkType(ResourceLocation id) {
		this(id, "wood");
	}
	
	private BarkType(ResourceLocation id, String fullWoodName, int frames) {
		this(id, defaultWoodName(id), fullWoodName, frames);
	}
	
	private BarkType(ResourceLocation id, String fullWoodName) {
		this(id, fullWoodName, 1);
	}
	
	public boolean isAnimated() {
		return frames > 1;
	}
	
	/**
	 * Uses a {@link FileToIdConverter} to convert a resource ID to a fully qualified resource path.
	 * @return a fully qualified {@link ResourceLocation}.
	 */
	public ResourceLocation toFilePath() {
		return PNG_LISTER.idToFile(logTextureId);
	}
	
	public ResourceLocation getLogBlockId() {
		return logTextureId;
	}
	
	public ResourceLocation getWoodBlockId() {
		return logTextureId.withPath(woodName + "_" + fullWoodName);
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
