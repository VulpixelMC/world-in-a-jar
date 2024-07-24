package gay.sylv.wij.impl.block.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import static gay.sylv.wij.impl.util.Constants.modId;

public final class BlockTags {
	public static final TagKey<Block> INCORRECT_FOR_BEDROCK_TOOL = create("incorrect_for_bedrock_tool");
	
	private static TagKey<Block> create(String id) {
		return TagKey.create(Registries.BLOCK, modId(id));
	}
}
