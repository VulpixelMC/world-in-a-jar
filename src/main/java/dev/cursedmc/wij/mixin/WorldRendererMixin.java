package dev.cursedmc.wij.mixin;

import dev.cursedmc.wij.duck.WorldRendererDuck;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements WorldRendererDuck {
	@Shadow
	private ClientWorld world;
	
	private WorldRendererMixin() {}
	
	@Override
	public void worldinajar$renderLayer(RenderLayer renderLayer, MatrixStack matrices, double sortX, double sortY, double sortZ, Matrix4f projectionMatrix) {
		renderLayer(renderLayer, matrices, sortX, sortY, sortZ, projectionMatrix);
	}
	
	@Override
	public void worldinajar$setupTerrain(Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator) {
		setupTerrain(camera, frustum, hasForcedFrustum, spectator);
	}
	
	@Override
	public void worldinajar$findChunksToRebuild(Camera camera) {
		findChunksToRebuild(camera);
	}
	
	@Override
	public ClientWorld worldinajar$getWorld() {
		return world;
	}
	
	@Shadow
	protected abstract void renderLayer(RenderLayer renderLayer, MatrixStack matrices, double sortX, double sortY, double sortZ, Matrix4f projectionMatrix);
	
	@Shadow protected abstract void setupTerrain(Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator);
	
	@Shadow protected abstract void findChunksToRebuild(Camera camera);
}
