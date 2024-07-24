package gay.sylv.wij.impl.item;

import gay.sylv.wij.impl.item.tool.tier.BedrockTier;
import gay.sylv.wij.impl.util.Initializable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.NotNull;

import static gay.sylv.wij.impl.util.Constants.modId;

public final class Items implements Initializable {
	public static final Items INSTANCE = new Items();
	
	public static final Item BEDROCK_PICKAXE = register(
			"bedrock_pickaxe",
			new PickaxeItem(
					BedrockTier.INSTANCE,
					new Item.Properties()
							.rarity(Rarity.UNCOMMON)
							.attributes(PickaxeItem.createAttributes(BedrockTier.INSTANCE, 1.0F, -2.8F))
			)
	);
	
	public static final Item BEDROCK_SHARD = register(
			"bedrock_shard",
			new Item(
					new Item.Properties()
							.fireResistant()
							.rarity(Rarity.UNCOMMON)
			)
	);
	
	private Items() {}
	
	private static <I extends Item> I register(@NotNull String id, I item) {
		return Registry.register(BuiltInRegistries.ITEM, modId(id), item);
	}
}
