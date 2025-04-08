/**
 * World In a Jar
 * Copyright (C) 2024  VulpixelMC
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package gay.sylv.wij.impl.block.item;

import dev.gegy.roles.api.PlayerRolesApi;
import gay.sylv.wij.api.permissions.Permissions;
import gay.sylv.wij.impl.block.Blocks;
import gay.sylv.wij.impl.block.entity.WorldJarBlockEntity;
import gay.sylv.wij.impl.component.Components;
import gay.sylv.wij.impl.dimension.Dimensions;
import gay.sylv.wij.impl.util.Constants;
import gay.sylv.wij.impl.util.jar.JarEntry;
import gay.sylv.wij.impl.util.jar.JarPlacer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class WorldJarBlockItem extends BlockItem {
	public WorldJarBlockItem(Block block, Properties properties) {
		super(block, properties);
	}
	
	@Override
	protected boolean canPlace(BlockPlaceContext context, BlockState state) {
		ItemStack itemInHand = context.getItemInHand();
		boolean canCreate = context.getPlayer() != null && PlayerRolesApi.lookup().byPlayer(context.getPlayer()).overrides().test(Permissions.CREATE_JAR);
		if (!(itemInHand.has(Components.JAR_ENTRY_TYPE) && Objects.requireNonNull(itemInHand.get(Components.JAR_ENTRY_TYPE)).id() > -1) && !canCreate) {
			if (context.getPlayer() != null && !context.getLevel().isClientSide()) {
				context.getPlayer().sendSystemMessage(Component.literal("You cannot create new World Jars! Ask a team member to place a new one from the creative menu."));
			}
			return false;
		}
		
		// Prevent placement in jar dimension
		if (!context.getLevel().dimension().equals(Dimensions.JAR)) {
			return super.canPlace(context, state);
		} else {
			return false;
		}
	}
	
	@Override
	public BlockPlaceContext updatePlacementContext(BlockPlaceContext context) {
		if (context.getLevel().isClientSide()) {
			return context;
		}
		
		ItemStack itemInHand = context.getItemInHand();
		JarPlacer jarPlacer = JarPlacer.getInstance();
		JarEntry jarEntry;
		if (itemInHand.has(Components.JAR_ENTRY_TYPE) && Objects.requireNonNull(itemInHand.get(Components.JAR_ENTRY_TYPE)).id() > -1) {
			jarEntry = itemInHand.get(Components.JAR_ENTRY_TYPE);
			assert jarEntry != null;
		} else if (context.getPlayer() != null && PlayerRolesApi.lookup().byPlayer(context.getPlayer()).overrides().test(Permissions.CREATE_JAR)) {
			jarEntry = jarPlacer.getFreeJarEntry();
			itemInHand.set(Components.JAR_ENTRY_TYPE, jarEntry);
			jarPlacer.placeJar(jarEntry);
		} else {
			return context;
		}
		
		CompoundTag tag = new CompoundTag();
		CompoundTag modTag = new CompoundTag();
		modTag.putInt("id", jarEntry.id());
		if (context.getPlayer() != null && context.getPlayer().isCreative()) {
			WorldJarBlockEntity.CONTAINER_LOCK.addToTag(tag);
		}
		tag.put(Constants.COMPAT_MOD_ID, modTag);
		
		BlockItem.setBlockEntityData(itemInHand, Blocks.WORLD_JAR.type(), tag);
		return context;
	}
}
