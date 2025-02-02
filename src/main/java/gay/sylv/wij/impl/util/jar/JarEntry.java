package gay.sylv.wij.impl.util.jar;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.ChunkPos;

public record JarEntry(int id) {
	public static final Codec<JarEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT
					.fieldOf("id")
					.forGetter(JarEntry::id)
	).apply(instance, JarEntry::new));
	
	public ChunkPos chunkPos() {
		return new ChunkPos(((id % 64) * 64) - 2048, ((id / 64) * 64) - 2048);
	}
}
