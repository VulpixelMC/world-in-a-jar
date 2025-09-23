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
package gay.sylv.wij.impl.component;

import gay.sylv.wij.impl.util.Initializable;
import gay.sylv.wij.impl.util.jar.JarEntry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import static gay.sylv.wij.impl.util.Constants.modId;

public final class Components implements Initializable {
	public static final Components INSTANCE = new Components();
	
	public static DataComponentType<JarEntry> JAR_ENTRY_TYPE;
	
	private Components() {}
	
	@Override
	public void initialize() {
		JAR_ENTRY_TYPE = register(
				"jar_entry",
				DataComponentType.<JarEntry>builder()
						.persistent(JarEntry.CODEC)
						.build()
		);
	}
	
	private static <T> DataComponentType<T> register(String name, DataComponentType<T> type) {
		return Registry.register(
				BuiltInRegistries.DATA_COMPONENT_TYPE,
				modId(name),
				type
		);
	}
}
