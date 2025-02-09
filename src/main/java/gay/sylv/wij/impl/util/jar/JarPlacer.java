package gay.sylv.wij.impl.util.jar;

import gay.sylv.wij.impl.Main;
import gay.sylv.wij.impl.block.Blocks;
import gay.sylv.wij.impl.dimension.Dimensions;
import gay.sylv.wij.impl.util.CallerSensitive;
import gay.sylv.wij.impl.util.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static gay.sylv.wij.impl.util.Assertions.checkCaller;
import static gay.sylv.wij.impl.util.Constants.modId;

/**
 * Does the calculations for figuring out where to place Jars in the Jar Dimension.
 */
public final class JarPlacer {
	private static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_ID + "/JarPlacer");
	private static JarPlacer instance = null;
	
	private final MinecraftServer server;
	
	private JarPlacer(MinecraftServer server) {
		this.server = server;
	}
	
	private JarEntries getJarEntries() {
		return Objects.requireNonNull(server.getLevel(Dimensions.JAR), "Jar dimension must exist.")
				.getDataStorage()
				.computeIfAbsent(JarEntries.factory(), JarEntries.FILE_NAME);
	}
	
	@NotNull
	public static JarPlacer getInstance() {
		return Objects.requireNonNull(instance, "JarPlacer must be initialized.");
	}
	
	public static boolean isInitialized() {
		return instance != null;
	}
	
	@CallerSensitive
	public static void initialize(MinecraftServer server) throws IllegalAccessException {
		checkCaller(Main.class);
		instance = new JarPlacer(server);
	}
	
	@CallerSensitive
	public static void clear() throws IllegalAccessException {
		checkCaller(Main.class);
		instance = null;
	}
	
	public @NotNull JarEntry getFreeJarEntry() {
		return getJarEntries().getFreeJarEntry();
	}
	
	public boolean jarExists(JarEntry jarEntry, ServerLevel level) {
		return !level.getBlockState(jarEntry.chunkPos().getWorldPosition()).isAir();
	}
	
	public void placeJar(JarEntry jarEntry) {
		ServerLevel level = Objects.requireNonNull(server.getLevel(Dimensions.JAR), "Jar dimension must exist.");
		if (!jarExists(jarEntry, level)) {
			StructureTemplateManager structureManager = level.getStructureManager();
			for (int x = 0; x < 2; x++) {
				for (int y = 0; y < 2; y++) {
					for (int z = 0; z < 2; z++) {
						StructureTemplate structureTemplate = structureManager.get(modId("jar-" + x + "-" + y + "-" + z)).orElseThrow();
						if (!structureTemplate.placeInWorld(level, jarEntry.chunkPos().getWorldPosition().offset(-1, -1, -1).offset(x * 48, y * 48, z * 48), BlockPos.ZERO, new StructurePlaceSettings(), level.random, 1 | 2)) {
							LOGGER.error("Failed to place jar in jar dimension!");
						}
					}
				}
			}
			level.setBlock(jarEntry.chunkPos().getWorldPosition(), Blocks.WORLD_JAR.block().defaultBlockState(), 1 | 2);
		}
	}
}
