package dev.cursedmc.wij.impl.mixin;

import dev.cursedmc.wij.impl.duck.PlayerWithReturnDim;
import dev.cursedmc.wij.impl.duck.PlayerWithReturnPos;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerEntity.class, priority = 1100)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerWithReturnPos, PlayerWithReturnDim {
	private RegistryKey<World> worldinajar$returnDim;
	private Vec3d worldinajar$returnPos;
	
	@SuppressWarnings("ConstantConditions")
	private PlayerEntityMixin() {
		super(null, null);
	}
	
	@Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
	private void writeNbt(NbtCompound nbt, CallbackInfo ci) {
		nbt.putString("return_dim", worldinajar$returnDim.getValue().toString());
		final long[] positions = new long[]{Double.doubleToLongBits(worldinajar$returnPos.x), Double.doubleToLongBits(worldinajar$returnPos.y), Double.doubleToLongBits(worldinajar$returnPos.z)};
		nbt.putLongArray("return_pos", positions);
	}
	
	@Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
	private void readNbt(NbtCompound nbt, CallbackInfo ci) {
		worldinajar$returnDim = RegistryKey.of(Registry.WORLD_KEY, new Identifier(nbt.getString("return_dim")));
		long[] positions = nbt.getLongArray("return_pos");
		if (positions.length == 0) {
			positions = new long[]{Double.doubleToLongBits(0.0), Double.doubleToLongBits(0.0), Double.doubleToLongBits(0.0)};
		}
		worldinajar$returnPos = new Vec3d(Double.longBitsToDouble(positions[0]), Double.longBitsToDouble(positions[1]), Double.longBitsToDouble(positions[2]));
	}
	
	@Override
	public RegistryKey<World> worldinajar$getReturnDim() {
		return worldinajar$returnDim;
	}
	
	@Override
	public void worldinajar$setReturnDim(RegistryKey<World> returnDim) {
		this.worldinajar$returnDim = returnDim;
	}
	
	@Override
	public Vec3d worldinajar$getReturnPos() {
		return worldinajar$returnPos;
	}
	
	@Override
	public void worldinajar$setReturnPos(Vec3d returnPos) {
		this.worldinajar$returnPos = returnPos;
	}
}
