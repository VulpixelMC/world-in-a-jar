package gay.sylv.wij.impl.duck;

import gay.sylv.wij.impl.network.Networking;

import java.util.Optional;

/**
 * Holds the location of the jar which the player entered. A mixin implements this interface on all {@link net.minecraft.world.entity.player.Player}s.
 */
public interface PlayerWithEnteredJar {
	Optional<Networking.JarLocation> worldinajar$getJarLocation();
	void worldinajar$setJarLocation(Networking.JarLocation location);
}
