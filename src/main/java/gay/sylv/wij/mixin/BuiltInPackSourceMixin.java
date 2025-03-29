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

import gay.sylv.wij.impl.datagen.RuntimeResourcePackImpl;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(value = BuiltInPackSource.class, priority = 999)
public abstract class BuiltInPackSourceMixin {
	@Inject(
			method = "listBundledPacks",
			at = @At("RETURN")
	)
	private void addBuiltinResourcePacks(Consumer<Pack> packConsumer, CallbackInfo ci) {
		packConsumer.accept(createBuiltinPack(PackType.CLIENT_RESOURCES));
		packConsumer.accept(createBuiltinPack(PackType.SERVER_DATA));
	}
	
	@Unique
	@Nullable
	private Pack createBuiltinPack(PackType type) {
		return Pack.readMetaAndCreate(RuntimeResourcePackImpl.INSTANCE.location(), RuntimeResourcePackImpl.FIXED_RESOURCES, type, RuntimeResourcePackImpl.BUILT_IN_SELECTION_CONFIG);
	}
}
