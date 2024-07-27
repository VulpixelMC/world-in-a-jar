package gay.sylv.wij.impl.block.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreativePlacedBlockItem extends BlockItem {
	public CreativePlacedBlockItem(Block block, Properties properties) {
		super(block, properties);
	}
	
	@Override
	public @NotNull InteractionResult useOn(UseOnContext context) {
		if (!isCreative(context.getPlayer())) {
			return InteractionResult.FAIL;
		} else {
			return super.useOn(context);
		}
	}
	
	private static boolean isCreative(@Nullable Player player) {
		return player != null && player.isCreative();
	}
}
