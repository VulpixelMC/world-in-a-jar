package gay.sylv.wij.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@WrapOperation(
			method = "getPlayerInfo(Ljava/lang/String;)Lnet/minecraft/client/multiplayer/PlayerInfo;",
			at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/GameProfile;getName()Ljava/lang/String;")
	)
	private String ignoreFakePlayers(GameProfile instance, Operation<String> original) {
		if (instance.getId().version() != 3) {
			return original.call(instance);
		} else {
			return ""; // an impossible name, so it's never equal
		}
	}
}
