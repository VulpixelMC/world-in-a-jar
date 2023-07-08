/**
 * World In a Jar
 * Copyright (C) 2023  Sylv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package gay.sylv.wij.impl.mixin;

import gay.sylv.wij.impl.duck.PlayerWithReturnDim;
import gay.sylv.wij.impl.duck.PlayerWithReturnPos;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author sylv
 */
@Mixin(value = PlayerEntity.class, priority = 1100)
public abstract class Mixin_PlayerEntity extends LivingEntity implements PlayerWithReturnPos, PlayerWithReturnDim {
	private RegistryKey<World> worldinajar$returnDim;
	private Vec3d worldinajar$returnPos;
	
	@SuppressWarnings("DataFlowIssue")
	private Mixin_PlayerEntity() {
		super(null, null);
	}
	
	@Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
	private void writeNbt(NbtCompound nbt, CallbackInfo ci) {
		if (worldinajar$returnDim != null) nbt.putString("return_dim", worldinajar$returnDim.getValue().toString());
		if (worldinajar$returnPos != null) {
			final long[] positions = new long[]{Double.doubleToLongBits(worldinajar$returnPos.x), Double.doubleToLongBits(worldinajar$returnPos.y), Double.doubleToLongBits(worldinajar$returnPos.z)};
			nbt.putLongArray("return_pos", positions);
		}
	}
	
	@Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
	private void readNbt(NbtCompound nbt, CallbackInfo ci) {
		if (nbt.contains("return_dim") && nbt.contains("return_pos")) {
			worldinajar$returnDim = RegistryKey.of(RegistryKeys.WORLD, new Identifier(nbt.getString("return_dim")));
			long[] positions = nbt.getLongArray("return_pos");
			if (positions.length == 0) {
				positions = new long[]{Double.doubleToLongBits(0.0), Double.doubleToLongBits(0.0), Double.doubleToLongBits(0.0)};
			}
			worldinajar$returnPos = new Vec3d(Double.longBitsToDouble(positions[0]), Double.longBitsToDouble(positions[1]), Double.longBitsToDouble(positions[2]));
		}
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
