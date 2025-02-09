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
package gay.sylv.wij.impl.util.jar;

import gay.sylv.wij.impl.util.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class JarEntries extends SavedData {
	public static final String FILE_NAME = Constants.COMPAT_MOD_ID + "_jar_data";
	
	private int jarCount;
	
	public JarEntries() {
		this.jarCount = 0;
	}
	
	public static SavedData.Factory<JarEntries> factory() {
		return new SavedData.Factory<>(JarEntries::new, JarEntries::load, null);
	}
	
	public static @NotNull JarEntries load(CompoundTag tag, HolderLookup.Provider registries) {
		int jarCount = tag.getInt("jar_count");
		JarEntries data = new JarEntries();
		data.jarCount = jarCount;
		return data;
	}
	
	@Override
	public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
		tag.putInt("jar_count", jarCount);
		return tag;
	}
	
	public @NotNull JarEntry getFreeJarEntry() {
		jarCount++;
		this.setDirty();
		return new JarEntry(jarCount - 1);
	}
}
