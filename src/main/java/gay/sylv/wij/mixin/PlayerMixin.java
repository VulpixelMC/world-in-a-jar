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
package gay.sylv.wij.mixin;

import gay.sylv.wij.impl.attachment.Attachments;
import gay.sylv.wij.impl.duck.PlayerWithEnteredJar;
import gay.sylv.wij.impl.network.Networking;
import gay.sylv.wij.impl.duck.PlayerWithReturn;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Objects;
import java.util.Optional;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerWithReturn, PlayerWithEnteredJar {
	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}
	
	@Override
	public @NotNull ResourceKey<Level> worldinajar$getReturnDimension() {
		if (hasAttached(Attachments.RETURN_JAR_LOCATION)) {
			return getAttachedOrThrow(Attachments.RETURN_JAR_LOCATION).dimension();
		} else {
			return Level.OVERWORLD;
		}
	}
	
	@Override
	public @NotNull Vec3 worldinajar$getReturnPos() {
		if (hasAttached(Attachments.RETURN_JAR_LOCATION)) {
			return getAttachedOrThrow(Attachments.RETURN_JAR_LOCATION).blockPos().getCenter();
		} else if (!level().isClientSide()) {
			return Objects.requireNonNull(level().getServer()).overworld().getSharedSpawnPos().getCenter();
		} else {
			return new Vec3(0.0d, 0.0d, 0.0d);
		}
	}
	
	@Override
	public void worldinajar$setReturnLocation(Networking.JarLocation returnLocation) {
		setAttached(Attachments.RETURN_JAR_LOCATION, returnLocation);
	}
	
	@Override
	public void worldinajar$RemoveReturnLocation() {
		removeAttached(Attachments.RETURN_JAR_LOCATION);
	}
	
	@Override
	public Optional<Networking.JarLocation> worldinajar$getJarLocation() {
		return Optional.ofNullable(getAttached(Attachments.RETURN_JAR_LOCATION));
	}
	
	@Override
	public void worldinajar$setJarLocation(Networking.JarLocation location) {
		setAttached(Attachments.RETURN_JAR_LOCATION, location);
	}
}
