package gay.sylv.wij.api.datagen;

import com.mojang.blaze3d.platform.NativeImage;
import gay.sylv.wij.impl.datagen.RuntimeResourcePackImpl;
import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;

import java.util.Map;

public interface RuntimeResourcePack extends PackResources, ModResourcePack {
	static RuntimeResourcePack getInstance() {
		return RuntimeResourcePackImpl.INSTANCE;
	}
	
	static Map<ResourceLocation, NativeImage> getTextures() {
		return RuntimeResourcePackImpl.getTextures();
	}
}
