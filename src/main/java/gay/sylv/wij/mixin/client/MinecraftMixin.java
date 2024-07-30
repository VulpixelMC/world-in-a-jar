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

import gay.sylv.wij.api.datagen.RuntimeResourcePack;
import gay.sylv.wij.impl.datagen.DynamicDataGenerator;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Shadow
	public abstract CompletableFuture<Void> reloadResourcePacks();
	
	private MinecraftMixin() {}
	
	@Inject(
			method = "onResourceLoadFinished",
			at = @At("TAIL")
	)
	private void onFinishLoad(CallbackInfo ci) {
		DynamicDataGenerator.TextureGenerator.generate(RuntimeResourcePack.getInstance(), Minecraft.getInstance().getResourceManager());
	}
	
	@Inject(
			method = "onGameLoadFinished",
			at = @At("TAIL")
	)
	private void onGameLoadFinished(CallbackInfo ci) {
		// FIXME: make it so the item models load properly and so we don't need this weird hack.
		this.reloadResourcePacks();
	}
}
