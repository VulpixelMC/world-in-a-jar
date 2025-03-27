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
package gay.sylv.wij.impl.client;

import gay.sylv.wij.impl.WIJMain;
import gay.sylv.wij.impl.client.render.JarInternalsRenderer;
import gay.sylv.wij.impl.client.network.ClientPackets;
import gay.sylv.wij.impl.util.Constants;
import gay.sylv.wij.impl.util.Initializable;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import org.slf4j.Logger;

public final class WIJClient implements Initializable {
	private static final Logger LOGGER = WIJMain.getLogger(WIJClient.class);
	
	@Override
	public void initialize() {
		LOGGER.info("Initializing {}/Client", Constants.MOD_NAME);
		
		ClientPackets.INSTANCE.initialize();
		
		CoreShaderRegistrationCallback.EVENT.register(context -> JarInternalsRenderer.INSTANCE.initialize());
		
		LOGGER.info("{}/Client loaded", Constants.MOD_NAME);
	}
}
