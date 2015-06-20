package fr.sii.ogham.email.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.ContentTranslatorBuilder;
import fr.sii.ogham.core.builder.MessageFillerBuilder;
import fr.sii.ogham.core.builder.MessagingSenderBuilder;
import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.OrCondition;
import fr.sii.ogham.core.condition.RequiredClassCondition;
import fr.sii.ogham.core.condition.RequiredPropertyCondition;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.filler.MessageFiller;
import fr.sii.ogham.core.filler.SubjectFiller;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.sender.ConditionalSender;
import fr.sii.ogham.core.sender.ContentTranslatorSender;
import fr.sii.ogham.core.sender.FillerSender;
import fr.sii.ogham.core.sender.MultiImplementationSender;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.core.translator.content.ContentTranslator;
import fr.sii.ogham.core.translator.resource.AttachmentResourceTranslator;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.email.EmailConstants;
import fr.sii.ogham.email.EmailConstants.SendGridConstants;
import fr.sii.ogham.email.sender.AttachmentResourceTranslatorSender;
import fr.sii.ogham.email.sender.EmailSender;

/**
 * <p>
 * Specialized builder for email sender.
 * </p>
 * There exists several implementations to send an email:
 * <ul>
 * <li>Using pure Java mail API</li>
 * <li>Using <a href="https://commons.apache.org/proper/commons-email/">Apache
 * Commons Email</a></li>
 * <li>Using any other library</li>
 * <li>Through <a href="https://sendgrid.com/">SendGrid</a></li>
 * <li>Through a WebService</li>
 * <li>...</li>
 * </ul>
 * <p>
 * This builder provides a {@link MultiImplementationSender}. The aim of the
 * {@link MultiImplementationSender} is to choose the best implementation for
 * sending the email according to the runtime environment (detection of
 * libraries in the classpath, availability of a particular property, ...).
 * </p>
 * <p>
 * This builder lets you the possibility to register any new implementation. It
 * allows you to enable or not templating support and automatic filling of
 * message values (like sender address for example).
 * </p>
 * 
 * @author Aurélien Baudet
 * @see EmailSender
 * @see JavaMailBuilder
 */
public class EmailBuilder implements MessagingSenderBuilder<ConditionalSender> {
	private static final Logger LOG = LoggerFactory.getLogger(EmailBuilder.class);

	/**
	 * The sender instance constructed by this builder
	 */
	private ConditionalSender sender;

	/**
	 * The specialized {@link MultiImplementationSender}. It is useful to
	 * register new implementations
	 */
	private EmailSender emailSender;

	/**
	 * The builder for message filler used to add values to the message
	 */
	private MessageFillerBuilder messageFillerBuilder;

	/**
	 * The builder for the translator that will update the content of the
	 * message
	 */
	private ContentTranslatorBuilder contentTranslatorBuilder;

	/**
	 * The builder for the resource translator to handle email attachments
	 */
	private AttachmentResourceTranslatorBuilder resourceTranslatorBuilder;

	/**
	 * Map that stores email implementations indexed by associated condition
	 */
	private Map<Condition<Message>, Builder<? extends MessageSender>> implementations;

	public EmailBuilder() {
		super();
		sender = emailSender = new EmailSender();
		implementations = new HashMap<>();
	}

	@Override
	public ConditionalSender build() throws BuildException {
		for (Entry<Condition<Message>, Builder<? extends MessageSender>> impl : implementations.entrySet()) {
			MessageSender s = impl.getValue().build();
			LOG.debug("Implementation {} registered", s);
			emailSender.addImplementation(impl.getKey(), s);
		}
		if (messageFillerBuilder != null) {
			MessageFiller messageFiller = messageFillerBuilder.build();
			LOG.debug("Automatic filling of message enabled {}", messageFiller);
			sender = new FillerSender(messageFiller, sender);
		}
		if (resourceTranslatorBuilder != null) {
			AttachmentResourceTranslator resourceTranslator = resourceTranslatorBuilder.build();
			LOG.debug("Resource translation enabled {}", resourceTranslator);
			sender = new AttachmentResourceTranslatorSender(resourceTranslator, sender);
		}
		if (contentTranslatorBuilder != null) {
			ContentTranslator contentTranslator = contentTranslatorBuilder.build();
			LOG.debug("Content translation enabled {}", contentTranslator);
			sender = new ContentTranslatorSender(contentTranslator, sender);
		}
		return sender;
	}

	/**
	 * Tells the builder to use all default behaviors and values:
	 * <ul>
	 * <li>Uses Java mail default behaviors and values</li>
	 * <li>Registers Java mail API implementation</li>
	 * <li>Enables automatic filling of message based on configuration
	 * properties</li>
	 * <li>Enables templating support</li>
	 * <li>Enables attachment features (see {@link #withAttachmentFeatures()})</li>
	 * </ul>
	 * <p>
	 * Configuration values come from system properties.
	 * </p>
	 * 
	 * @return this instance for fluent use
	 */
	public EmailBuilder useDefaults() {
		return useDefaults(BuilderUtils.getDefaultProperties());
	}

	/**
	 * Tells the builder to use all default behaviors and values:
	 * <ul>
	 * <li>Uses Java mail default behaviors and values</li>
	 * <li>Registers Java mail API implementation</li>
	 * <li>Enables automatic filling of message based on configuration
	 * properties</li>
	 * <li>Enables templating support</li>
	 * <li>Enables attachment features (see {@link #withAttachmentFeatures()})</li>
	 * </ul>
	 * <p>
	 * Configuration values come from provided properties.
	 * </p>
	 * 
	 * @param properties
	 *            the properties to use instead of default ones
	 * @return this instance for fluent use
	 */
	public EmailBuilder useDefaults(Properties properties) {
		registerDefaultImplementations(properties);
		withAutoFilling(properties);
		withTemplate(properties);
		withAttachmentFeatures();
		return this;
	}

	/**
	 * Register a new implementation for sending email. The implementation is
	 * associated to a condition. If the condition evaluation returns true at
	 * runtime then it means that the implementation can be used. If several
	 * implementations are available, only the first implementation is really
	 * invoked.
	 * 
	 * @param condition
	 *            the condition that indicates at runtime if the implementation
	 *            can be used or not
	 * @param implementation
	 *            the implementation to register
	 * @return this instance for fluent use
	 */
	public EmailBuilder registerImplementation(Condition<Message> condition, MessageSender implementation) {
		emailSender.addImplementation(condition, implementation);
		return this;
	}

	/**
	 * Register a new implementation for sending email. The implementation is
	 * associated to a condition. If the condition evaluation returns true at
	 * runtime then it means that the implementation can be used. If several
	 * implementations are available, only the first implementation is really
	 * invoked.
	 * 
	 * @param condition
	 *            the condition that indicates at runtime if the implementation
	 *            can be used or not
	 * @param builder
	 *            the builder for the implementation to register
	 * @return this instance for fluent use
	 */
	public EmailBuilder registerImplementation(Condition<Message> condition, Builder<? extends MessageSender> builder) {
		implementations.put(condition, builder);
		return this;
	}

	/**
	 * Register all default implementations:
	 * <ul>
	 * <li>Java mail API implementation</li>
	 * </ul>
	 * <p>
	 * Configuration values come from system properties.
	 * </p>
	 * <p>
	 * Automatically called by {@link #useDefaults()} and
	 * {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @return this instance for fluent use
	 */
	public EmailBuilder registerDefaultImplementations() {
		return registerDefaultImplementations(BuilderUtils.getDefaultProperties());
	}

	/**
	 * Register all default implementations:
	 * <ul>
	 * <li>Java mail API implementation</li>
	 * </ul>
	 * <p>
	 * Configuration values come from provided properties.
	 * </p>
	 * <p>
	 * Automatically called by {@link #useDefaults()} and
	 * {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @param properties
	 *            the properties to use
	 * @return this instance for fluent use
	 */
	public EmailBuilder registerDefaultImplementations(Properties properties) {
		withJavaMail(properties);
		withSendGrid(properties);
		return this;
	}

	/**
	 * Enable Java Mail API implementation. This implementation is used only if
	 * the associated condition indicates that Java Mail API can be used. The
	 * condition checks if:
	 * <ul>
	 * <li>The property <code>mail.smtp.host</code> is set</li>
	 * <li>The class <code>javax.mail.Transport</code> (Java Mail API) is
	 * available in the classpath</li>
	 * <li>The class <code>com.sun.mail.smtp.SMTPTransport</code> (Java Mail
	 * implementation) is available in the classpath</li>
	 * </ul>
	 * The registration can silently fail if the javax.mail jar is not in the
	 * classpath. In this case, the Java Mail API is not registered at all.
	 * 
	 * @param properties
	 *            the properties used to check if property exists
	 * @return this builder instance for fluent use
	 */
	public EmailBuilder withJavaMail(Properties properties) {
		// Java Mail API can be used only if the property "mail.smtp.host" is
		// provided and also if the class "javax.mail.Transport" is defined in
		// the classpath. The try/catch clause is mandatory in order to prevent
		// failure when javax.mail jar is not in the classpath
		try {
			registerImplementation(new AndCondition<>(new RequiredPropertyCondition<Message>("mail.smtp.host", properties), new RequiredClassCondition<Message>("javax.mail.Transport"),
					new RequiredClassCondition<Message>("com.sun.mail.smtp.SMTPTransport")), new JavaMailBuilder().useDefaults(properties));
		} catch (Throwable e) {
			LOG.debug("Can't register Java Mail implementation", e);
		}
		return this;
	}

	/**
	 * Enable SendGrid implementation. This implementation is used only if the
	 * associated condition indicates that Java Mail API can be used. The
	 * condition checks if:
	 * <ul>
	 * <li>The property <code>sendgrid.api.key</code> is set</li>
	 * <li>The property <code>sendgrid.username</code> and
	 * <code>sendgrid.password</code> is set</li>
	 * <li>The class <code>com.sendgrid.SendGrid</code> is available in the
	 * classpath</li>
	 * </ul>
	 * The registration can silently fail if the javax.mail jar is not in the
	 * classpath. In this case, the SendGrid is not registered at all.
	 * 
	 * @param properties
	 *            the properties used to check if property exists
	 * @return this builder instance for fluent use
	 */
	public EmailBuilder withSendGrid(Properties properties) {
		// SendGrid can be used only if the property "sendgrid.api.key" is
		// provided and also if the class "com.sendgrid.SendGrid" is defined in
		// the classpath. The try/catch clause is mandatory in order to prevent
		// failure when sendgrid jar is not in the classpath
		try {
			registerImplementation(new AndCondition<>(
										new OrCondition<>(
												new RequiredPropertyCondition<Message>(SendGridConstants.API_KEY, properties),
												new AndCondition<>(
														new RequiredPropertyCondition<Message>(SendGridConstants.USERNAME, properties),
														new RequiredPropertyCondition<Message>(SendGridConstants.PASSWORD, properties))),
										new RequiredClassCondition<Message>("com.sendgrid.SendGrid")),
					new SendGridBuilder().useDefaults(properties));
		} catch (Throwable e) {
			LOG.debug("Can't register SendGrid implementation", e);
		}
		return this;
	}

	/**
	 * Enables automatic filling of emails with values that come from multiple
	 * sources. It let you use your own builder instead of using default
	 * behaviors.
	 * 
	 * @param builder
	 *            the builder for constructing the message filler
	 * @return this instance for fluent use
	 */
	public EmailBuilder withAutoFilling(MessageFillerBuilder builder) {
		messageFillerBuilder = builder;
		return this;
	}

	/**
	 * Enables automatic filling of emails with values that come from multiple
	 * sources:
	 * <ul>
	 * <li>Fill email with values that come from provided configuration
	 * properties.</li>
	 * <li>Generate subject for the email (see {@link SubjectFiller})</li>
	 * </ul>
	 * See {@link MessageFillerBuilder#useDefaults(Properties, String)} for more
	 * information.
	 * <p>
	 * Automatically called by {@link #useDefaults()} and
	 * {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @param props
	 *            the properties that contains the values to set on the email
	 * @param baseKey
	 *            the prefix for the keys used for filling the message
	 * @return this instance for fluent use
	 */
	public EmailBuilder withAutoFilling(Properties props, String baseKey) {
		withAutoFilling(new MessageFillerBuilder().useDefaults(props, baseKey));
		return this;
	}

	/**
	 * Enables automatic filling of emails with values that come from multiple
	 * sources:
	 * <ul>
	 * <li>Fill email with values that come from provided configuration
	 * properties. It uses the default prefix for the keys ("ogham.email").</li>
	 * <li>Generate subject for the email (see {@link SubjectFiller})</li>
	 * </ul>
	 * <p>
	 * Automatically called by {@link #useDefaults()} and
	 * {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @param props
	 *            the properties that contains the values to set on the email
	 * @return this instance for fluent use
	 */
	public EmailBuilder withAutoFilling(Properties props) {
		return withAutoFilling(props, EmailConstants.PROPERTIES_PREFIX);
	}

	/**
	 * Enables automatic filling of emails with values that come from multiple
	 * sources:
	 * <ul>
	 * <li>Fill email with values that come from system configuration
	 * properties. It uses the default prefix for the keys ("ogham.email").</li>
	 * <li>Generate subject for the email (see {@link SubjectFiller})</li>
	 * </ul>
	 * <p>
	 * Automatically called by {@link #useDefaults()} and
	 * {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @return this instance for fluent use
	 */
	public EmailBuilder withAutoFilling() {
		return withAutoFilling(BuilderUtils.getDefaultProperties());
	}

	/**
	 * Enables templating support using all default behaviors and values. See
	 * {@link ContentTranslatorBuilder#useDefaults()} for more information.
	 * 
	 * <p>
	 * Automatically called by {@link #useDefaults()} and
	 * {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @return this instance for fluent use
	 */
	public EmailBuilder withTemplate() {
		return withTemplate(BuilderUtils.getDefaultProperties());
	}

	/**
	 * Enables templating support using all default behaviors and values. See
	 * {@link ContentTranslatorBuilder#useDefaults()} for more information.
	 * 
	 * <p>
	 * Automatically called by {@link #useDefaults()} and
	 * {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @param properties
	 *            the properties to use
	 * @return this instance for fluent use
	 */
	public EmailBuilder withTemplate(Properties properties) {
		return withTemplate(new ContentTranslatorBuilder().useDefaults(properties));
	}

	/**
	 * Enables templating support using the provided
	 * {@link ContentTranslatorBuilder}. It decorates the email sender with a
	 * {@link ContentTranslatorSender}.
	 * 
	 * @param builder
	 *            the builder to use for constructing the
	 *            {@link ContentTranslator} instead of using the default one
	 * @return this instance for fluent use
	 */
	public EmailBuilder withTemplate(ContentTranslatorBuilder builder) {
		this.contentTranslatorBuilder = builder;
		return this;
	}

	/**
	 * Enable attachment features like attachment resolution based on lookup
	 * mapping. It delegates to {@link AttachmentResourceTranslatorBuilder} with
	 * the default behavior and values (see
	 * {@link AttachmentResourceTranslatorBuilder#useDefaults()}).
	 * 
	 * <p>
	 * Automatically called by {@link #useDefaults()} and
	 * {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @return this instance for fluent use
	 */
	public EmailBuilder withAttachmentFeatures() {
		return withAttachmentFeatures(new AttachmentResourceTranslatorBuilder().useDefaults());
	}

	/**
	 * Enable attachment features using the provided translator builder.
	 * 
	 * <p>
	 * Automatically called by {@link #useDefaults()} and
	 * {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @param builder
	 *            the builder for the translator to use instead of the default
	 *            one
	 * @return this instance for fluent use
	 */
	public EmailBuilder withAttachmentFeatures(AttachmentResourceTranslatorBuilder builder) {
		resourceTranslatorBuilder = builder;
		return this;
	}

	/**
	 * Get reference to the specialized builder. It may be useful to fine tune a
	 * specific implementation.
	 * 
	 * @param clazz
	 *            the class of the builder to get
	 * @param <B>
	 *            the type of the class to get
	 * @return the builder instance for the specific implementation
	 * @throws IllegalArgumentException
	 *             when provided class references an nonexistent builder
	 */
	@SuppressWarnings("unchecked")
	public <B extends Builder<? extends MessageSender>> B getImplementationBuilder(Class<B> clazz) {
		for (Builder<? extends MessageSender> builder : implementations.values()) {
			if (clazz.isAssignableFrom(builder.getClass())) {
				return (B) builder;
			}
		}
		throw new IllegalArgumentException("No implementation builder exists for " + clazz.getSimpleName());
	}

	/**
	 * Get the reference to the specialized builder for Java Mail API. It may be
	 * useful to fine tune Java Mail API implementation.
	 * 
	 * @return The specialized builder for Java Mail API
	 */
	public JavaMailBuilder getJavaMailBuilder() {
		return getImplementationBuilder(JavaMailBuilder.class);
	}

	/**
	 * Get the reference to the specialized builder for SendGrid. It may be
	 * useful to fine tune SendGrid implementation.
	 * 
	 * @return The specialized builder for SendGrid
	 */
	public SendGridBuilder getSendGridBuilder() {
		return getImplementationBuilder(SendGridBuilder.class);
	}
}
