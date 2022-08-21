package dev.cursedmc.wij.duck;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Matrix4f;

public interface WorldRendererDuck {
	void worldinajar$renderLayer(RenderLayer renderLayer, MatrixStack matrices, double sortX, double sortY, double sortZ, Matrix4f projectionMatrix);
	void worldinajar$setupTerrain(Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator);
	void worldinajar$findChunksToRebuild(Camera camera);
	ClientWorld worldinajar$getWorld();
}
