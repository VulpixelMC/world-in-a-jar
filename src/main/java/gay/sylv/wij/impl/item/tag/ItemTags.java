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
package gay.sylv.wij.impl.item.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static gay.sylv.wij.impl.util.Constants.modId;

public final class ItemTags {
	public static final TagKey<Item> CHIPS_UNBREAKABLE = create("chips_unbreakable");
	public static final TagKey<Item> DESTROYS_UNBREAKABLE = create("destroys_unbreakable");
	public static final TagKey<Item> CHIPS_OR_DESTROYS_UNBREAKABLE = create("chips_or_destroys_unbreakable");
	public static final TagKey<Item> BARK = create("bark");
	
	private static TagKey<Item> create(String id) {
		return TagKey.create(Registries.ITEM, modId(id));
	}
}
