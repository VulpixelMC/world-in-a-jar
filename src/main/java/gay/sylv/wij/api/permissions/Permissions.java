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
import dev.gegy.roles.api.override.RoleOverrideType;
import gay.sylv.wij.impl.util.Initializable;
import org.jetbrains.annotations.ApiStatus;

import static gay.sylv.wij.impl.util.Constants.modId;

public final class Permissions implements Initializable {
	@ApiStatus.Internal
	public static final Permissions INSTANCE = new Permissions();
	
	public static RoleOverrideType<Boolean> CREATE_JAR;
	
	private Permissions() {}
	
	@ApiStatus.Internal
	@Override
	public void initialize() {
		CREATE_JAR = register(
				"create_jar",
				Codec.BOOL
		);
	}
	
	private static <T> RoleOverrideType<T> register(String id, Codec<T> codec) {
		return RoleOverrideType.register(modId(id), codec);
	}
}
