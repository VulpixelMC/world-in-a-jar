package gay.sylv.wij.mixin.breakingbedrock;

import net.anawesomguy.breakingbedrock.BedrockBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
		value = BedrockBlock.class,
		remap = false
)
@Pseudo
public abstract class Mixin_BedrockBlock extends BlockBehaviour {
	public Mixin_BedrockBlock(Properties properties) {
		super(properties);
	}
	
	@Inject(
			method = "getDestroyProgress",
			at = @At("HEAD"),
			cancellable = true
	)
	private void overrideDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
		cir.setReturnValue(super.getDestroyProgress(state, player, level, pos));
	}
}
