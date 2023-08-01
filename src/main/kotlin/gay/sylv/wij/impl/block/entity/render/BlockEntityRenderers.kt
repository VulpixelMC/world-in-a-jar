/**
 * World In a Jar
 * Copyright (C) 2023  Sylv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package gay.sylv.wij.impl.block.entity.render

import gay.sylv.wij.impl.block.entity.BlockEntityTypes
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import org.quiltmc.loader.api.minecraft.ClientOnly

@ClientOnly
object BlockEntityRenderers : gay.sylv.wij.api.Initializable {
	init {
		BlockEntityRendererFactories.register(
			BlockEntityTypes.WORLD_JAR,
			::WorldJarBlockEntityRenderer,
		)
	}
}
