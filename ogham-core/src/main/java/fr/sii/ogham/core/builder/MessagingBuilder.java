package fr.sii.ogham.core.builder;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.mimetype.SimpleMimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.resolution.StandaloneResourceResolutionBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.sender.ConditionalSender;
import fr.sii.ogham.core.service.CatchAllMessagingService;
import fr.sii.ogham.core.service.EverySupportingMessagingService;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;

public class MessagingBuilder implements Builder<MessagingService> {
	private static final Logger LOG = LoggerFactory.getLogger(MessagingBuilder.class);
	private static final String BASE_PACKAGE = "fr.sii.ogham";
	
	private List<PrioritizedConfigurer> configurers;
	private EnvironmentBuilder<MessagingBuilder> environmentBuilder;
	private MimetypeDetectionBuilder<MessagingBuilder> mimetypeBuilder;
	private StandaloneResourceResolutionBuilder<MessagingBuilder> resourceBuilder;
	private EmailBuilder emailBuilder;
	private SmsBuilder smsBuilder;
	private boolean catchAll;

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

	public EnvironmentBuilder<MessagingBuilder> environment() {
		return environmentBuilder;
	}

	public StandaloneResourceResolutionBuilder<MessagingBuilder> resource() {
		return resourceBuilder;
	}

	public MimetypeDetectionBuilder<MessagingBuilder> mimetype() {
		return mimetypeBuilder;
	}

	public MessagingBuilder catchAll(boolean enable) {
		catchAll = enable;
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
		for(PrioritizedConfigurer configurer : configurers) {
			configurer.getConfigurer().configure(this);
		}
	}
	
	@Override
	public MessagingService build() throws BuildException {
		LOG.info("Using service that calls all registered senders");
		List<ConditionalSender> senders = buildSenders();
		LOG.debug("Registered senders: {}", senders);
		MessagingService service = new EverySupportingMessagingService(senders);
		if(catchAll) {
			service = new CatchAllMessagingService(service);
		}
		return service;
	}

	private List<ConditionalSender> buildSenders() {
		List<ConditionalSender> senders = new ArrayList<>();
		if(emailBuilder!=null) {
			senders.add(emailBuilder.build());
		}
		if(smsBuilder!=null) {
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
		if(autoconfigure) {
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
	 * @return
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
		if(autoconfigure) {
			builder.configure();
		}
		return builder;
	}
	
	private static void findAndRegister(MessagingBuilder builder, String builderName, String... basePackages) {
		Reflections reflections = new Reflections(basePackages, new SubTypesScanner());
		Set<Class<? extends MessagingConfigurer>> configurerClasses = reflections.getSubTypesOf(MessagingConfigurer.class);
		for(Class<? extends MessagingConfigurer> configurerClass : configurerClasses) {
			ConfigurerFor annotation = configurerClass.getAnnotation(ConfigurerFor.class);
			if(annotation!=null && asList(annotation.targetedBuilder()).contains(builderName)) {
				try {
					builder.register(configurerClass.newInstance(), annotation.priority());
				} catch (InstantiationException | IllegalAccessException e) {
					LOG.error("Failed to register custom auto-discovered configurer ("+configurerClass.getSimpleName()+") for standard messaging builder", e);
					throw new BuildException("Failed to register custom auto-discovered configurer ("+configurerClass.getSimpleName()+") for standard messaging builder", e);
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
	
	private static class AnnotatedConfigurer {
		private final ConfigurerFor annotation;
		private final Class<? extends MessagingConfigurer> configurerClass;
		public AnnotatedConfigurer(ConfigurerFor annotation, Class<? extends MessagingConfigurer> configurerClass) {
			super();
			this.annotation = annotation;
			this.configurerClass = configurerClass;
		}
		public ConfigurerFor getAnnotation() {
			return annotation;
		}
		public Class<? extends MessagingConfigurer> getConfigurerClass() {
			return configurerClass;
		}
	}

}
