package gay.sylv.wij.impl.dimension;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import static gay.sylv.wij.impl.util.Constants.modId;

public final class Dimensions {
	public static final ResourceKey<Level> JAR = ResourceKey.create(Registries.DIMENSION, modId("jar"));
	
	public static class Types {
		private Types() {}
	}
	
	private Dimensions() {}
}
