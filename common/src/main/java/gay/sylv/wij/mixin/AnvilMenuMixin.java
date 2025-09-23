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
package gay.sylv.wij.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import gay.sylv.wij.api.block.BarkType;
import gay.sylv.wij.impl.item.Items;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Locale;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
	@Shadow
	@Final
	private DataSlot cost;
	@Unique
	private static final FoodProperties WELL_DONE = new FoodProperties.Builder()
			.alwaysEdible()
			.nutrition(2)
			.saturationModifier(0.5f)
			.effect(new MobEffectInstance(MobEffects.HARM, 1, 0), 1.0f)
			.build();
	@Unique
	private static final ItemLore WELL_DONE_LORE = new ItemLore(List.of(Component.translatable("worldinajar.lore.bark.well_done")));
	@Unique
	private static final Component WELL_DONE_NAME = Component.translatable("item.worldinajar.bark.minecraft.spruce.well_done");
	@Unique
	private boolean wellDone = false;
	
	public AnvilMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
		super(type, containerId, playerInventory, access);
	}
	
	@SuppressWarnings("MixinExtrasOperationParameters")
	@WrapOperation(
			method = "createResult",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;set(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;",
					ordinal = 0
			)
	)
	private <T> @Nullable T wellDone(ItemStack instance, DataComponentType<? super T> component, @Nullable T value, Operation<T> original) {
		wellDone = value instanceof Component text && instance.is(Items.BARK.get(BarkType.SPRUCE)) && text.getString().toLowerCase(Locale.ROOT).equals("well done");
		if (wellDone) {
			instance.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
			instance.set(DataComponents.ITEM_NAME, WELL_DONE_NAME);
			instance.set(DataComponents.LORE, WELL_DONE_LORE);
			instance.set(DataComponents.FOOD, WELL_DONE);
		}
		return original.call(instance, component, value);
	}
	
	@SuppressWarnings("UnresolvedLocalCapture")
	@Inject(
			method = "createResult",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V",
					shift = At.Shift.BEFORE
			)
	)
	private void beforeSetItem(CallbackInfo ci, @Local(ordinal = 1) ItemStack itemStack2) {
		if (wellDone) {
			itemStack2.remove(DataComponents.CUSTOM_NAME);
			cost.set(1);
		}
	}
}
