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
