package gay.sylv.wij.mixin;

import gay.sylv.wij.api.entity.event.ServerPlayerEventsExtra;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
abstract class PlayerListMixin {
	@Inject(
			method = "placeNewPlayer",
			at = @At("TAIL")
	)
	private void afterSpawn(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
		ServerPlayerEventsExtra.AFTER_SPAWN.invoker().afterSpawn(connection, player, cookie);
	}
}
