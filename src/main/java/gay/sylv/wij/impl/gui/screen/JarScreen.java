package gay.sylv.wij.impl.gui.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class JarScreen extends Screen {
	public Button enter;
	
	protected JarScreen() {
		super(Component.translatable("gui.worldinajar.world_jar.title"));
	}
	
	@Override
	protected void init() {
		enter = Button.builder(Component.translatable("gui.worldinajar.world_jar.enter"), button -> {
		
		}).build();
	}
}
