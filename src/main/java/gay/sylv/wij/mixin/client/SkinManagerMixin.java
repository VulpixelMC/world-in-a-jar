package gay.sylv.wij.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Objects;

@Mixin(SkinManager.class)
abstract class SkinManagerMixin {
	@ModifyVariable(
			method = "getOrLoad",
			at = @At("HEAD"),
			argsOnly = true,
			index = 1
	)
	private GameProfile getOrLoadTinySkin(GameProfile value) {
		if (value.getId().version() == 3) {
			Minecraft client = Minecraft.getInstance();
			if (client.getCurrentServer() == null && !client.isLocalServer()) return value;
			if (client.isLocalServer()) {
				Player player = Objects.requireNonNull(client.getSingleplayerServer()).getPlayerList().getPlayerByName(value.getName());
				if (player == null) return value;
				
				return player.getGameProfile();
			}
			
			ClientPacketListener connection = client.getConnection();
			if (connection == null) return value;
			
			PlayerInfo playerInfo = connection.getPlayerInfo(value.getName());
			if (playerInfo == null) return value;
			return playerInfo.getProfile();
		}
		
		return value;
	}
}
