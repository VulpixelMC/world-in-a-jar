package gay.sylv.wij.api.event;

import dev.yumi.commons.event.EventManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

public final class Events {
	@ApiStatus.Internal
	public static final EventManager<String> EVENT_MANAGER = new EventManager<>("default", Function.identity());

	private Events() {}
}
