package fr.sii.ogham.core.service;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.registry.CleanableRegistry;
import fr.sii.ogham.core.clean.Cleanable;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.clean.CleanException;
import fr.sii.ogham.core.exception.clean.CloseException;
import fr.sii.ogham.core.message.Message;

/**
 * Service that decorated the regular service to also provide the possibility to
 * clean resources.
 * 
 * <p>
 * Usually, the {@link MessagingService} in instantiated once per application
 * and is closed when the application exists. But for any reason, a developer
 * may want to create a new service several times. For example, he may create a
 * new service everytime a message has to be sent (even if it is less efficient,
 * we are not here to make judgments). In this case, the previously created
 * service may need to be destroyed and all associated resources should be
 * cleaned up.
 * 
 * This service provides three ways to do some cleanup: manually, using
 * auto-close behavior using try-with-resource or automatically before garbage
 * collection (<strong>/!\ Read information below to understand the
 * risks</strong>).
 * 
 * <p>
 * This service implements {@link Cleanable} interface to provide a way to
 * manually clean resources (by explicitly calling {@link #clean()} method).
 * 
 * <p>
 * This service also implements {@link Closeable} interface to automatically
 * cleanup resources using the try-with-resource capabilities:
 * 
 * <pre>
 * {@code
 *  try (CleanableMessagingService service = (CleanableMessagingService) builder.build()) {
 *  	service.send(new Email());
 *  } 
 * }
 * </pre>
 * 
 * In Spring environment, the {@link #close()} is automatically called when the
 * enclosing ApplicationContext is closed.
 * 
 * <p>
 * This service also overrides the {@code finalize()} method so that if the
 * service is garbage collected (no more references on the service instance),
 * all resources are automatically cleaned. The purpose is to provide an easy to
 * use service that tries to do some cleanup even if the developer forgets to do
 * so.
 * 
 * <strong>However, be careful as stated by Sonar: The {@code Object.finalize()}
 * method is called on an object by the garbage collector when it determines
 * that there are no more references to the object. But there is absolutely no
 * warranty that this method will be called AS SOON AS the last references to
 * the object are removed. It can be few microseconds to few minutes later. So
 * when system resources need to be disposed by an object, it's better to not
 * rely on this asynchronous mechanism to dispose them.</strong>
 * 
 * <p>
 * The cleanup of resources happens only once per {@link Cleanable}. It means
 * that if the developer explicitly calls {@link #clean()}, all resources are
 * cleaned up and then if service is garbage collected, the resources are not
 * cleaned a second time.
 * 
 * 
 * @author Aurélien Baudet
 * @see Cleanable
 * @see CleanableRegistry
 * @see AutoCloseable
 * @see "https://www.javaworld.com/article/2076697/object-finalization-and-cleanup.html"
 *
 */
public class CleanableMessagingService implements MessagingService, Cleanable, Closeable {
	private static final Logger LOG = LoggerFactory.getLogger(CleanableMessagingService.class);

	private final MessagingService delegate;
	private final Cleanable cleaner;

	/**
	 * Wraps the service and delegates cleaning operations to a dedicated
	 * implementation.
	 * 
	 * @param delegate
	 *            the wrapped service
	 * @param cleaner
	 *            the implementation that will really do some cleanup
	 */
	public CleanableMessagingService(MessagingService delegate, Cleanable cleaner) {
		super();
		this.delegate = delegate;
		this.cleaner = cleaner;
	}

	@Override
	public void send(Message message) throws MessagingException {
		delegate.send(message);
	}

	@Override
	public void clean() throws CleanException {
		LOG.info("Manually cleaning all resources...");
		cleaner.clean();
		LOG.info("Manually cleaned");
	}

	@Override
	public void close() throws IOException {
		try {
			LOG.info("Automatically closing all resources...");
			cleaner.clean();
			LOG.info("Automatically closed");
		} catch (CleanException e) {
			throw new CloseException("Failed to close resources", e);
		}
	}

	@Override
	@SuppressWarnings("squid:ObjectFinalizeOverridenCheck") // This is
															// intentional for
															// automatic cleanup
															// if developer
															// forgets it
	protected void finalize() throws Throwable {
		try {
			LOG.info("Automatically cleaning all resources...");
			cleaner.clean();
			LOG.info("Automatically cleaned");
		} finally {
			super.finalize();
		}
	}

}
