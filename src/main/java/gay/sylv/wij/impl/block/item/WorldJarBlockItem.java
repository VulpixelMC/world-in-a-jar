package gay.sylv.wij.impl.block.item;

import gay.sylv.wij.impl.block.Blocks;
import gay.sylv.wij.impl.component.Components;
import gay.sylv.wij.impl.dimension.Dimensions;
import gay.sylv.wij.impl.util.Constants;
import gay.sylv.wij.impl.util.jar.JarEntry;
import gay.sylv.wij.impl.util.jar.JarPlacer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class WorldJarBlockItem extends BlockItem {
	public WorldJarBlockItem(Block block, Properties properties) {
		super(block, properties);
	}
	
	@Override
	protected boolean canPlace(BlockPlaceContext context, BlockState state) {
		// Prevent placement in jar dimension
		if (!context.getLevel().dimension().equals(Dimensions.JAR)) {
			return super.canPlace(context, state);
		} else {
			return false;
		}
	}
	
	@Override
	public BlockPlaceContext updatePlacementContext(BlockPlaceContext context) {
		if (context.getLevel().isClientSide()) {
			return context;
		}
		
		ItemStack itemInHand = context.getItemInHand();
		JarPlacer jarPlacer = JarPlacer.getInstance();
		JarEntry jarEntry;
		if (itemInHand.has(Components.JAR_ENTRY_TYPE) && Objects.requireNonNull(itemInHand.get(Components.JAR_ENTRY_TYPE)).id() > -1) {
			jarEntry = itemInHand.get(Components.JAR_ENTRY_TYPE);
			assert jarEntry != null;
		} else {
			jarEntry = jarPlacer.getFreeJarEntry();
			itemInHand.set(Components.JAR_ENTRY_TYPE, jarEntry);
			jarPlacer.placeJar(jarEntry);
		}
		
		CompoundTag tag = new CompoundTag();
		CompoundTag modTag = new CompoundTag();
		modTag.putInt("id", jarEntry.id());
		tag.put(Constants.COMPAT_MOD_ID, modTag);
		
		BlockItem.setBlockEntityData(itemInHand, Blocks.WORLD_JAR.type(), tag);
		return context;
	}
}
