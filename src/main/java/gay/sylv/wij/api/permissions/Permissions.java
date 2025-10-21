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
package gay.sylv.wij.api.permissions;

import com.mojang.serialization.Codec;
import dev.gegy.roles.api.PlayerRolesApi;
import dev.gegy.roles.api.RoleLookup;
import dev.gegy.roles.api.RoleReader;
import dev.gegy.roles.api.override.RoleOverrideReader;
import dev.gegy.roles.api.override.RoleOverrideType;
import gay.sylv.wij.impl.util.Initializable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static gay.sylv.wij.impl.util.Constants.modId;

public final class Permissions implements Initializable {
	@ApiStatus.Internal
	public static final Permissions INSTANCE = new Permissions();
	
	public static Permission<Boolean> CREATE_JAR;
	
	private Permissions() {}
	
	@ApiStatus.Internal
	@Override
	public void initialize() {
		CREATE_JAR = register(
				"create_jar",
				Codec.BOOL,
				true,
				t -> t
		);
	}
	
	private static <T> Permission<T> register(String id, Codec<T> codec, T defaultValue, Predicate<T> condition) {
		return new Permission<>(RoleOverrideType.register(modId(id), codec), defaultValue, condition);
	}
	
	public static final class Permission<T> {
		private final RoleOverrideType<T> overrideType;
		private final T defaultValue;
		private final Predicate<T> condition;
		
		private Permission(RoleOverrideType<T> overrideType, T defaultValue, Predicate<T> condition) {
			this.overrideType = overrideType;
			this.defaultValue = defaultValue;
			this.condition = condition;
		}
		
		public boolean test(Player player) {
			if (player == null) return false;
			RoleOverrideReader overrides = PlayerRolesApi.lookup().byPlayer(player).overrides();
			T t = overrides.select(this.overrideType);
			if (t == null) {
				return this.condition.test(this.defaultValue);
			} else {
				return this.condition.test(t);
			}
		}
	}
}
