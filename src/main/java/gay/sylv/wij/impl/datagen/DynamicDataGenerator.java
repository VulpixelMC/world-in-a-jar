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
import gay.sylv.wij.impl.util.Initializable;
import gay.sylv.wij.impl.util.MapWithException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;

import static gay.sylv.wij.impl.util.Constants.modId;

public class DynamicDataGenerator implements Initializable {
	public static final DynamicDataGenerator INSTANCE = new DynamicDataGenerator();
	
	private DynamicDataGenerator() {}
	
	@Override
	public void initialize() {
		if (Main.isClient()) {
			TextureGenerator.INSTANCE.initialize();
		}
	}
	
	@Environment(EnvType.CLIENT)
	private static final class TextureGenerator implements Initializable {
		static final TextureGenerator INSTANCE = new TextureGenerator();
		
		private TextureGenerator() {}
		
		@Override
		public void initialize() {
			this.generate(Minecraft.getInstance().getTextureManager());
		}
		
		public void generate(TextureManager manager) {
			InputStream inputStream = Main.getModContainer().findPath("assets/" + Constants.MOD_ID + "/icon.png").map(MapWithException::newInputStream).orElse(null);
			if (inputStream != null) {
				try {
					add(modId("icon"), NativeImage.read(inputStream));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		private void add(ResourceLocation id, NativeImage image) {
			RuntimeResourcePack.addTexture(id, image);
		}
	}
}
