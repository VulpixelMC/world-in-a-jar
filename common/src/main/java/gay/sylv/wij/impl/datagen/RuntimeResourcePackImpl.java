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
package gay.sylv.wij.impl.datagen;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import gay.sylv.wij.api.datagen.RuntimeResourcePack;
import gay.sylv.wij.impl.WorldInAJar;
import gay.sylv.wij.impl.util.Constants;
import gay.sylv.wij.impl.util.Conversions;
import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.FileUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class RuntimeResourcePackImpl implements RuntimeResourcePack, PackResources, ModResourcePack {
	public static final RuntimeResourcePackImpl INSTANCE = new RuntimeResourcePackImpl();
	public static final String PACK_ID = Constants.COMPAT_MOD_ID + "_rrp";
	public static final PackSelectionConfig BUILT_IN_SELECTION_CONFIG = new PackSelectionConfig(true, Pack.Position.TOP, false);
	public static final Pack.ResourcesSupplier FIXED_RESOURCES = fixedResources();
	
	private static final PackLocationInfo LOCATION = new PackLocationInfo(PACK_ID, Component.literal(Constants.MOD_NAME + " RRP"), PackSource.BUILT_IN, Optional.empty());
	private static final Map<ResourceLocation, NativeImage> ITEM_TEXTURES = new HashMap<>();
	private static final Map<ResourceLocation, String> ITEM_TEXTURE_MCMETA = new HashMap<>();
	private static final Map<ResourceLocation, String> MODELS = new HashMap<>();
	private static final Map<ResourceLocation, String> ITEM_TAGS = new HashMap<>();
	private static final FileToIdConverter ITEM_PNG_LISTER = new FileToIdConverter("textures/item", ".png");
	private static final FileToIdConverter ITEM_PNG_MCMETA_LISTER = new FileToIdConverter("textures/item", ".png.mcmeta");
	private static final FileToIdConverter JSON_MODEL_LISTER = new FileToIdConverter("models", ".json");
	private static final FileToIdConverter JSON_ITEM_TAG_LISTER = FileToIdConverter.json("tags/item");
	
	@Override
	public Map<ResourceLocation, String> getItemMcmeta() {
		return ITEM_TEXTURE_MCMETA;
	}
	
	@Override
	public void addItemMcmeta(ResourceLocation id, String mcmetaJson) {
		ITEM_TEXTURE_MCMETA.put(ITEM_PNG_MCMETA_LISTER.idToFile(id), mcmetaJson);
	}
	
	@Override
	public Map<ResourceLocation, String> getItemTags() {
		return ITEM_TAGS;
	}
	
	@Override
	public void addItemTag(ResourceLocation id, String tagJson) {
		if (!ITEM_TAGS.containsKey(JSON_ITEM_TAG_LISTER.idToFile(id))) {
			ITEM_TAGS.put(JSON_ITEM_TAG_LISTER.idToFile(id), tagJson);
		} else {
			Gson gson = new Gson();
			JsonObject element = gson.fromJson(tagJson, JsonElement.class).getAsJsonObject();
			JsonArray newTags = element.get("values").getAsJsonArray();
			
			String oldTagJson = ITEM_TAGS.get(JSON_ITEM_TAG_LISTER.idToFile(id));
			JsonObject oldElement = gson.fromJson(oldTagJson, JsonElement.class).getAsJsonObject();
			JsonArray oldTags = oldElement.get("values").getAsJsonArray();
			
			newTags.addAll(oldTags);
			Collection<ResourceLocation> ids = newTags.asList().stream()
					.map(JsonElement::getAsString)
					.map(ResourceLocation::tryParse)
					.collect(Collectors.toCollection(ArrayList::new));
			ITEM_TAGS.put(JSON_ITEM_TAG_LISTER.idToFile(id), generatedTag(ids, false));
		}
	}
	
	@Override
	public Map<ResourceLocation, String> getModels() {
		return MODELS;
	}
	
	@Override
	public void addModel(ResourceLocation id, String modelJson) {
		MODELS.put(JSON_MODEL_LISTER.idToFile(id), modelJson);
	}
	
	@Override
	public Map<ResourceLocation, NativeImage> getItemTextures() {
		return ITEM_TEXTURES;
	}
	
	@Override
	public void addItemTexture(ResourceLocation id, NativeImage image) {
		ITEM_TEXTURES.put(ITEM_PNG_LISTER.idToFile(id), image);
	}
	
	@Nullable
	@Override
	public IoSupplier<InputStream> getResource(ResourceLocation id) {
		IoSupplier<InputStream> supplier = getResource(PackType.CLIENT_RESOURCES, id);
		if (supplier == null) {
			return getResource(PackType.SERVER_DATA, id);
		} else {
			return supplier;
		}
	}
	
	public static String generatedTag(Collection<ResourceLocation> ids, boolean replace) {
		Gson gson = new Gson();
		return String.format("""
			{
				"replace": %s,
				"values": %s
			}
			""", replace, gson.toJson(ids.stream().map(ResourceLocation::toString).collect(Collectors.toList())));
	}
	
	private static Pack.ResourcesSupplier fixedResources() {
		return new Pack.ResourcesSupplier() {
			@Override
			public @NotNull PackResources openPrimary(PackLocationInfo location) {
				return INSTANCE;
			}
			
			@Override
			public @NotNull PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
				return INSTANCE;
			}
		};
	}
	
	@Override
	public ModMetadata getFabricModMetadata() {
		return WorldInAJar.getModContainer().getMetadata();
	}
	
	@Override
	public ModResourcePack createOverlay(String overlay) {
		return new RuntimeResourcePackImpl();
	}
	
	@Nullable
	@Override
	public IoSupplier<InputStream> getRootResource(String... elements) {
		FileUtil.validatePath(elements);
		Optional<Path> optionalPath = WorldInAJar.getModContainer().findPath("rrp/" + String.join("/", elements));
		return optionalPath.map(IoSupplier::create).orElse(null);
	}
	
	@Nullable
	@Override
	public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation id) {
		if (packType == PackType.CLIENT_RESOURCES) {
			if (ITEM_TEXTURES.containsKey(id)) {
				return Conversions.convert(ITEM_TEXTURES.get(id));
			} else if (ITEM_TEXTURE_MCMETA.containsKey(id)) {
				return Conversions.convert(ITEM_TEXTURE_MCMETA.get(id));
			} if (MODELS.containsKey(id)) {
				return Conversions.convert(MODELS.get(id));
			}
		} else if (packType == PackType.SERVER_DATA) {
			if (ITEM_TAGS.containsKey(id)) {
				return Conversions.convert(ITEM_TAGS.get(id));
			}
		}
		
		return null;
	}
	
	@Override
	public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
		if (Objects.equals(namespace, Constants.COMPAT_MOD_ID)) {
			if (packType == PackType.CLIENT_RESOURCES) {
				if (path.equals("textures/item")) {
					for (var entry : ITEM_TEXTURES.entrySet()) {
						resourceOutput.accept(entry.getKey(), Conversions.convert(entry.getValue()));
					}
					for (var entry : ITEM_TEXTURE_MCMETA.entrySet()) {
						resourceOutput.accept(entry.getKey(), Conversions.convert(entry.getValue()));
					}
				} else if (path.equals("models")) {
					for (var entry : MODELS.entrySet()) {
						resourceOutput.accept(entry.getKey(), Conversions.convert(entry.getValue()));
					}
				}
			} else if (packType == PackType.SERVER_DATA) {
				if (path.equals("tags/item")) {
					for (var entry : ITEM_TAGS.entrySet()) {
						resourceOutput.accept(entry.getKey(), Conversions.convert(entry.getValue()));
					}
				}
			}
		}
	}
	
	@Override
	public @NotNull Set<String> getNamespaces(PackType type) {
		if (type == PackType.CLIENT_RESOURCES || type == PackType.SERVER_DATA) {
			return Set.of(Constants.COMPAT_MOD_ID);
		} else {
			return Set.of();
		}
	}
	
	@Nullable
	@Override
	public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException {
		return AbstractPackResources.getMetadataFromStream(deserializer, Objects.requireNonNull(getRootResource("pack.mcmeta")).get());
	}
	
	@Override
	public @NotNull PackLocationInfo location() {
		return LOCATION;
	}
	
	@Override
	public void close() {
	}
}
