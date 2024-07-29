package gay.sylv.wij.api.datagen;

import com.mojang.blaze3d.platform.NativeImage;
import gay.sylv.wij.impl.datagen.RuntimeResourcePackImpl;
import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Map;

public interface RuntimeResourcePack extends PackResources, ModResourcePack {
	static RuntimeResourcePack getInstance() {
		return RuntimeResourcePackImpl.INSTANCE;
	}
	
	Map<ResourceLocation, String> getModels();
	
	void addModel(ResourceLocation id, String modelJson);
	
	Map<ResourceLocation, NativeImage> getTextures();
	
	void addTexture(ResourceLocation id, NativeImage image);
	
	@Nullable
	IoSupplier<InputStream> getResource(ResourceLocation id);
}
