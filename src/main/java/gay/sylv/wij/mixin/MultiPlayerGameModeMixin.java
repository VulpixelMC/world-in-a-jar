package gay.sylv.wij.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import gay.sylv.wij.impl.block.Blocks;
import gay.sylv.wij.impl.dimension.Dimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
	@Shadow
	@Final
	private Minecraft minecraft;
	
	@Inject(
			method = "destroyBlock",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;canAttackBlock(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)Z"),
			cancellable = true
	)
	private void dontDestroyWorldJar(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local BlockState state) {
		if (this.minecraft.level != null && this.minecraft.level.dimension().equals(Dimensions.JAR) && state.is(Blocks.WORLD_JAR.block())) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}
}
