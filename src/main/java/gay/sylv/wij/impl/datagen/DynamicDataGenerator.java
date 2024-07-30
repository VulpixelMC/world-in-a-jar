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
import gay.sylv.wij.api.block.BarkType;
import gay.sylv.wij.api.datagen.RuntimeResourcePack;
import gay.sylv.wij.impl.Main;
import gay.sylv.wij.impl.item.BarkItem;
import gay.sylv.wij.impl.item.Items;
import gay.sylv.wij.impl.util.Constants;
import gay.sylv.wij.impl.util.Initializable;
import gay.sylv.wij.impl.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static gay.sylv.wij.impl.datagen.RuntimeResourcePackImpl.generatedTag;
import static gay.sylv.wij.impl.util.Constants.modId;

public final class DynamicDataGenerator implements Initializable {
	public static final DynamicDataGenerator INSTANCE = new DynamicDataGenerator();
	
	private DynamicDataGenerator() {}
	
	@Override
	public void initialize() {
		if (Main.isClient()) {
			BarkType.registerAll(BarkType.class);
		}
	}
	
	public static final class ItemGenerator {
		private ItemGenerator() {}
		
		public static void registerBark(BarkType type) {
			Item barkItem = Registry.register(
					BuiltInRegistries.ITEM,
					type.getIdentifier(),
					new BarkItem(
							new Item.Properties()
									.component(DataComponents.LORE, new ItemLore(List.of(Component.translatable("worldinajar.lore.bark")))),
							type
					)
			);
			Items.BARK.put(type, barkItem);
		}
		
		public static void generateBarkTag(BarkType type) {
			String generatedTag = generatedTag(List.of(type.getIdentifier()), false);
			RuntimeResourcePack.getInstance().addItemTag(modId("bark"), generatedTag);
		}
		
		public static void generateModel(BarkType type) {
			RuntimeResourcePack.getInstance().addModel(type.getResourceIdentifier("item"), generatedModel(type.getResourceIdentifier("item")));
		}
		
		private static String generatedModel(ResourceLocation id) {
			return String.format("""
              {
              	"parent": "item/generated",
              	"textures": {
              		"layer0": "%1$s"
              	}
              }
              """, id.toString());
		}
	}
	
	@Environment(EnvType.CLIENT)
	public static final class TextureGenerator {
		private TextureGenerator() {}
		
		public static void generate(RuntimeResourcePack rrp, ResourceManager manager) {
			NativeImage barkMask = getTexture(rrp, "bark.png");
			
			BarkType.getTypes().stream()
					.filter(BarkType::strippable)
					.map(type -> {
						NativeImage log;
						try {
							log = NativeImage.read(manager.getResourceOrThrow(type.toFilePath()).open());
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						
						return new Pair<>(maskImage(barkMask, log), type);
					})
					.forEach((pair) -> {
						NativeImage barkImage = pair.first();
						BarkType type = pair.second();
						rrp.addItemTexture(type.getIdentifier(), barkImage);
					});
		}
		
		private static @NotNull NativeImage getTexture(RuntimeResourcePack rrp, String texture) {
			IoSupplier<InputStream> inputStream = Objects.requireNonNull(rrp.getRootResource(texture));
			NativeImage image;
			try {
				image = NativeImage.read(inputStream.get());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			if (image.format() != NativeImage.Format.RGBA) {
				throwError(texture);
			}
			return image;
		}
		
		private static void throwError(String maskName) {
			throw new RuntimeException("World In a Jar's runtime resource generation has failed due to an incorrect image format! Please check ensure that `src/main/resources/rrp/" + maskName + "` is in RGBA format.");
		}
		
		public static NativeImage maskImage(NativeImage mask, NativeImage texture) {
			return maskImage(mask, texture, Constants.TEXTURE_MASK);
		}
		
		public static NativeImage maskImage(NativeImage mask, NativeImage texture, int maskColor) {
			int maskRed = FastColor.ABGR32.red(maskColor);
			int maskGreen = FastColor.ABGR32.green(maskColor);
			int maskBlue = FastColor.ABGR32.blue(maskColor);
			
			int[] pixels = texture.getPixelsRGBA();
			AtomicInteger index = new AtomicInteger();
			return mask.mappedCopy(pixel -> {
				index.getAndIncrement();
				
				int red = FastColor.ABGR32.red(pixel);
				int green = FastColor.ABGR32.green(pixel);
				int blue = FastColor.ABGR32.blue(pixel);
				int alpha = FastColor.ABGR32.alpha(pixel);
				
				if (red == maskRed && green == maskGreen && blue == maskBlue) {
					int color = pixels[index.get()];
					int newRed = FastColor.ABGR32.red(color);
					int newGreen = FastColor.ABGR32.green(color);
					int newBlue = FastColor.ABGR32.blue(color);
					return FastColor.ABGR32.color(alpha, newBlue, newGreen, newRed);
				} else {
					return pixel;
				}
			});
		}
	}
}
