package fr.sii.notification.email.builder;

import java.util.Properties;

import javax.mail.Authenticator;

import fr.sii.notification.core.builder.ContentTranslatorBuilder;
import fr.sii.notification.core.builder.NotificationSenderBuilder;
import fr.sii.notification.core.condition.Condition;
import fr.sii.notification.core.condition.RequiredClassCondition;
import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.core.filler.PropertiesFiller;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.sender.ConditionalSender;
import fr.sii.notification.core.sender.ContentTranslatorSender;
import fr.sii.notification.core.sender.FillerSender;
import fr.sii.notification.core.sender.MultiImplementationSender;
import fr.sii.notification.core.sender.NotificationSender;
import fr.sii.notification.core.translator.ContentTranslator;
import fr.sii.notification.core.util.BuilderUtil;
import fr.sii.notification.email.EmailConstants;
import fr.sii.notification.email.attachment.translator.AttachmentSourceTranslator;
import fr.sii.notification.email.sender.AttachmentSourceTranslatorSender;
import fr.sii.notification.email.sender.EmailSender;

/**
 * <p>
 * Specialized builder for email sender.
 * </p>
 * There exists several implementations to send an email:
 * <ul>
 * <li>Using pure Java mail API</li>
 * <li>Using Apache Commons Email</li>
 * <li>Using any other library</li>
 * <li>Through a WebService</li>
 * <li>...</li>
 * </ul>
 * <p>
 * This builder provides a {@link MultiImplementationSender}. The aim of the
 * {@link MultiImplementationSender} is to choose the best implementation for
 * sending the email according to the runtime environement (detection of
 * libraries in the classpath, availability of a particular property, ...).
 * </p>
 * <p>
 * This builder let you the possibility to register any new implementation. It
 * allows you to enable or not templating support and automatic filling of
 * message values (like sender address for example).
 * </p>
 * 
 * @author Aurélien Baudet
 * @see EmailSender
 * @see JavaMailBuilder
 */
public class EmailBuilder implements NotificationSenderBuilder<ConditionalSender> {
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
	 * A builder for implementation based on Java mail API
	 */
	private JavaMailBuilder javaMailBuilder;

	public EmailBuilder() {
		super();
		sender = emailSender = new EmailSender();
		javaMailBuilder = new JavaMailBuilder().useDefaults();
	}

	@Override
	public ConditionalSender build() throws BuildException {
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
		setJavaMailBuilder(new JavaMailBuilder().useDefaults(properties));
		registerDefaultImplementations();
		withConfigurationFiller(properties);
		withTemplate();
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
	 * Register all default implementations:
	 * <ul>
	 * <li>Java mail API implementation</li>
	 * </ul>
	 * <p>
	 * Automatically called by {@link #useDefaults()} and
	 * {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @return this instance for fluent use
	 */
	public EmailBuilder registerDefaultImplementations() {
		registerImplementation(new RequiredClassCondition<Message>("javax.mail.Transport"), javaMailBuilder.build());
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
		return withTemplate(new ContentTranslatorBuilder().useDefaults());
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
	 * mapping. It delegates to {@link AttachmentSourceTranslatorBuilder} with
	 * the default behavior and values (see
	 * {@link AttachmentSourceTranslatorBuilder#useDefaults()}).
	 * 
	 * <p>
	 * Automatically called by {@link #useDefaults()} and
	 * {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @return this instance for fluent use
	 */
	public EmailBuilder withAttachmentFeatures() {
		return withAttachmentFeatures(new AttachmentSourceTranslatorBuilder().useDefaults());
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
	public EmailBuilder withAttachmentFeatures(AttachmentSourceTranslatorBuilder builder) {
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
	public EmailBuilder withAttachmentFeatures(AttachmentSourceTranslator translator) {
		sender = new AttachmentSourceTranslatorSender(translator, sender);
		return this;
	}

	/**
	 * Provide your own builder for Java mail API implementation.
	 * 
	 * @param javaMailBuilder
	 *            the builder to use instead of the default one
	 * @return this instance for fluent use
	 */
	public EmailBuilder setJavaMailBuilder(JavaMailBuilder javaMailBuilder) {
		this.javaMailBuilder = javaMailBuilder;
		return this;
	}

	/**
	 * Set the authentication mechanism to use for sending email. In case that
	 * you want to provide your own Java mail API builder, this method MUST be
	 * called after calling {@link #setJavaMailBuilder(JavaMailBuilder)}. If you
	 * call {@link #setJavaMailBuilder(JavaMailBuilder)} after, then the
	 * authentication mechanism will not be transmitted to the builder.
	 * 
	 * @param authenticator
	 *            the authentication mechanism
	 * @return this instance for fluent use
	 */
	public EmailBuilder setAuthenticator(Authenticator authenticator) {
		javaMailBuilder.setAuthenticator(authenticator);
		return this;
	}

	/**
	 * Get reference to the specialized builder for Java mail API builder in
	 * order to fine tuning it.
	 * 
	 * @return the builder for Java mail API
	 */
	public JavaMailBuilder getJavaMailBuilder() {
		return javaMailBuilder;
	}
}
