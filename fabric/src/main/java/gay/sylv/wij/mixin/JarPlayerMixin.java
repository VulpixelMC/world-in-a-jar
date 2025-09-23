package gay.sylv.wij.mixin;

import com.mojang.authlib.GameProfile;
import gay.sylv.wij.api.fake.JarPlayer;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(JarPlayer.class)
abstract class JarPlayerMixin extends FakePlayer {
	protected JarPlayerMixin(ServerLevel world, GameProfile profile) {
		super(world, profile);
	}
}
