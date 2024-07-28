package gay.sylv.wij.mixin.client;

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
		packConsumer.accept(createBuiltinPack());
	}
	
	@Unique
	@Nullable
	private Pack createBuiltinPack() {
		return Pack.readMetaAndCreate(RuntimeResourcePackImpl.INSTANCE.location(), RuntimeResourcePackImpl.FIXED_RESOURCES, PackType.CLIENT_RESOURCES, RuntimeResourcePackImpl.BUILT_IN_SELECTION_CONFIG);
	}
}
