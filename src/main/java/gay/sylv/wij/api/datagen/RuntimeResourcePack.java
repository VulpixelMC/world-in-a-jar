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
package gay.sylv.wij.api.datagen;

import com.mojang.blaze3d.platform.NativeImage;
import gay.sylv.wij.impl.datagen.RuntimeResourcePackImpl;
import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Map;

public interface RuntimeResourcePack extends PackResources, ModResourcePack {
	static RuntimeResourcePack getInstance() {
		return RuntimeResourcePackImpl.INSTANCE;
	}
	
	Map<ResourceLocation, String> getItemMcmeta();
	
	void addItemMcmeta(ResourceLocation id, String mcmetaJson);
	
	Map<ResourceLocation, String> getItemTags();
	
	void addItemTag(ResourceLocation id, String tagJson);
	
	Map<ResourceLocation, String> getModels();
	
	void addModel(ResourceLocation id, String modelJson);
	
	Map<ResourceLocation, NativeImage> getItemTextures();
	
	void addItemTexture(ResourceLocation id, NativeImage image);
	
	@Nullable
	IoSupplier<InputStream> getResource(ResourceLocation id);
}
