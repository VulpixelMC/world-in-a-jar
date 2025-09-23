package gay.sylv.wij.impl.platform;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Collection;

public interface PlatformHelper {
	/**
	 * Gets the current platform
	 *
	 * @return An enum value representing the current platform.
	 */
	Platform getPlatform();

	/**
	 * Checks if a mod with the given id is loaded.
	 *
	 * @param modId The mod to check if it is loaded.
	 * @return True if the mod is loaded, false otherwise.
	 */
	boolean isModLoaded(String modId);

	/**
	 * Check if the game is currently in a development environment.
	 *
	 * @return True if in a development environment, false otherwise.
	 */
	boolean isDevelopmentEnvironment();

	/**
	 * Creates a fake player entity. This is a platform-dependent operation.
	 * @return The fake player.
	 */
	ServerPlayer createFakePlayer(ServerLevel level, GameProfile profile);

	void sendClientbound(ServerPlayer player, CustomPacketPayload payload);

	void sendServerbound(CustomPacketPayload payload);

	Collection<ServerPlayer> lookupPlayersTracking(BlockEntity blockEntity);

	Side getSide();
}
