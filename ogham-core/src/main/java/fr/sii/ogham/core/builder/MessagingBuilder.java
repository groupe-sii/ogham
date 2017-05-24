package fr.sii.ogham.core.builder;

import static java.util.Arrays.asList;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.activation.MimetypesFileTypeMap;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.mimetype.SimpleMimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.resolution.StandaloneResourceResolutionBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.sender.ConditionalSender;
import fr.sii.ogham.core.service.EverySupportingMessagingService;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.service.WrapExceptionMessagingService;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;

/**
 * TODO: indiquer ce qu'on peut faire avec plein d'exemples de code: - comment
 * configurer l'envoi d'email - comment configurer l'envoi de SMS
 * 
 * TODO: expliquer les standard, minimal, empty et le lien avec les configurers
 * 
 * TODO: expliquer que les comportements par défaut sont amenés par les
 * configurers
 * 
 * @author Aurélien Baudet
 *
 */
public class MessagingBuilder implements Builder<MessagingService> {
	private static final Logger LOG = LoggerFactory.getLogger(MessagingBuilder.class);
	private static final String BASE_PACKAGE = "fr.sii.ogham";

	private List<PrioritizedConfigurer> configurers;
	private EnvironmentBuilder<MessagingBuilder> environmentBuilder;
	private MimetypeDetectionBuilder<MessagingBuilder> mimetypeBuilder;
	private StandaloneResourceResolutionBuilder<MessagingBuilder> resourceBuilder;
	private EmailBuilder emailBuilder;
	private SmsBuilder smsBuilder;
	private boolean wrapUncaught;

	public MessagingBuilder() {
		super();
		configurers = new ArrayList<>();
		environmentBuilder = new SimpleEnvironmentBuilder<>(this);
		mimetypeBuilder = new SimpleMimetypeDetectionBuilder<>(this, environmentBuilder);
		resourceBuilder = new StandaloneResourceResolutionBuilder<>(this, environmentBuilder);
	}

	public MessagingBuilder register(MessagingConfigurer configurer, int priority) {
		configurers.add(new PrioritizedConfigurer(priority, configurer));
		return this;
	}

	/**
	 * Configures environment for the builder (and sub-builders if inherited).
	 * Environment consists of configuration properties/values that are used to
	 * configure the system (see {@link EnvironmentBuilder} for more
	 * information).
	 * 
	 * You can use system properties:
	 * 
	 * <pre>
	 * .environment()
	 *    .systemProperties();
	 * </pre>
	 * 
	 * Or, you can load properties from a file:
	 * 
	 * <pre>
	 * .environment()
	 *    .properties("/path/to/file.properties")
	 * </pre>
	 * 
	 * Or using directly a {@link Properties} object:
	 * 
	 * <pre>
	 * Properties myprops = new Properties();
	 * myprops.setProperty("foo", "bar");
	 * .environment()
	 *    .properties(myprops)
	 * </pre>
	 * 
	 * Or defining directly properties:
	 * 
	 * <pre>
	 * .environment()
	 *    .properties()
	 *       .set("foo", "bar")
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Every time you are configuring {@link #environment()}, you update the
	 * same instance.
	 * </p>
	 * 
	 * @return the builder to configure properties handling
	 */
	public EnvironmentBuilder<MessagingBuilder> environment() {
		return environmentBuilder;
	}

	public StandaloneResourceResolutionBuilder<MessagingBuilder> resource() {
		return resourceBuilder;
	}

	/**
	 * Builder that configures mimetype detection.
	 * 
	 * There exists several implementations to provide the mimetype:
	 * <ul>
	 * <li>Using Java {@link MimetypesFileTypeMap}</li>
	 * <li>Using Java 7 {@link Files#probeContentType(java.nio.file.Path)}</li>
	 * <li>Using <a href="http://tika.apache.org/">Apache Tika</a></li>
	 * <li>Using
	 * <a href="https://github.com/arimus/jmimemagic">JMimeMagic</a></li>
	 * </ul>
	 * 
	 * <p>
	 * Both implementations provided by Java are based on file extensions. This
	 * can't be used in most cases as we often handle {@link InputStream}s.
	 * </p>
	 * 
	 * <p>
	 * In previous version of Ogham, JMimeMagic was used and was working quite
	 * well. Unfortunately, the library is no more maintained.
	 * </p>
	 * 
	 * <p>
	 * You can configure how Tika will detect mimetype:
	 * 
	 * <pre>
	 * .mimetype()
	 *    .tika()
	 *       ...
	 * </pre>
	 * 
	 * <p>
	 * This builder allows to use several providers. It will chain them until
	 * one can find a valid mimetype. If none is found, you can explicitly
	 * provide the default one:
	 * 
	 * <pre>
	 * .mimetype()
	 *    .defaultMimetype("text/html")
	 * </pre>
	 * 
	 * <p>
	 * Every time you are configuring {@link #mimetype()}, the same instance is
	 * used.
	 * </p>
	 * 
	 * @return the builder to configure mimetype detection
	 */
	public MimetypeDetectionBuilder<MessagingBuilder> mimetype() {
		return mimetypeBuilder;
	}

	public MessagingBuilder wrapUncaught(boolean enable) {
		wrapUncaught = enable;
		return this;
	}

	public EmailBuilder email() {
		if (emailBuilder == null) {
			emailBuilder = new EmailBuilder(this, environmentBuilder);
		}
		return emailBuilder;
	}

	public SmsBuilder sms() {
		if (smsBuilder == null) {
			smsBuilder = new SmsBuilder(this, environmentBuilder);
		}
		return smsBuilder;
	}

	public void configure() {
		Collections.sort(configurers, new PriorityComparator());
		for (PrioritizedConfigurer configurer : configurers) {
			configurer.getConfigurer().configure(this);
		}
	}

	/**
	 * Builds the messaging service. The messaging service relies on the
	 * generated senders. Each sender is able to manage one or multiple
	 * messages. The default implementation of the messaging service is to ask
	 * each sender if it is able to handle the message and if it the case, then
	 * use this sender to really send the message. This implementation doesn't
	 * stop when the message is handled by a sender to possibly let another send
	 * the message through another channel.
	 * 
	 * <p>
	 * If a {@link RuntimeException} is thrown while trying to send a message,
	 * the service will catch it and wrap it into a {@link MessagingException}
	 * in order to indicate that the exception was caused while trying to send a
	 * message.
	 * </p>
	 * 
	 * @return the messaging service instance
	 * @throws BuildException
	 *             when service couldn't be instantiated and configured
	 */
	@Override
	public MessagingService build() throws BuildException {
		LOG.info("Using service that calls all registered senders");
		List<ConditionalSender> senders = buildSenders();
		LOG.debug("Registered senders: {}", senders);
		MessagingService service = new EverySupportingMessagingService(senders);
		if (wrapUncaught) {
			service = new WrapExceptionMessagingService(service);
		}
		return service;
	}

	private List<ConditionalSender> buildSenders() {
		List<ConditionalSender> senders = new ArrayList<>();
		if (emailBuilder != null) {
			senders.add(emailBuilder.build());
		}
		if (smsBuilder != null) {
			senders.add(smsBuilder.build());
		}
		return senders;
	}

	public static MessagingBuilder empty() {
		return new MessagingBuilder();
	}

	public static MessagingBuilder standard() {
		return standard(BASE_PACKAGE);
	}

	public static MessagingBuilder standard(String... basePackages) {
		return standard(true, basePackages);
	}

	public static MessagingBuilder standard(boolean autoconfigure) {
		return standard(autoconfigure, BASE_PACKAGE);
	}

	public static MessagingBuilder standard(boolean autoconfigure, String... basePackages) {
		MessagingBuilder builder = new MessagingBuilder();
		findAndRegister(builder, "standard", basePackages);
		if (autoconfigure) {
			builder.configure();
		}
		return builder;
	}

	public static MessagingBuilder minimal() {
		return minimal(BASE_PACKAGE);
	}

	/**
	 * Common but no implementation
	 * 
	 * @param basePackages
	 *            the base packages where to find {@link Configurer}
	 *            implementations
	 * @return the minimal builder
	 */
	public static MessagingBuilder minimal(String... basePackages) {
		return minimal(true, basePackages);
	}

	public static MessagingBuilder minimal(boolean autoconfigure) {
		return minimal(autoconfigure, BASE_PACKAGE);
	}

	public static MessagingBuilder minimal(boolean autoconfigure, String... basePackages) {
		MessagingBuilder builder = new MessagingBuilder();
		findAndRegister(builder, "minimal", basePackages);
		if (autoconfigure) {
			builder.configure();
		}
		return builder;
	}

	public static void findAndRegister(MessagingBuilder builder, String builderName, String... basePackages) {
		Reflections reflections = new Reflections(basePackages, new SubTypesScanner());
		Set<Class<? extends MessagingConfigurer>> configurerClasses = reflections.getSubTypesOf(MessagingConfigurer.class);
		for (Class<? extends MessagingConfigurer> configurerClass : configurerClasses) {
			ConfigurerFor annotation = configurerClass.getAnnotation(ConfigurerFor.class);
			if (annotation != null && asList(annotation.targetedBuilder()).contains(builderName)) {
				try {
					builder.register(configurerClass.newInstance(), annotation.priority());
				} catch (InstantiationException | IllegalAccessException e) {
					LOG.error("Failed to register custom auto-discovered configurer (" + configurerClass.getSimpleName() + ") for standard messaging builder", e);
					throw new BuildException("Failed to register custom auto-discovered configurer (" + configurerClass.getSimpleName() + ") for standard messaging builder", e);
				}
			}
		}
	}

	private static class PriorityComparator implements Comparator<PrioritizedConfigurer> {
		@Override
		public int compare(PrioritizedConfigurer o1, PrioritizedConfigurer o2) {
			return -Integer.compare(o1.getPriority(), o2.getPriority());
		}
	}

	private static class PrioritizedConfigurer {
		private final int priority;
		private final MessagingConfigurer configurer;

		public PrioritizedConfigurer(int priority, MessagingConfigurer configurer) {
			super();
			this.priority = priority;
			this.configurer = configurer;
		}

		public int getPriority() {
			return priority;
		}

		public MessagingConfigurer getConfigurer() {
			return configurer;
		}
	}

}
