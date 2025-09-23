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
