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
import gay.sylv.wij.impl.WorldInAJar;
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
import java.nio.charset.StandardCharsets;
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
		if (WorldInAJar.isClient()) {
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
			String model;
			if (type == BarkType.SPRUCE) {
				model = String.format(
						"""
						{
							"parent": "item/generated",
							"textures": {
								"layer0": "%1$s"
							},
							"overrides": [
						 		{
						 			"predicate": {
						 				"custom_model_data": 1
						 			},
						 			"model": "minecraft:item/cooked_beef"
						 		}
						 	]
						}
						""", type.getResourceIdentifier("item"));
			} else {
				model = generatedModel(type.getResourceIdentifier("item"));
			}
			RuntimeResourcePack.getInstance().addModel(type.getResourceIdentifier("item"), model);
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
					.map(type -> {
						NativeImage log;
						try {
							log = NativeImage.read(manager.getResourceOrThrow(type.toFilePath()).open());
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						NativeImage darkenedLog = multiplyBrightness(log, 0.85f);
						
						if (type.isAnimated()) {
							try (var inputStream = manager.getResourceOrThrow(type.toFilePath().withSuffix(".mcmeta")).open()) {
								rrp.addItemMcmeta(type.getIdentifier(), new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
						
						NativeImage expandedMask = expand(barkMask, log.getWidth(), log.getHeight());
						int shadowMask = FastColor.ABGR32.color(255, 0, 0, 255);
						maskImage(expandedMask, log);
						maskImage(log, darkenedLog, shadowMask);
						
						return new Pair<>(darkenedLog, type);
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
		
		public static NativeImage expand(NativeImage mask, int width, int height) {
			if (width == mask.getWidth() && height == mask.getHeight()) {
				return mask;
			}
			
			NativeImage newMask = new NativeImage(mask.format(), width, height, false);
			int[] pixels = mask.getPixelsRGBA();
			AtomicInteger index = new AtomicInteger();
			newMask.applyToAllPixels(pixel -> {
				int i = index.getAndIncrement();
				if (i + 1 > mask.getWidth() * mask.getHeight()) {
					index.set(1);
					i = 0;
				}
				
				return pixels[i];
			});
			
			return newMask;
		}
		
		public static void replaceColor(NativeImage texture, int maskColor, int color) {
			texture.applyToAllPixels(pixel -> {
				if (pixel == maskColor) {
					return color;
				} else {
					return pixel;
				}
			});
		}
		
		public static NativeImage multiplyBrightness(NativeImage texture, float multiply) {
			return texture.mappedCopy(pixel -> {
				int red = FastColor.ABGR32.red(pixel);
				int green = FastColor.ABGR32.green(pixel);
				int blue = FastColor.ABGR32.blue(pixel);
				int alpha = FastColor.ABGR32.alpha(pixel);
				return FastColor.ABGR32.color(alpha, (int) (blue * multiply), (int) (green * multiply), (int) (red * multiply));
			});
		}
		
		public static void maskImage(NativeImage mask, NativeImage texture) {
			maskImage(mask, texture, Constants.TEXTURE_MASK);
		}
		
		public static void maskImage(NativeImage mask, NativeImage texture, int maskColor) {
			int maskRed = FastColor.ABGR32.red(maskColor);
			int maskGreen = FastColor.ABGR32.green(maskColor);
			int maskBlue = FastColor.ABGR32.blue(maskColor);
			
			int[] pixels = mask.getPixelsRGBA();
			AtomicInteger index = new AtomicInteger();
			texture.applyToAllPixels(pixel -> {
				int i = index.getAndIncrement();
				
				int newRed = FastColor.ABGR32.red(pixel);
				int newGreen = FastColor.ABGR32.green(pixel);
				int newBlue = FastColor.ABGR32.blue(pixel);
				
				int color = pixels[i];
				int red = FastColor.ABGR32.red(color);
				int green = FastColor.ABGR32.green(color);
				int blue = FastColor.ABGR32.blue(color);
				int alpha = FastColor.ABGR32.alpha(color);
				
				if (red == maskRed && green == maskGreen && blue == maskBlue) {
					return FastColor.ABGR32.color(alpha, newBlue, newGreen, newRed);
				} else {
					return color;
				}
			});
		}
	}
}
