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

import com.mojang.blaze3d.platform.NativeImage;
import gay.sylv.wij.impl.Main;
import gay.sylv.wij.impl.util.Constants;
import gay.sylv.wij.impl.util.Conversions;
import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.FileUtil;
import net.minecraft.network.chat.Component;
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

public class RuntimeResourcePack implements PackResources, ModResourcePack {
	public static final RuntimeResourcePack INSTANCE = new RuntimeResourcePack();
	public static final String PACK_ID = Constants.MOD_ID + "_rrp";
	public static final PackSelectionConfig BUILT_IN_SELECTION_CONFIG = new PackSelectionConfig(true, Pack.Position.TOP, false);
	public static final Pack.ResourcesSupplier FIXED_RESOURCES = fixedResources();
	
	private static final PackLocationInfo LOCATION = new PackLocationInfo(PACK_ID, Component.literal(Constants.MOD_NAME + " RRP"), PackSource.BUILT_IN, Optional.empty());
	private static final Map<ResourceLocation, NativeImage> TEXTURES = new HashMap<>();
	
	public static Set<Map.Entry<ResourceLocation, NativeImage>> getTextures() {
		return TEXTURES.entrySet();
	}
	
	static void addTexture(ResourceLocation id, NativeImage image) {
		TEXTURES.put(id, image);
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
		return Main.getModContainer().getMetadata();
	}
	
	@Override
	public ModResourcePack createOverlay(String overlay) {
		return new RuntimeResourcePack();
	}
	
	@Nullable
	@Override
	public IoSupplier<InputStream> getRootResource(String... elements) {
		FileUtil.validatePath(elements);
		Optional<Path> optionalPath = Main.getModContainer().findPath("rrp/" + String.join("/", elements));
		return optionalPath.map(IoSupplier::create).orElse(null);
	}
	
	@Nullable
	@Override
	public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation id) {
		if (packType == PackType.CLIENT_RESOURCES && TEXTURES.containsKey(id)) {
			return Conversions.convert(TEXTURES.get(id));
		} else {
			return null;
		}
	}
	
	@Override
	public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
		if (Objects.equals(namespace, Constants.MOD_ID) && packType == PackType.CLIENT_RESOURCES) {
			for (var entry : TEXTURES.entrySet()) {
				resourceOutput.accept(entry.getKey(), Conversions.convert(entry.getValue()));
			}
		}
	}
	
	@Override
	public @NotNull Set<String> getNamespaces(PackType type) {
		if (type == PackType.CLIENT_RESOURCES) {
			return Set.of(Constants.MOD_ID);
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
