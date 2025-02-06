package gay.sylv.wij.impl.duck;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.phys.Vec3;

public interface RendererWithExternalBlockStates {
	PalettedContainer<BlockState> worldinajar$getExternalBlockStateContainer();
	void worldinajar$setExternalBlockStateContainer(PalettedContainer<BlockState> externalBlockStateContainer);
	Vec3 worldinajar$getCenterOfJar();
	void worldinajar$setCenterOfJar(Vec3 centerOfJar);
}
