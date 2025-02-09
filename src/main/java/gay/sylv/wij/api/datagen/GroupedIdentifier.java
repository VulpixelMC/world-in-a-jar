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
package gay.sylv.wij.api.datagen;

import gay.sylv.wij.impl.util.Constants;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Describes a runtime-generated resource identifier.
 * <p>
 * For example, the {@code worldinajar:bark} equivalent of {@code minecraft:oak_log} would be {@code worldinajar:bark/minecraft/oak}.
 */
public interface GroupedIdentifier {
	/**
	 * @return the mod ID of the {@link GroupedIdentifier}. For example, {@code worldinajar} in {@code worldinajar:bark/minecraft/oak}.
	 */
	default String getModId() {
		return Constants.COMPAT_MOD_ID;
	}
	
	/**
	 * @return the group of the {@link GroupedIdentifier}. For example, {@code bark} in {@code worldinajar:bark/minecraft/oak}.
	 */
	String getGroup();
	
	/**
	 * @return the namespace of the {@link GroupedIdentifier}. For example, {@code minecraft} in {@code worldinajar:bark/minecraft/oak}.
	 */
	String getNamespace();
	
	/**
	 * @return the path of the {@link GroupedIdentifier}. For example, {@code oak} in {@code worldinajar:bark/minecraft/oak}.
	 */
	String getPath();
	
	/**
	 * Gets the proper {@link ResourceLocation} that this identifier belongs to.
	 * @param modId The ID of the mod that this identifier belongs to.
	 * @param resourceType The type of resource. For example, {@code item} in {@code worldinajar:item/bark/minecraft/oak}.
	 * @return the {@link ResourceLocation}.
	 */
	default ResourceLocation getResourceIdentifier(String modId, @Nullable String resourceType) {
		String prefix = resourceType != null ? resourceType + "/" : "";
		return ResourceLocation.fromNamespaceAndPath(modId, String.format("%s%s/%s/%s", prefix, getGroup(), getNamespace(), getPath()));
	}
	
	/**
	 * Gets the proper {@link ResourceLocation} that this identifier belongs to.
	 * @param resourceType The type of resource. For example, {@code item} in {@code worldinajar:item/bark/minecraft/oak}.
	 * @return the {@link ResourceLocation}.
	 */
	default ResourceLocation getResourceIdentifier(@Nullable String resourceType) {
		return getResourceIdentifier(getModId(), resourceType);
	}
	
	/**
	 * Gets the proper {@link ResourceLocation} that this identifier belongs to.
	 * @param modId The ID of the mod that this identifier belongs to.
	 * @return the {@link ResourceLocation}.
	 */
	default ResourceLocation getIdentifier(String modId) {
		return getResourceIdentifier(modId, null);
	}
	
	/**
	 * Gets the proper {@link ResourceLocation} that this identifier belongs to.
	 * @return the {@link ResourceLocation}.
	 */
	default ResourceLocation getIdentifier() {
		return getIdentifier(getModId());
	}
}
