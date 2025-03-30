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
package gay.sylv.wij.impl.block.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import static gay.sylv.wij.impl.util.Constants.modId;

public final class BlockTags {
	public static final TagKey<Block> INCORRECT_FOR_BEDROCK_TOOL = create("incorrect_for_bedrock_tool");
	public static final TagKey<Block> NEEDS_BEDROCK_TOOL = create("needs_bedrock_tool");
	public static final TagKey<Block> UNBREAKABLE = create("unbreakable");
	public static final TagKey<Block> NAUGHTY_BLOCKS = create("naughty_blocks");
	
	private static TagKey<Block> create(String id) {
		return TagKey.create(Registries.BLOCK, modId(id));
	}
}
