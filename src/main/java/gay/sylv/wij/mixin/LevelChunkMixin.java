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
package gay.sylv.wij.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gay.sylv.wij.impl.Main;
import gay.sylv.wij.impl.block.Blocks;
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import gay.sylv.wij.impl.dimension.Dimensions;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin extends ChunkAccess {
	@Shadow
	@Final
	Level level;
	
	public LevelChunkMixin(
			ChunkPos chunkPos, UpgradeData upgradeData,
			LevelHeightAccessor levelHeightAccessor, Registry<Biome> biomeRegistry,
			long inhabitedTime, @Nullable LevelChunkSection[] sections,
			@Nullable BlendingData blendingData
	) {
		super(chunkPos, upgradeData, levelHeightAccessor, biomeRegistry, inhabitedTime, sections, blendingData);
	}
	
	@WrapOperation(
			method = "setBlockState",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;")
	)
	private BlockState onSetBlockState(LevelChunkSection instance, int x, int y, int z, BlockState state, Operation<BlockState> original) {
		if (this.level.dimension().equals(Dimensions.JAR) && state.is(Blocks.WORLD_JAR.block())) {
			return state;
		}
		return original.call(instance, x, y, z, state);
	}
	
	@Inject(
			method = "setBlockState",
			at = @At("RETURN")
	)
	private void updateWorldJar(BlockPos pos, BlockState state, boolean isMoving, CallbackInfoReturnable<BlockState> cir) {
		if (!Main.DISABLE_LAG && !this.level.isClientSide() && this.level.dimension().equals(Dimensions.JAR)) {
			// TODO: optimize/cache this somehow
			WorldJarBlockEntity.INSTANCES.forEachAuto(jar -> {
				if (jar.hasBlockPos(pos)) {
					SectionPos sectionPos = SectionPos.of(pos.subtract(jar.getInternalPos()));
					jar.updateSectionStates(Objects.requireNonNull(this.level.getServer()), sectionPos);
					for (ServerPlayer player : PlayerLookup.tracking(jar)) {
						jar.sendJarChunk(player, sectionPos);
					}
				}
			});
		}
	}
}
