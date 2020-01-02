package fr.sii.ogham.email.builder.javamail;

import static fr.sii.ogham.core.condition.fluent.MessageConditions.requiredProperty;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Properties;

import javax.activation.MimetypesFileTypeMap;
import javax.mail.Authenticator;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.ActivableAtRuntime;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilderDelegate;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilderDelegate;
import fr.sii.ogham.core.builder.mimetype.SimpleMimetypeDetectionBuilder;
import fr.sii.ogham.core.charset.CharsetDetector;
import fr.sii.ogham.core.charset.FixedCharsetDetector;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.convert.DefaultConverter;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.message.content.MayHaveStringContent;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.resource.ByteResource;
import fr.sii.ogham.core.resource.FileResource;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.content.ContentWithAttachments;
import fr.sii.ogham.email.sender.impl.JavaMailSender;
import fr.sii.ogham.email.sender.impl.PropertiesBridge;
import fr.sii.ogham.email.sender.impl.javamail.ContentWithAttachmentsHandler;
import fr.sii.ogham.email.sender.impl.javamail.FileResourceHandler;
import fr.sii.ogham.email.sender.impl.javamail.JavaMailInterceptor;
import fr.sii.ogham.email.sender.impl.javamail.MapAttachmentResourceHandler;
import fr.sii.ogham.email.sender.impl.javamail.MultiContentHandler;
import fr.sii.ogham.email.sender.impl.javamail.PriorizedContentHandler;
import fr.sii.ogham.email.sender.impl.javamail.StreamResourceHandler;
import fr.sii.ogham.email.sender.impl.javamail.StringContentHandler;

/**
 * Configures how Java Mail implementation will send {@link Email}s.
 * 
 * <p>
 * To send {@link Email} using Java Mail, you need to register this builder into
 * a {@link MessagingBuilder} like this:
 * 
 * <pre>
 * <code>
 * MessagingBuilder msgBuilder = ...
 * msgBuilder.email()
 *    .sender(JavaMailBuilder.class)    // registers the builder and accesses to that builder for configuring it
 * </code>
 * </pre>
 * 
 * Once the builder is registered, sending email through Java Mail requires at
 * least host of the SMTP server. You can define it using:
 * 
 * <pre>
 * <code>
 * msgBuilder.email()
 *    .sender(JavaMailBuilder.class)    // registers the builder and accesses to that builder for configuring it
 *       .host("localhost")
 * </code>
 * </pre>
 * 
 * Or you can also use property keys (using interpolation):
 * 
 * <pre>
 * <code>
 * msgBuilder
 * .environment()
 *    .properties()
 *       .set("custom.property.for.host", "localhost")
 *       .and()
 *    .and()
 * .email()
 *    .sender(JavaMailBuilder.class)    // registers the builder and accesses to that builder for configuring it
 *       .host()
 *       	.properties("${custom.property.for.host}")
 * </code>
 * </pre>
 * 
 * You can do the same with port of the SMTP server.
 * 
 * 
 * <p>
 * SMTP server may require authentication. In most cases, authentication is done
 * using username/password. You can use this builder to quickly provide your
 * username and password:
 * 
 * <pre>
 * <code>
 * .sender(JavaMailBuilder.class)
 *    .authenticator()
 *        .username("foo")
 *        .password("bar")
 * </code>
 * </pre>
 * 
 * If you need another authentication mechanism, you can directly provide your
 * own {@link Authenticator} implementation:
 * 
 * <pre>
 * <code>
 * .sender(JavaMailBuilder.class)
 *    .authenticator(new MyCustomAuthenticator())
 * </code>
 * </pre>
 * 
 * 
 * <p>
 * Finally, Ogham will transform general {@link Email} object into
 * {@link MimeMessage}, {@link MimeMultipart}, {@link MimeBodyPart} objects.
 * This transformation will fit almost all use cases but you may need to
 * customize a part of the javax.mail message. Instead of doing again the same
 * work Ogham does, this builder allows you to intercept the message to modify
 * it just before sending it:
 * 
 * <pre>
 * <code>
 * .sender(JavaMailBuilder.class)
 *    .intercept(new MyCustomInterceptor())
 * </code>
 * </pre>
 * 
 * See {@link JavaMailInterceptor} for more information.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class JavaMailBuilder extends AbstractParent<EmailBuilder> implements Builder<JavaMailSender>, ActivableAtRuntime {
	private static final Logger LOG = LoggerFactory.getLogger(JavaMailBuilder.class);

	private EnvironmentBuilder<JavaMailBuilder> environmentBuilder;
	private final ConfigurationValueBuilderHelper<JavaMailBuilder, String> hostValueBuilder;
	private final ConfigurationValueBuilderHelper<JavaMailBuilder, Integer> portValueBuilder;
	private final ConfigurationValueBuilderHelper<JavaMailBuilder, Charset> charsetValueBuilder;
	private Authenticator authenticator;
	private UsernamePasswordAuthenticatorBuilder authenticatorBuilder;
	private JavaMailInterceptor interceptor;
	private MimetypeDetectionBuilder<JavaMailBuilder> mimetypeBuilder;
	private CharsetDetector charsetDetector;

	/**
	 * Default constructor when using JavaMail sender without all Ogham work.
	 * 
	 * <strong>WARNING: use is only if you know what you are doing !</strong>
	 */
	public JavaMailBuilder() {
		this(null, null);
		environment();
		mimetype();
	}

	/**
	 * Constructor that is called when using Ogham builder:
	 * 
	 * <pre>
	 * MessagingBuilder msgBuilder = ...
	 * msgBuilder
	 * .email()
	 *    .sender(JavaMailBuilder.class)
	 * </pre>
	 * 
	 * <p>
	 * Initializes the builder with the parent instance (used by the
	 * {@link #and()} method) and the {@link EnvironmentBuilder}. The
	 * {@link EnvironmentBuilder} is used to evaluate property values when
	 * {@link #build()} is called.
	 * 
	 * @param parent
	 *            the parent builder instance for fluent chaining
	 * @param environmentBuilder
	 *            used to evaluate property values
	 */
	public JavaMailBuilder(EmailBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		if (environmentBuilder != null) {
			this.environmentBuilder = new EnvironmentBuilderDelegate<>(this, environmentBuilder);
		}
		hostValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class);
		portValueBuilder = new ConfigurationValueBuilderHelper<>(this, Integer.class);
		charsetValueBuilder = new ConfigurationValueBuilderHelper<>(this, Charset.class);
	}

	/**
	 * Set the mail server address host (IP or hostname).
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #host()}.
	 * 
	 * <pre>
	 * .host("smtp.gmail.com")
	 * .host()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("localhost")
	 * </pre>
	 * 
	 * <pre>
	 * .host("smtp.gmail.com")
	 * .host()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("localhost")
	 * </pre>
	 * 
	 * In both cases, {@code host("smtp.gmail.com")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param host
	 *            the host of the mail server
	 * @return this instance for fluent chaining
	 */
	public JavaMailBuilder host(String host) {
		hostValueBuilder.setValue(host);
		return this;
	}
	
	
	/**
	 * Set the mail server address host (IP or hostname).
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .host()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("localhost")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #host(String)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .host("smtp.gmail.com")
	 * .host()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("localhost")
	 * </pre>
	 * 
	 * The value {@code "smtp.gmail.com"} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<JavaMailBuilder, String> host() {
		return hostValueBuilder;
	}
	
	/**
	 * Set the mail server port.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #port()}.
	 * 
	 * <pre>
	 * .port(10025)
	 * .port()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(25)
	 * </pre>
	 * 
	 * <pre>
	 * .port(10025)
	 * .port()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(25)
	 * </pre>
	 * 
	 * In both cases, {@code port(10025)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param port
	 *            the port of mail server
	 * @return this instance for fluent chaining
	 */
	public JavaMailBuilder port(Integer port) {
		portValueBuilder.setValue(port);
		return this;
	}

	
	/**
	 * Set the mail server port
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .port()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(25)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #port(Integer)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .port(10025)
	 * .port()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(25)
	 * </pre>
	 * 
	 * The value {@code 10025} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<JavaMailBuilder, Integer> port() {
		return portValueBuilder;
	}
	
	/**
	 * Set charset to use for email body.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #charset()}.
	 * 
	 * <pre>
	 * .charset(StandardCharsets.UTF_16)
	 * .charset()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(StandardCharsets.UTF_8)
	 * </pre>
	 * 
	 * <pre>
	 * .charset(StandardCharsets.UTF_16)
	 * .charset()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(StandardCharsets.UTF_8)
	 * </pre>
	 * 
	 * In both cases, {@code charset(StandardCharsets.UTF_16)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param charset
	 *            the charset to use for email body
	 * @return this instance for fluent chaining
	 */
	public JavaMailBuilder charset(Charset charset) {
		charsetValueBuilder.setValue(charset);
		return this;
	}
	
	
	/**
	 * Set charset to use for email body
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .charset()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(StandardCharsets.UTF_8)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #charset(Charset)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .charset(StandardCharsets.UTF_16)
	 * .charset()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(StandardCharsets.UTF_8)
	 * </pre>
	 * 
	 * The value {@code StandardCharsets.UTF_16} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<JavaMailBuilder, Charset> charset() {
		return charsetValueBuilder;
	}

	/**
	 * Defines a custom detector that will indicate which charset corresponds
	 * for a particular string.
	 * 
	 * This value preempts any other value defined by calling
	 * {@link #charset(Charset)} method.
	 * 
	 * If this method is called several times, only the last provider is used.
	 * 
	 * @param charsetDetector
	 *            the provider used to detect charset of a string
	 * @return this instance for fluent chaining
	 */
	public JavaMailBuilder charset(CharsetDetector charsetDetector) {
		this.charsetDetector = charsetDetector;
		return this;
	}

	/**
	 * SMTP server may require authentication. In most cases, authentication is
	 * done using username/password. You can use this builder to quickly provide
	 * your username and password:
	 * 
	 * <pre>
	 * .sender(JavaMailBuilder.class)
	 *    .authenticator()
	 *        .username("foo")
	 *        .password("bar")
	 * </pre>
	 * 
	 * @return the builder to configure username/password authentication
	 */
	public UsernamePasswordAuthenticatorBuilder authenticator() {
		if (authenticatorBuilder == null) {
			authenticatorBuilder = new UsernamePasswordAuthenticatorBuilder(this);
		}
		return authenticatorBuilder;
	}

	/**
	 * SMTP server may require authentication. In most cases, authentication is
	 * done using username/password. However, if you need another authentication
	 * mechanism, you can directly provide your own {@link Authenticator}
	 * implementation:
	 * 
	 * <pre>
	 * .sender(JavaMailBuilder.class)
	 *    .authenticator(new MyCustomAuthenticator())
	 * </pre>
	 * 
	 * @param authenticator
	 *            the custom authenticator implementation
	 * @return the builder to configure username/password authentication
	 */
	public JavaMailBuilder authenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
		return this;
	}

	/**
	 * Configures environment for the builder (and sub-builders). Environment
	 * consists of configuration properties/values that are used to configure
	 * the system (see {@link EnvironmentBuilder} for more information).
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
	 * If no environment was previously used, it creates a new one. Then each
	 * time you call {@link #environment()}, the same instance is used.
	 * </p>
	 * 
	 * @return the builder to configure properties handling
	 */
	public EnvironmentBuilder<JavaMailBuilder> environment() {
		if (environmentBuilder == null) {
			environmentBuilder = new SimpleEnvironmentBuilder<>(this);
		}
		return environmentBuilder;
	}

	/**
	 * NOTE: this is mostly for advance usage (when creating a custom module).
	 * 
	 * Inherits environment configuration from another builder. This is useful
	 * for configuring independently different parts of Ogham but keeping a
	 * whole coherence (see {@link DefaultJavaMailConfigurer} for an example of
	 * use).
	 * 
	 * The same instance is shared meaning that all changes done here will also
	 * impact the other builder.
	 * 
	 * <p>
	 * If a previous builder was defined (by calling {@link #environment()} for
	 * example), the new builder will override it.
	 * 
	 * @param builder
	 *            the builder to inherit
	 * @return this instance for fluent chaining
	 */
	public JavaMailBuilder environment(EnvironmentBuilder<?> builder) {
		environmentBuilder = new EnvironmentBuilderDelegate<>(this, builder);
		return this;
	}

	/**
	 * Ogham will transform general {@link Email} object into
	 * {@link MimeMessage}, {@link MimeMultipart}, {@link MimeBodyPart} objects.
	 * This transformation will fit almost all use cases but you may need to
	 * customize a part of the javax.mail message. Instead of doing again the
	 * same work Ogham does, this builder allows you to intercept the message to
	 * modify it just before sending it:
	 * 
	 * <pre>
	 * .sender(JavaMailBuilder.class)
	 *    .intercept(new MyCustomInterceptor())
	 * </pre>
	 * 
	 * See {@link JavaMailInterceptor} for more information.
	 * 
	 * @param interceptor
	 *            the custom interceptor used to modify {@link MimeMessage}
	 * @return this instance for fluent chaining
	 */
	public JavaMailBuilder intercept(JavaMailInterceptor interceptor) {
		this.interceptor = interceptor;
		return this;
	}

	/**
	 * Builder that configures mimetype detection. Detection is used here to
	 * detect mimetype of {@link Attachment}s.
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
	 * If no mimetype detector was previously defined, it creates a new one.
	 * Then each time you call {@link #mimetype()}, the same instance is used.
	 * </p>
	 * 
	 * @return the builder to configure mimetype detection
	 */
	public MimetypeDetectionBuilder<JavaMailBuilder> mimetype() {
		if (mimetypeBuilder == null) {
			mimetypeBuilder = new SimpleMimetypeDetectionBuilder<>(this, environmentBuilder);
		}
		return mimetypeBuilder;
	}

	/**
	 * NOTE: this is mostly for advance usage (when creating a custom module).
	 * 
	 * Inherits mimetype configuration from another builder. This is useful for
	 * configuring independently different parts of Ogham but keeping a whole
	 * coherence.
	 * 
	 * The same instance is shared meaning that all changes done here will also
	 * impact the other builder.
	 * 
	 * <p>
	 * If a previous builder was defined (by calling {@link #mimetype()} for
	 * example), the new builder will override it.
	 * 
	 * @param builder
	 *            the builder to inherit
	 * @return this instance for fluent chaining
	 */
	public JavaMailBuilder mimetype(MimetypeDetectionBuilder<?> builder) {
		mimetypeBuilder = new MimetypeDetectionBuilderDelegate<>(this, builder);
		return this;
	}

	@Override
	public JavaMailSender build() {
		Properties props = buildProperties();
		MimeTypeProvider mimetypeProvider = mimetype().build();
		LOG.info("Sending email using JavaMail API is registered");
		LOG.debug("SMTP server address: {}:{}", props.getProperty("mail.host"), props.getProperty("mail.port"));
		return new JavaMailSender(props, buildContentHandler(mimetypeProvider), buildAttachmentHandler(mimetypeProvider), buildAuthenticator(), interceptor);
	}

	@Override
	public Condition<Message> getCondition() {
		PropertyResolver propertyResolver = buildPropertyResolver();
		return requiredProperty(propertyResolver, "mail.host").or(requiredProperty(propertyResolver, "mail.smtp.host"));
	}

	private Properties buildProperties() {
		return new PropertiesBridge(buildPropertyResolver());
	}

	private OverrideJavaMailResolver buildPropertyResolver() {
		return new OverrideJavaMailResolver(getPropertyResolver(), getConverter(), hostValueBuilder, portValueBuilder);
	}

	private Converter getConverter() {
		if (environmentBuilder == null) {
			return new DefaultConverter();
		}
		return environmentBuilder.converter().build();
	}

	private Authenticator buildAuthenticator() {
		if (this.authenticator != null) {
			return this.authenticator;
		}
		if (authenticatorBuilder != null) {
			return authenticatorBuilder.build();
		}
		return null;
	}

	private PriorizedContentHandler buildContentHandler(MimeTypeProvider mimetypeProvider) {
		PriorizedContentHandler contentHandler = new PriorizedContentHandler();
		contentHandler.register(MultiContent.class, new MultiContentHandler(contentHandler));
		contentHandler.register(ContentWithAttachments.class, new ContentWithAttachmentsHandler(contentHandler));
		contentHandler.register(MayHaveStringContent.class, new StringContentHandler(mimetypeProvider, buildCharset()));
		return contentHandler;
	}

	private CharsetDetector buildCharset() {
		if (this.charsetDetector != null) {
			return this.charsetDetector;
		}
		Charset charset = this.charsetValueBuilder.getValue(getPropertyResolver());
		if (charset != null) {
			return new FixedCharsetDetector(charset);
		}
		return new FixedCharsetDetector();
	}

	private PropertyResolver getPropertyResolver() {
		if (environmentBuilder != null) {
			return environmentBuilder.build();
		}
		return BuilderUtils.getDefaultPropertyResolver(BuilderUtils.getDefaultProperties());
	}

	private static MapAttachmentResourceHandler buildAttachmentHandler(MimeTypeProvider mimetypeProvider) {
		MapAttachmentResourceHandler attachmentHandler = new MapAttachmentResourceHandler();
		attachmentHandler.addResourceHandler(ByteResource.class, new StreamResourceHandler(mimetypeProvider));
		attachmentHandler.addResourceHandler(FileResource.class, new FileResourceHandler(mimetypeProvider));
		return attachmentHandler;
	}

}
