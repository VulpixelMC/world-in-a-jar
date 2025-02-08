package gay.sylv.wij.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NearestAttackableTargetGoal.class)
public class NearestAttackableTargetGoalMixin {
	@WrapWithCondition(
			method = "findTarget",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/Level;getNearestPlayer(Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;Lnet/minecraft/world/entity/LivingEntity;DDD)Lnet/minecraft/world/entity/player/Player;")
	)
	private boolean cancelFindingFakePlayers(NearestAttackableTargetGoal instance, LivingEntity value) {
		return !(value instanceof FakePlayer);
	}
}
