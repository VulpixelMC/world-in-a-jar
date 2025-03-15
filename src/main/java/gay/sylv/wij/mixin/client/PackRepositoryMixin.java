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
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PackRepository.class)
public class PackRepositoryMixin {
	@Inject(
			method = "openAllSelected",
			at = @At("RETURN"),
			cancellable = true
	)
	private void onOpenAllSelected(CallbackInfoReturnable<List<PackResources>> cir) {
		List<PackResources> resources = cir.getReturnValue();
		DynamicDataGenerator.TextureGenerator.generate(RuntimeResourcePack.getInstance(), resources);
		ArrayList<PackResources> newResources = new ArrayList<>(resources);
		newResources.add(RuntimeResourcePack.getInstance());
		cir.setReturnValue(List.copyOf(newResources));
	}
}
