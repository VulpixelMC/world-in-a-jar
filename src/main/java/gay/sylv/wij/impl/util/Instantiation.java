package gay.sylv.wij.impl.util;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;

import static gay.sylv.wij.impl.util.Constants.modId;

/**
 * Utilities for instantiation.
 */
public final class Instantiation {
	private Instantiation() {}
	
	public static <T extends Record & CustomPacketPayload> CustomPacketPayload.Type<T> packetType(String id) {
		return new CustomPacketPayload.Type<>(modId(id));
	}
	
	public static PalettedContainer<BlockState> blockStatePalettedContainer() {
		return new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
	}
}
