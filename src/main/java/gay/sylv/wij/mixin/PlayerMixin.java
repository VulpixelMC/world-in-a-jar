package gay.sylv.wij.mixin;

import gay.sylv.wij.impl.network.Networking;
import gay.sylv.wij.mixin.duck.PlayerWithReturnDim;
import gay.sylv.wij.mixin.duck.PlayerWithReturnPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(Player.class)
public class PlayerMixin implements PlayerWithReturnPos, PlayerWithReturnDim {
	@Unique
	private Map<Networking.JarLocation, ResourceKey<Level>> returnDimensions;
	/**
	 * 🥺
	 */
	@Unique
	private Map<Networking.JarLocation, Vec3> returnPossies;
	
	@Override
	public ResourceKey<Level> worldinajar$getReturnDimension(Networking.JarLocation jarLocation) {
		return returnDimensions.get(jarLocation);
	}
	
	@Override
	public void worldinajar$setReturnDimension(Networking.JarLocation jarLocation, ResourceKey<Level> dimension) {
		returnDimensions.put(jarLocation, dimension);
	}
	
	@Override
	public Vec3 worldinajar$getReturnPos(Networking.JarLocation jarLocation) {
		return returnPossies.get(jarLocation);
	}
	
	@Override
	public void worldinajar$setReturnPos(Networking.JarLocation jarLocation, Vec3 returnPos) {
		returnPossies.put(jarLocation, returnPos);
	}
}
