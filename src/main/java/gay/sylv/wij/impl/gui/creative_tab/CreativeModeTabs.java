package gay.sylv.wij.impl.gui.creative_tab;

import gay.sylv.wij.api.block.BarkType;
import gay.sylv.wij.impl.block.Blocks;
import gay.sylv.wij.impl.item.Items;
import gay.sylv.wij.impl.util.Constants;
import gay.sylv.wij.impl.util.Initializable;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Function;

import static gay.sylv.wij.impl.util.Constants.modId;

public class CreativeModeTabs implements Initializable {
	public static final CreativeModeTabs INSTANCE = new CreativeModeTabs();
	
	public static CreativeModeTab WORLD_IN_A_JAR;
	public static CreativeModeTab BARK;
	
	private CreativeModeTabs() {}
	
	@Override
	public void initialize() {
		WORLD_IN_A_JAR = register(
				Constants.COMPAT_MOD_ID,
				builder -> builder
						.title(Component.translatable("itemGroup." + Constants.MOD_ID))
						.icon(() -> Blocks.WORLD_JAR.item().getDefaultInstance())
						.displayItems((itemDisplayParameters, output) -> {
							output.accept(Blocks.WORLD_JAR.item());
							output.accept(Blocks.SUSSYSTONE.item());
							output.accept(Blocks.REINFORCED_ECHNOPLAST.item());
							output.accept(Blocks.CRACKED_BEDROCK.item());
							output.accept(Items.BEDROCK_SHARD);
							output.accept(Items.BEDROCK_PICKAXE);
							output.accept(Blocks.CORK_BLOCK.item());
						})
		);
		
		BARK = register(
				"bark",
				builder -> builder
						.title(Component.translatable("itemGroup." + Constants.MOD_ID + ".bark"))
						.icon(() -> Items.BARK.get(BarkType.OAK).getDefaultInstance())
						.displayItems(
								(itemDisplayParameters, output) ->
										Items.BARK.forEach((barkType, item) -> output.accept(item))
						)
		);
	}
	
	private static CreativeModeTab register(String id, Function<CreativeModeTab.Builder, CreativeModeTab.Builder> builder) {
		return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, modId(id), builder.apply(FabricItemGroup.builder()).build());
	}
}
