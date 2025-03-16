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
			String playerName = value.getName();
			if (client.isLocalServer()) {
				Player player = Objects.requireNonNull(client.getSingleplayerServer()).getPlayerList().getPlayerByName(playerName);
				if (player == null) return value;
				
				return player.getGameProfile();
			}
			
			ClientPacketListener connection = client.getConnection();
			if (connection == null) return value;
			
			PlayerInfo playerInfo = connection.getPlayerInfo(playerName);
			if (playerInfo == null) return value;
			return playerInfo.getProfile();
		}
		
		return value;
	}
}
