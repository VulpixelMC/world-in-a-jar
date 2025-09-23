package gay.sylv.wij.api.fake;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class JarPlayer extends ServerPlayer {
	protected JarPlayer(
			MinecraftServer server, ServerLevel level, GameProfile gameProfile,
			ClientInformation clientInformation
	) {
		super(server, level, gameProfile, clientInformation);
	}

	public JarPlayer(ServerLevel level, GameProfile profile) {
		super(level.getServer(), level, profile, ClientInformation.createDefault());
	}
}
