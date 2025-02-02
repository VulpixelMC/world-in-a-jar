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
