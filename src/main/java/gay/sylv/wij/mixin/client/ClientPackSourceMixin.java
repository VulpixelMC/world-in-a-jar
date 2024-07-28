package gay.sylv.wij.mixin.client;

import gay.sylv.wij.impl.datagen.RuntimeResourcePack;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.level.validation.DirectoryValidator;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ClientPackSource.class)
public abstract class ClientPackSourceMixin extends BuiltInPackSource {
	private ClientPackSourceMixin(PackType packType, VanillaPackResources vanillaPack, ResourceLocation packDir, DirectoryValidator validator) {
		super(packType, vanillaPack, packDir, validator);
	}
	
	@Mixin(value = BuiltInPackSource.class, priority = 999)
	public static abstract class Parent {
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
			return Pack.readMetaAndCreate(RuntimeResourcePack.INSTANCE.location(), RuntimeResourcePack.FIXED_RESOURCES, PackType.CLIENT_RESOURCES, RuntimeResourcePack.BUILT_IN_SELECTION_CONFIG);
		}
	}
}
