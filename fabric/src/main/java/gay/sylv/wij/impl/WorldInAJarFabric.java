package gay.sylv.wij.impl;

import gay.sylv.wij.impl.attachment.FabricAttachments;
import gay.sylv.wij.impl.block.Blocks;
import gay.sylv.wij.impl.block.tag.BlockTags;
import gay.sylv.wij.impl.dimension.Dimensions;
import gay.sylv.wij.api.entity.event.ServerPlayerEventsExtra;
import gay.sylv.wij.impl.item.BedrockPickaxeItem;
import gay.sylv.wij.impl.item.tag.ItemTags;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class WorldInAJarFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		// Enabled for debug purposes.
		SharedConstants.IS_RUNNING_IN_IDE = WorldInAJar.getHelper().isDevelopmentEnvironment();
		WorldInAJar.init();

		FabricAttachments.INSTANCE.initialize();

		PlayerBlockBreakEvents.BEFORE.register((Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) -> {
			// Prevent breaking/destruction in jar dimension
			return !(level.dimension().equals(Dimensions.JAR) && state.is(Blocks.WORLD_JAR.block()));
		});

		ServerLifecycleEvents.SERVER_STARTED.register(WorldInAJar::onServerStart);
		ServerLifecycleEvents.SERVER_STOPPED.register(WorldInAJar::onServerStop);

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			if (!(handler.getPlayer() instanceof FakePlayer) && handler.getPlayer().level().dimension().equals(Dimensions.JAR)) {
				WorldInAJar.removeFakePlayer(handler.getPlayer().serverLevel(), handler.getPlayer());
			}
		});

		PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
			if (player.getMainHandItem().is(ItemTags.CHIPS_OR_DESTROYS_UNBREAKABLE) && BedrockPickaxeItem.isBedrockMineable(level, state, player)) {
				BedrockPickaxeItem.dropBedrockShard(player.getMainHandItem(), level, state, pos, player);
			}

			return !state.is(BlockTags.UNBREAKABLE) || player.getAbilities().instabuild;
		});
	}
}
