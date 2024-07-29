package gay.sylv.wij.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Iterator;

@Mixin(FontManager.class)
public abstract class FontManagerMixin {
	private FontManagerMixin() {}
	
	@WrapOperation(
			method = "loadResourceStack",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Iterator;hasNext()Z"
			)
	)
	private static boolean hijackHasNext(Iterator<Resource> instance, Operation<Boolean> original, @Local(argsOnly = true) ResourceLocation fontId) {
		// weird issue where it turns models/ into s/ and thinks it's a font
		if (fontId.getPath().startsWith("s/")) {
			return false;
		} else {
			return original.call(instance);
		}
	}
}
