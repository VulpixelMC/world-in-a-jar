/**
 * World In a Jar
 * Copyright (C) 2023  Sylv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package gay.sylv.wij.impl.mixin;

import gay.sylv.wij.impl.block.Blocks;
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import gay.sylv.wij.impl.dimension.DimensionTypes;
import gay.sylv.wij.impl.network.s2c.JarWorldBlockUpdateS2CPacket;
import gay.sylv.wij.impl.network.s2c.S2CPackets;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author sylv
 */
@Mixin(WorldChunk.class)
public abstract class Mixin_WorldChunk extends Chunk {
	@Shadow
	@Final
	World world;
	
	@Shadow
	public abstract BlockState getBlockState(BlockPos pos);
	
	@SuppressWarnings("DataFlowIssue")
	private Mixin_WorldChunk() {
		super(null, null, null, null, 0, null, null);
	}
	
	/**
	 * Send a {@link JarWorldBlockUpdateS2CPacket} to clients tracking any {@link WorldJarBlockEntity}s.
	 * @author sylv
	 * TODO: use a cache for jar updates
	 */
	@Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getX()I"), cancellable = true)
	private void updateWorldJar(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir) {
		if (!world.isClient) {
			if (world.getRegistryKey() == DimensionTypes.WORLD_JAR_WORLD) {
				if (state.isOf(Blocks.INSTANCE.getWORLD_JAR())) {
					cir.setReturnValue(this.getBlockState(pos));
					cir.cancel();
				}
				for (WorldJarBlockEntity entity : WorldJarBlockEntity.Companion.getINSTANCES()) {
					if (entity.hasBlockPos(pos)) {
						for (ServerPlayerEntity player : PlayerLookup.tracking(entity)) {
							PacketByteBuf buf = PacketByteBufs.create();
							JarWorldBlockUpdateS2CPacket packet = new JarWorldBlockUpdateS2CPacket(pos, state, entity.getPos());
							packet.write(buf);
							ServerPlayNetworking.send(player, S2CPackets.INSTANCE.getJAR_WORLD_BLOCK_UPDATE$worldinajar(), buf);
						}
					}
				}
			}
		}
	}
}
