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
