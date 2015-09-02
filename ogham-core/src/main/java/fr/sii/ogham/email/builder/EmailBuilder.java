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
import fr.sii.ogham.core.builder.TemplateBuilder;
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
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.core.sender.MultiImplementationSender;
import fr.sii.ogham.core.translator.content.ContentTranslator;
import fr.sii.ogham.core.translator.resource.AttachmentResourceTranslator;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.email.EmailConstants;
import fr.sii.ogham.email.EmailConstants.SendGridConstants;
import fr.sii.ogham.email.sender.AttachmentResourceTranslatorSender;
import fr.sii.ogham.email.sender.EmailSender;
import fr.sii.ogham.template.TemplateConstants;

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
 * @see SendGridBuilder
 * @see TemplateBuilder
 * @see AttachmentResourceTranslatorBuilder
 * @see ContentTranslatorBuilder
 * @see MessageFillerBuilder
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

	/**
	 * Own property key for template resolution prefix
	 */
	private String templatePrefixKey;

	/**
	 * Own property key for template resolution prefix
	 */
	private String templateSuffixKey;

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
			if (templatePrefixKey != null) {
				LOG.debug("Use custom property key {} for prefix template resolution", templatePrefixKey);
				getTemplateBuilder().setPrefixKey(templatePrefixKey);
			}
			if (templateSuffixKey != null) {
				LOG.debug("Use custom property key {} for suffix template resolution", templateSuffixKey);
				getTemplateBuilder().setSuffixKey(templateSuffixKey);
			}
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
		enableEmailTemplateKeys();
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
			// @formatter:off
			registerImplementation(new AndCondition<>(
										new OrCondition<>(
												new RequiredPropertyCondition<Message>("mail.smtp.host", properties),
												new RequiredPropertyCondition<Message>("mail.host",	properties)),
										new RequiredClassCondition<Message>("javax.mail.Transport"),
										new RequiredClassCondition<Message>("com.sun.mail.smtp.SMTPTransport")),
					new JavaMailBuilder().useDefaults(properties));
			// @formatter:on
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
	 * <li>The property <code>ogham.email.sendgrid.api.key</code> is set</li>
	 * <li>The property <code>ogham.email.sendgrid.username</code> and
	 * <code>ogham.email.sendgrid.password</code> is set</li>
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
			// @formatter:off
			registerImplementation(new AndCondition<>(
										new OrCondition<>(
												new RequiredPropertyCondition<Message>(SendGridConstants.API_KEY, properties),
												new AndCondition<>(
														new RequiredPropertyCondition<Message>(SendGridConstants.USERNAME, properties),
														new RequiredPropertyCondition<Message>(SendGridConstants.PASSWORD, properties))),
										new RequiredClassCondition<Message>("com.sendgrid.SendGrid")),
					new SendGridBuilder().useDefaults(properties));
			// @formatter:on
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
	 * See {@link MessageFillerBuilder#useDefaults(Properties, String...)} for
	 * more information.
	 * <p>
	 * Automatically called by {@link #useDefaults()} and
	 * {@link #useDefaults(Properties)}
	 * </p>
	 * 
	 * @param props
	 *            the properties that contains the values to set on the email
	 * @param baseKeys
	 *            the prefix(es) for the keys used for filling the message
	 * @return this instance for fluent use
	 */
	public EmailBuilder withAutoFilling(Properties props, String... baseKeys) {
		withAutoFilling(new MessageFillerBuilder().useDefaults(props, baseKeys));
		return this;
	}

	/**
	 * Enables automatic filling of emails with values that come from multiple
	 * sources:
	 * <ul>
	 * <li>Fill email with values that come from provided configuration
	 * properties. It uses the default prefix for the keys ("mail" and
	 * "ogham.email").</li>
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
		return withAutoFilling(props, EmailConstants.FILL_PREFIXES);
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
	 * <p>
	 * Calling this method will enable different location for email templates
	 * from default one. The location will be specified by different property
	 * keys for prefix and suffix.
	 * </p>
	 * 
	 * By default default properties are:
	 * <ul>
	 * <li>ogham.template.prefix (see {@link TemplateConstants#PREFIX_PROPERTY})
	 * </li>
	 * <li>ogham.template.suffix (see {@link TemplateConstants#SUFFIX_PROPERTY}</li>
	 * </ul>
	 * 
	 * Calling this method will change the property keys to:
	 * <ul>
	 * <li>ogham.email.template.prefix (see
	 * {@link fr.sii.ogham.email.EmailConstants.TemplateConstants#PREFIX_PROPERTY}
	 * </li>
	 * <li>ogham.email.template.suffix (see
	 * {@link fr.sii.ogham.email.EmailConstants.TemplateConstants#SUFFIX_PROPERTY}
	 * </li>
	 * </ul>
	 * 
	 * @return this instance for fluent use
	 */
	public EmailBuilder enableEmailTemplateKeys() {
		setTemplatePrefixKey(EmailConstants.TemplateConstants.PREFIX_PROPERTY);
		setTemplateSuffixKey(EmailConstants.TemplateConstants.SUFFIX_PROPERTY);
		return this;
	}

	/**
	 * <p>
	 * Calling this method will enable different location for email templates
	 * from default one. The location will be specified by a different property
	 * key for prefix.
	 * </p>
	 * 
	 * <p>
	 * By default default property key is ogham.template.prefix (see
	 * {@link TemplateConstants#PREFIX_PROPERTY})
	 * </p>
	 * 
	 * <p>
	 * Calling this method will change the property key to the provided key.
	 * </p>
	 * 
	 * @param prefixKey
	 *            the new key for the email template prefix
	 * @return this instance for fluent use
	 */
	public EmailBuilder setTemplatePrefixKey(String prefixKey) {
		this.templatePrefixKey = prefixKey;
		return this;
	}

	/**
	 * <p>
	 * Calling this method will enable different location for email templates
	 * from default one. The location will be specified by a different property
	 * key for suffix.
	 * </p>
	 * 
	 * <p>
	 * By default default property key is ogham.template.prefix (see
	 * {@link TemplateConstants#SUFFIX_PROPERTY})
	 * </p>
	 * 
	 * <p>
	 * Calling this method will change the property key to the provided key.
	 * </p>
	 * 
	 * @param suffixKey
	 *            the new key for the email template suffix
	 * @return this instance for fluent use
	 */
	public EmailBuilder setTemplateSuffixKey(String suffixKey) {
		this.templateSuffixKey = suffixKey;
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
	 * <p>
	 * Get reference to the specialized builder. It may be useful to fine tune a
	 * specific implementation.
	 * </p>
	 * <p>
	 * There also exists shortcuts: {@link #getJavaMailBuilder()} and
	 * {@link #getSendGridBuilder()}.
	 * </p>
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
	 * <p>
	 * Get the reference to the specialized builder for Java Mail API. It may be
	 * useful to fine tune Java Mail API implementation.
	 * </p>
	 * 
	 * Access this builder if you want to:
	 * <ul>
	 * <li>Customize content handler management</li>
	 * <li>Customize resource attachment handler management</li>
	 * <li>Customize Mimetype detection</li>
	 * <li>Use custom Authenticator</li>
	 * <li>Use custom interceptor</li>
	 * </ul>
	 * 
	 * @return The specialized builder for Java Mail API
	 */
	public JavaMailBuilder getJavaMailBuilder() {
		return getImplementationBuilder(JavaMailBuilder.class);
	}

	/**
	 * <p>
	 * Get the reference to the specialized builder for SendGrid. It may be
	 * useful to fine tune SendGrid implementation.
	 * </p>
	 * 
	 * Access this builder if you want to:
	 * <ul>
	 * <li>Customize content handler management</li>
	 * <li>Customize Mimetype detection</li>
	 * <li>Customize username/password/API key</li>
	 * <li>Provide your own SendGrid client implementation</li>
	 * </ul>
	 * 
	 * @return The specialized builder for SendGrid
	 */
	public SendGridBuilder getSendGridBuilder() {
		return getImplementationBuilder(SendGridBuilder.class);
	}

	/**
	 * <p>
	 * Get the builder used for filling messages.
	 * </p>
	 * 
	 * Access this builder if you want to:
	 * <ul>
	 * <li>Enable/disable automatic filling of messages with values provided in
	 * configuration</li>
	 * <li>Enable/disable automatic filling of subject for messages based on
	 * templates</li>
	 * <li>Add your own message filler</li>
	 * </ul>
	 * 
	 * @return the builder used for filling messages
	 */
	public MessageFillerBuilder getMessageFillerBuilder() {
		return messageFillerBuilder;
	}

	/**
	 * <p>
	 * Get the builder used transform the content of the message. It may be
	 * useful to fine tune templating mechanism, resource inlining and messages
	 * with with several contents.
	 * </p>
	 * 
	 * Access this builder if you want to:
	 * <ul>
	 * <li>Customize templating mechanism (see {@link #getTemplateBuilder()})</li>
	 * <li>Enable/disable support for messages with multiple contents</li>
	 * <li>Enable/disable support for inlining of resources</li>
	 * <li>Add your own content translator</li>
	 * </ul>
	 * 
	 * @return the builder used to transform the content of the message
	 */
	public ContentTranslatorBuilder getContentTranslatorBuilder() {
		return contentTranslatorBuilder;
	}

	/**
	 * <p>
	 * Shortcut to directly access template builder for fine tuning templating
	 * mechanism.
	 * </p>
	 * 
	 * Access this builder if you want to:
	 * <ul>
	 * <li>Customize how template resources are resolved</li>
	 * <li>Register a custom lookup mapping resolver for template resources</li>
	 * <li>Use your own template engine</li>
	 * <li>Customize the template engine configuration</li>
	 * <li>Set the prefix and suffix for template resolution</li>
	 * <li>Set the property key for prefix and suffix resolution</li>
	 * </ul>
	 * 
	 * @return the template builder
	 */
	public TemplateBuilder getTemplateBuilder() {
		return contentTranslatorBuilder.getTemplateBuilder();
	}

	/**
	 * <p>
	 * Get the builder used to transform the resources associated to the
	 * message. It may be useful to fine tune how to attach resources to
	 * messages.
	 * </p>
	 * 
	 * Access this builder if you want to:
	 * <ul>
	 * <li>Customize how attached resources are transformed</li>
	 * <li>Customize how attached resources are resolved for transformation</li>
	 * <li>Register a custom lookup mapping resolver for attached resources</li>
	 * </ul>
	 * 
	 * @return the builder used to transform the resources associated to the
	 *         message
	 */
	public AttachmentResourceTranslatorBuilder getResourceTranslatorBuilder() {
		return resourceTranslatorBuilder;
	}
}
