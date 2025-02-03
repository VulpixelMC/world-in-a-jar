package gay.sylv.wij.mixin;

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
	
	@Inject(
			method = "setBlockState",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;getX()I")
	)
	private void updateWorldJar(BlockPos pos, BlockState state, boolean isMoving, CallbackInfoReturnable<BlockState> cir) {
		if (!this.level.isClientSide() && this.level.dimension().equals(Dimensions.JAR)) {
			// TODO: optimize/cache this somehow
			WorldJarBlockEntity.INSTANCES.forEachAuto(jar -> {
				if (jar.hasBlockPos(pos)) {
					SectionPos sectionPos = SectionPos.of(pos.subtract(jar.getInternalPos()));
					for (ServerPlayer player : PlayerLookup.tracking(jar)) {
						jar.updateSectionStates(player.server, sectionPos);
						jar.sendJarChunk(player, sectionPos);
					}
				}
			});
		}
	}
}
