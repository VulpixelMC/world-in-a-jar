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
