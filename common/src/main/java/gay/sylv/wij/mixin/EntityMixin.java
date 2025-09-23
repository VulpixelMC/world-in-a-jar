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

import gay.sylv.wij.api.fake.JarPlayer;
import gay.sylv.wij.impl.WorldInAJar;
import gay.sylv.wij.impl.block.Blocks;
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import gay.sylv.wij.impl.dimension.Dimensions;
import gay.sylv.wij.impl.duck.PlayerWithEnteredJar;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(Entity.class)
abstract class EntityMixin {
	@Shadow
	public abstract Level level();

	@Inject(
			method = "setLevel",
			at = @At("TAIL")
	)
	private void afterSetLevel(Level level, CallbackInfo ci) {
		//noinspection ConstantValue
		if (!level.isClientSide() && level.dimension().equals(Dimensions.JAR) && (Entity) (Object) this instanceof ServerPlayer player && !(player instanceof JarPlayer)) {
			WorldInAJar.createFakePlayer((ServerLevel) level, player);
		}
	}

	@Inject(
			method = "setPos(DDD)V",
			at = @At("TAIL")
	)
	private void afterSetPos(double oldX, double oldY, double oldZ, CallbackInfo ci) {
		if ((Entity) (Object) this instanceof ServerPlayer player && player.level().dimension().equals(Dimensions.JAR)) {
			((PlayerWithEnteredJar) this).worldinajar$getJarLocation().ifPresent(jarLocation -> {
				ServerLevel jarLevel = Objects.requireNonNull(player.server.getLevel(jarLocation.dimension())).getLevel();
				Optional<WorldJarBlockEntity> optionalJar = jarLevel.getBlockEntity(jarLocation.blockPos(), Blocks.WORLD_JAR.type());
				if (optionalJar.isEmpty()) return;
				WorldJarBlockEntity jar = optionalJar.get();
				BlockPos topCorner = jar.getInternalPos();
				double x = oldX - topCorner.getX();
				double y = oldY - topCorner.getY();
				double z = oldZ - topCorner.getZ();
				x /= jar.getScale();
				y /= jar.getScale();
				z /= jar.getScale();
				x += jar.getBlockPos().getX();
				y += jar.getBlockPos().getY();
				z += jar.getBlockPos().getZ();
				Optional<JarPlayer> optionalFakePlayer = WorldInAJar.getFakePlayer(jar, player);
				if (optionalFakePlayer.isEmpty()) return;
				JarPlayer fakePlayer = optionalFakePlayer.get();
				fakePlayer.setPos(x, y, z);
			});
		}
	}

	@Inject(
			method = "setRot",
			at = @At("TAIL")
	)
	private void afterSetRot(float yRot, float xRot, CallbackInfo ci) {
		worldinajar$setRot(fakePlayer -> {
			fakePlayer.setYRot(yRot);
			fakePlayer.setXRot(xRot);
		});
	}

	@Inject(
			method = "setXRot",
			at = @At("TAIL")
	)
	private void afterSetXRot(float xRot, CallbackInfo ci) {
		worldinajar$setRot(fakePlayer -> fakePlayer.setXRot(xRot));
	}

	@Inject(
			method = "setYRot",
			at = @At("TAIL")
	)
	private void afterSetYRot(float yRot, CallbackInfo ci) {
		worldinajar$setRot(fakePlayer -> fakePlayer.setYRot(yRot));
	}

	@Unique
	private void worldinajar$setRot(Consumer<JarPlayer> setRot) {
		if ((Entity) (Object) this instanceof ServerPlayer player && player.level().dimension().equals(Dimensions.JAR)) {
			((PlayerWithEnteredJar) this).worldinajar$getJarLocation().ifPresent(jarLocation -> {
				ServerLevel jarLevel = Objects.requireNonNull(player.server.getLevel(jarLocation.dimension())).getLevel();
				Optional<WorldJarBlockEntity> optionalJar = jarLevel.getBlockEntity(jarLocation.blockPos(), Blocks.WORLD_JAR.type());
				if (optionalJar.isEmpty()) return;
				WorldJarBlockEntity jar = optionalJar.get();
				Optional<JarPlayer> optionalFakePlayer = WorldInAJar.getFakePlayer(jar, player);
				if (optionalFakePlayer.isEmpty()) return;
				JarPlayer fakePlayer = optionalFakePlayer.get();
				setRot.accept(fakePlayer);
			});
		}
	}
}
