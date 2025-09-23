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
package gay.sylv.wij.impl.item;

import gay.sylv.wij.api.block.BarkType;
import gay.sylv.wij.impl.item.tool.tier.BedrockTier;
import gay.sylv.wij.impl.util.Initializable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static gay.sylv.wij.impl.util.Constants.modId;

public final class Items implements Initializable {
	public static final Items INSTANCE = new Items();
	
	public static Map<BarkType, Item> BARK = new HashMap<>();
	public static Item BEDROCK_PICKAXE;
	public static Item BEDROCK_SHARD;
	
	private Items() {}
	
	@Override
	public void initialize() {
		BEDROCK_PICKAXE = register(
				"bedrock_pickaxe",
				new BedrockPickaxeItem(
						BedrockTier.INSTANCE,
						new Item.Properties()
								.rarity(Rarity.UNCOMMON)
								.attributes(PickaxeItem.createAttributes(BedrockTier.INSTANCE, 1.0F, -2.8F))
				)
		);
		
		BEDROCK_SHARD = register(
				"bedrock_shard",
				new Item(
						new Item.Properties()
								.fireResistant()
								.rarity(Rarity.UNCOMMON)
				)
		);
	}
	
	private static <I extends Item> I register(@NotNull String id, I item) {
		return Registry.register(BuiltInRegistries.ITEM, modId(id), item);
	}
}
