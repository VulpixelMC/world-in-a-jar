package gay.sylv.wij.impl.item;

import gay.sylv.wij.api.block.BarkType;
import net.minecraft.world.item.Item;

public class BarkItem extends Item {
	private final BarkType type;
	
	public BarkItem(Properties properties, BarkType type) {
		super(properties);
		this.type = type;
	}
	
	public BarkType getType() {
		return type;
	}
}
