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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import gay.sylv.wij.impl.dimension.Dimensions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Level.class)
public class LevelMixin {
	@WrapOperation(
			method = "<init>",
			at = @At(value = "NEW", target = "()Lnet/minecraft/world/level/border/WorldBorder;")
	)
	private WorldBorder cancelJarWorldBorder(
			Operation<WorldBorder> original,
			@Local(argsOnly = true) ResourceKey<Level> dimension
	) {
		if (!dimension.equals(Dimensions.JAR)) {
			return original.call();
		} else {
			return new WorldBorder() {
				@Override
				public void setSize(double size) {
				}
				
				@Override
				public void lerpSizeBetween(double oldSize, double newSize, long time) {
				}
				
				@Override
				public void applySettings(Settings serializer) {
				}
			};
		}
	}
}
