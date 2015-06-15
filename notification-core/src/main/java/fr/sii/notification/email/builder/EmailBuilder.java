package fr.sii.notification.email.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.core.builder.Builder;
import fr.sii.notification.core.builder.ContentTranslatorBuilder;
import fr.sii.notification.core.builder.NotificationSenderBuilder;
import fr.sii.notification.core.condition.AndCondition;
import fr.sii.notification.core.condition.Condition;
import fr.sii.notification.core.condition.RequiredClassCondition;
import fr.sii.notification.core.condition.RequiredPropertyCondition;
import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.core.filler.PropertiesFiller;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.sender.ConditionalSender;
import fr.sii.notification.core.sender.ContentTranslatorSender;
import fr.sii.notification.core.sender.FillerSender;
import fr.sii.notification.core.sender.MultiImplementationSender;
import fr.sii.notification.core.sender.NotificationSender;
import fr.sii.notification.core.translator.content.ContentTranslator;
import fr.sii.notification.core.translator.resource.AttachmentResourceTranslator;
import fr.sii.notification.core.util.BuilderUtil;
import fr.sii.notification.email.EmailConstants;
import fr.sii.notification.email.sender.AttachmentResourceTranslatorSender;
import fr.sii.notification.email.sender.EmailSender;

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
public class EmailBuilder implements NotificationSenderBuilder<ConditionalSender> {
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

	private Map<Condition<Message>, Builder<? extends NotificationSender>> implementations;

	public EmailBuilder() {
		super();
		sender = emailSender = new EmailSender();
		implementations = new HashMap<>();
	}

	@Override
	public ConditionalSender build() throws BuildException {
		for (Entry<Condition<Message>, Builder<? extends NotificationSender>> impl : implementations.entrySet()) {
			NotificationSender s = impl.getValue().build();
			LOG.debug("Implementation {} registered", s);
			emailSender.addImplementation(impl.getKey(), s);
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
		return useDefaults(BuilderUtil.getDefaultProperties());
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
		withConfigurationFiller(properties);
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
	public EmailBuilder registerImplementation(Condition<Message> condition, NotificationSender implementation) {
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
	public EmailBuilder registerImplementation(Condition<Message> condition, Builder<? extends NotificationSender> builder) {
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
		return registerDefaultImplementations(BuilderUtil.getDefaultProperties());
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
			registerImplementation(new AndCondition<>(
						new RequiredPropertyCondition<Message>("mail.smtp.host", properties),
						new RequiredClassCondition<Message>("javax.mail.Transport"),
						new RequiredClassCondition<Message>("com.sun.mail.smtp.SMTPTransport")),
					new JavaMailBuilder().useDefaults(properties));
		} catch (Throwable e) {
			LOG.debug("Can't register Java Mail implementation", e);
		}
		return this;
	}

	/**
	 * Enables filling of emails with values that comes from provided
	 * configuration properties.
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
	public EmailBuilder withConfigurationFiller(Properties props, String baseKey) {
		sender = new FillerSender(new PropertiesFiller(props, baseKey), sender);
		return this;
	}

	/**
	 * Enables filling of emails with values that comes from provided
	 * configuration properties. It uses the default prefix for the keys
	 * ("notification.email").
	 * <p>
	 * Automatically called by {@link #useDefaults()} and
	 * {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @param props
	 *            the properties that contains the values to set on the email
	 * @return this instance for fluent use
	 */
	public EmailBuilder withConfigurationFiller(Properties props) {
		return withConfigurationFiller(props, EmailConstants.PROPERTIES_PREFIX);
	}

	/**
	 * Enables filling of emails with values that comes from system
	 * configuration properties. It uses the default prefix for the keys
	 * ("notification.email").
	 * <p>
	 * Automatically called by {@link #useDefaults()} and
	 * {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @return this instance for fluent use
	 */
	public EmailBuilder withConfigurationFiller() {
		return withConfigurationFiller(BuilderUtil.getDefaultProperties());
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
		return withTemplate(BuilderUtil.getDefaultProperties());
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
	 * Enables templating support using the provided {@link ContentTranslator}.
	 * It decorates the email sender with a {@link ContentTranslatorSender}.
	 * 
	 * @param translator
	 *            the translator to use for templating transformations
	 * @return this instance for fluent use
	 */
	public EmailBuilder withTemplate(ContentTranslator translator) {
		sender = new ContentTranslatorSender(translator, sender);
		return this;
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
		return withTemplate(builder.build());
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
	 * Enable attachment features like attachment resolution based on lookup
	 * mapping.
	 * 
	 * <p>
	 * Automatically called by {@link #useDefaults()} and
	 * {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @param builder
	 *            the builder to use instead of the default one
	 * @return this instance for fluent use
	 */
	public EmailBuilder withAttachmentFeatures(AttachmentResourceTranslatorBuilder builder) {
		return withAttachmentFeatures(builder.build());
	}

	/**
	 * Enable attachment features using the provided translator.
	 * 
	 * <p>
	 * Automatically called by {@link #useDefaults()} and
	 * {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @param translator
	 *            the translator to use instead of the default one
	 * @return this instance for fluent use
	 */
	public EmailBuilder withAttachmentFeatures(AttachmentResourceTranslator translator) {
		sender = new AttachmentResourceTranslatorSender(translator, sender);
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
	public <B extends Builder<? extends NotificationSender>> B getImplementationBuilder(Class<B> clazz) {
		for (Builder<? extends NotificationSender> builder : implementations.values()) {
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
}
