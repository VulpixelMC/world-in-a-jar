package gay.sylv.wij.impl.mixin.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * Allows access to {@link net.minecraft.client.render.chunk.ChunkBuilder.ChunkData#bufferState} and implements an invoker for {@link ChunkBuilder.ChunkData#getBlockEntities()}.
 * @author sylv
 */
@SuppressWarnings("unused")
@ClientOnly
@Mixin(ChunkBuilder.ChunkData.class)
public interface Accessor_ChunkData {
	@Accessor
	@Nullable
	BufferBuilder.SortState getBufferState();
	
	@Accessor
	void setBufferState(@Nullable BufferBuilder.SortState bufferState);
	
	@Accessor
	@NotNull List<BlockEntity> getBlockEntities();
}
