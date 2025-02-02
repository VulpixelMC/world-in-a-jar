package gay.sylv.wij.impl.attachment;

import gay.sylv.wij.impl.network.Networking;
import gay.sylv.wij.impl.util.Initializable;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import java.util.function.Consumer;

import static gay.sylv.wij.impl.util.Constants.modId;

public final class Attachments implements Initializable {
	public static final Attachments INSTANCE = new Attachments();
	
	public static AttachmentType<Networking.JarLocation> RETURN_JAR_LOCATION;
	
	private Attachments() {}
	
	@Override
	public void initialize() {
		RETURN_JAR_LOCATION = register(
				"return_jar_location",
				builder -> builder
						.copyOnDeath()
						.persistent(Networking.JarLocation.CODEC)
						.syncWith(Networking.JarLocation.STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
		);
	}
	
	private static <A> AttachmentType<A> register(String name, Consumer<AttachmentRegistry.Builder<A>> consumer) {
		return AttachmentRegistry.create(modId(name), consumer);
	}
}
