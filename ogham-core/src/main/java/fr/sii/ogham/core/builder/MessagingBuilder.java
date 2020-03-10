package fr.sii.ogham.core.builder;

import static java.util.Arrays.asList;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.activation.MimetypesFileTypeMap;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.ConfigurationPhase;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.mimetype.SimpleMimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.core.builder.resolution.StandaloneResourceResolutionBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.sender.ConditionalSender;
import fr.sii.ogham.core.service.EverySupportingMessagingService;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.service.WrapExceptionMessagingService;
import fr.sii.ogham.core.util.PriorizedList;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.builder.SmsBuilder;
import fr.sii.ogham.sms.message.Sms;

/**
 * Ogham provides many useful behaviors to focus on the message content and not
 * the need of understanding and implementing complex protocols. Sending emails
 * seems to be easy but the RFCs
 * (<a href="https://tools.ietf.org/html/rfc5321">RFC5321</a>,
 * <a href="https://tools.ietf.org/html/rfc821">RFC821</a>, ...) are long and
 * complex. If you don't know those RFCs, some email clients won't be able to
 * read your emails. This is the same with the
 * <a href="http://opensmpp.org/specs/smppv50.pdf">SMPP protocol</a> used for
 * sending SMS.
 * 
 * The builder is a helper to instantiate and configure a
 * {@link MessagingService}. The {@link MessagingService} is used to send:
 * <ul>
 * <li>{@link Email} messages</li>
 * <li>{@link Sms} messages</li>
 * </ul>
 * 
 * Content of the messages can be provided by templates. Templates are parsed by
 * template engines. Several template engines are supported:
 * <ul>
 * <li>Thymeleaf</li>
 * <li>Freemaker</li>
 * </ul>
 * 
 * Ogham allows to send {@link Email} and {@link Sms} using several
 * implementations:
 * <ul>
 * <li>JavaMail (STMP) or SendGrid (HTTP) for sending {@link Email}</li>
 * <li>Cloudhopper (SMPP) or OVH (HTTP) for sending {@link Sms}</li>
 * </ul>
 * The aim is to provide an abstraction to construct a portable message (
 * {@link Email} or {@link Sms}) and to be able to change infrastructure (SMTP
 * server to online HTTP service for example) without changing your code.
 * 
 * <p>
 * Here is an example of standard configuration that provides a default behavior
 * that fits 95% of usages:
 * 
 * <pre>
 * <code>
 * // Instantiate the messaging service
 * MessagingService service = MessagingBuilder.standard()
 *   .build();
 * // send the email
 * service.send(new Email()
 *   .content(new MultiTemplateContent("email/sample", new SampleBean("foo", 42)))
 *   .to("Foo Bar &lt;foo.bar@sii.fr&gt;"))
 *   .attach(new Attachment("file:/data/reports/report1.pdf"));
 * </code>
 * </pre>
 * 
 * This sample shows that:
 * <ul>
 * <li>System properties are used to configure "mail.host" and "mail.port"</li>
 * <li>As properties "mail.host" and "mail.port" are defined and
 * "ogham-email-javamail" is used, the email is sent using JavaMail
 * implementation</li>
 * <li>An email is sent to "foo.bar@sii.fr"</li>
 * <li>The email provides a main content (HTML) and a fallback content (text)
 * <ul>
 * <li>The HTML content comes from a template located in the classpath
 * (email/sample.html) and variables that are present in the template are
 * replaced by values provided by a simple bean object (no conversion is
 * needed). As "ogham-template-thymeleaf" is used and the template is a
 * Thymeleaf template (Thymeleaf directive on &lt;html&gt; tag), this template
 * is parse by Thymeleaf</li>
 * <li>The text content comes from a template located in the classpath
 * (email/sample.txt.ftl) and variables that are present in the template are
 * replaced by values provided by a simple bean object (no conversion is
 * needed). As "ogham-template-freemarker" is used and the template is a
 * Freemarker template (".ftl" extension), this template is parse by
 * Thymeleaf</li>
 * </ul>
 * </li>
 * <li>The HTML template has CSS styles that are inlined (CSS are forbidden by
 * many email clients but inlined styles attributes are allowed)</li>
 * <li>The HTML template references images (using &lt;img&gt;) that are
 * automatically attached to the sent email (mimetype of each image has been
 * detected and indicated to the sent message)</li>
 * <li>A file located on the filesystem is attached to the email, its mimetype
 * has been automatically detected and indicated to the sent message</li>
 * <li>The subject is provided by the &lt;title&gt; tag of the HTML</li>
 * <li>The sender email address is provided by the system property
 * "ogham.email.from.default-value"</li>
 * </ul>
 * 
 * <p>
 * Here is an example of minimal configuration that provides same default
 * behavior as previous exemple but no sender implementation is registered. You
 * then have to enable implementation(s) and configure them:
 * 
 * <pre>
 * <code>
 * // Instantiate the messaging service
 * MessagingService service = MessagingBuilder.minimal()
 *   .email()
 *     .sender(JavaMailBuilder.class)
 *       .host().properties("${mail.host}").and()
 *       .port().properties("${mail.port}").and()
 *       .charset().properties("${ogham.email.javamail.body.charset}").defaultValue("UTF-8").and()
 *       .mimetype()
 *         .tika()
 *           .failIfOctetStream(false)
 *           .and()
 *         .and()
 *       .and()
 *     .and()
 *   .build();
 * // send the email
 * service.send(new Email()
 *   .content(new MultiTemplateContent("email/sample", new SampleBean("foo", 42)))
 *   .to("Foo Bar &lt;foo.bar@sii.fr&gt;"))
 *   .attach(new Attachment("file:/data/reports/report1.pdf"));
 * </code>
 * </pre>
 * 
 * This sample has the same effect as the previous one (for this case, some
 * options of JavaMail implementation are not enabled). Moreover, if you want to
 * send a {@link Sms}, you will also need to register and configure at least one
 * SMS sender implementation.
 * 
 * To benefit of all advantages but keeping control on which implementations are
 * use, you can register implementation configurers:
 * 
 * <pre>
 * <code>
 * // Instantiate the messaging service
 * MessagingService service = MessagingBuilder.minimal()
 *   .register(new DefaultJavaMailConfigurer(), 50000)     // enable sending Email through SMTP
 *   .register(new DefaultSendGridConfigurer(), 30000)     // enable sending Email through HTTP (using an online service)
 *   .register(new DefaultCloudHopperConfigurer(), 40000)  // enable sending SMS through SMPP
 *   .register(new DefaultOvhSmsConfigurer(), 20000)     // enable sending SMS through HTTP (using an online service)
 *   .build();
 * // send the email
 * service.send(new Email()
 *   .content(new MultiTemplateContent("email/sample", new SampleBean("foo", 42)))
 *   .to("Foo Bar &lt;foo.bar@sii.fr&gt;"))
 *   .attach(new Attachment("file:/data/reports/report1.pdf"));
 * </code>
 * </pre>
 * 
 * This sample has the same effect as using {@link #standard()}.
 * 
 * You can create your own configurer for any sender implementation and register
 * it too. You can then mutualize and externalize reusable configuration.
 * 
 * <p>
 * The predefined behaviors are brought by static factory methods
 * {@link #standard()} and {@link #minimal()}. If you don't want to use
 * predefined behaviors, you can use directly {@code new MessagingBuilder()} or
 * {@link #empty()} static factory.
 * 
 * Those factory methods rely on {@link MessagingConfigurer}s to provide
 * predefined behaviors.
 * 
 * <p>
 * The default behaviors can be used and customized:
 * 
 * <pre>
 * <code>
 * // Instantiate the messaging service
 * MessagingService service = MessagingBuilder.standard()
 *   .environment()
 *     .properties()
 *       .set("mail.host", "localhost")
 *       .set("mail.port", "25")
 *       .and()
 *     .and()
 *   .mimetype()
 *     .defaultMimetype("application/octet-stream")
 *     .and()
 *   .build();
 * // send the email
 * service.send(new Email()
 *   .content(new MultiTemplateContent("email/sample", new SampleBean("foo", 42)))
 *   .to("Foo Bar &lt;foo.bar@sii.fr&gt;"))
 *   .attach(new Attachment("file:/data/reports/report1.pdf"));
 * </code>
 * </pre>
 * 
 * The previous sample shows how to change default behaviors to fit your needs:
 * <ul>
 * <li>It set the SMTP host and port in the code not by using system
 * properties</li>
 * <li>It overrides default mimetype to provide a mimetype that fit your needs
 * when mimetype detection is not enough accurate</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class MessagingBuilder implements Builder<MessagingService> {
	private static final Logger LOG = LoggerFactory.getLogger(MessagingBuilder.class);
	private static final String BASE_PACKAGE = "fr.sii.ogham";

	protected final boolean autoconfigure;
	protected final Map<ConfigurationPhase, Boolean> alreadyConfigured;
	protected final PriorizedList<ConfigurerWithPhase> configurers;
	protected final EnvironmentBuilder<MessagingBuilder> environmentBuilder;
	protected final BuildContext buildContext;
	protected MimetypeDetectionBuilder<MessagingBuilder> mimetypeBuilder;
	protected StandaloneResourceResolutionBuilder<MessagingBuilder> resourceBuilder;
	protected EmailBuilder emailBuilder;
	protected SmsBuilder smsBuilder;
	protected final ConfigurationValueBuilderHelper<MessagingBuilder, Boolean> wrapUncaughtValueBuilder;

	/**
	 * Initializes the builder with minimal requirements:
	 * <ul>
	 * <li>an empty {@link EnvironmentBuilder}</li>
	 * <li>an empty {@link MimetypeDetectionBuilder}</li>
	 * <li>an empty {@link ResourceResolutionBuilder}</li>
	 * </ul>
	 * 
	 * 
	 * <p>
	 * If {@code autoconfigure} parameter is true, it applies all registered
	 * configurers on this builder instance.
	 * 
	 * When using {@link #standard()} and {@link #minimal()} factory methods,
	 * {@code autoconfigure} parameter is set to true.
	 * 
	 * 
	 * @param autoconfigure
	 *            Trigger configuration automatically if true (for all phases).
	 *            If false, you have to call
	 *            {@link #configure(ConfigurationPhase)} manually.
	 */
	public MessagingBuilder(boolean autoconfigure) {
		super();
		this.autoconfigure = autoconfigure;
		alreadyConfigured = new EnumMap<>(ConfigurationPhase.class);
		configurers = new PriorizedList<>();
		environmentBuilder = new SimpleEnvironmentBuilder<>(this);
		buildContext = new EnvBuilderBasedContext(environmentBuilder);
		mimetypeBuilder = new SimpleMimetypeDetectionBuilder<>(this, buildContext);
		resourceBuilder = new StandaloneResourceResolutionBuilder<>(this, buildContext);
		wrapUncaughtValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class, buildContext);
	}

	/**
	 * Registers a configurer with a priority. Configuration order may be
	 * important. The priority is used to apply configurers in order. The
	 * configurer with highest priority (applied first) has the greatest value.
	 * 
	 * The configurer is applied on a this builder instance to configure it
	 * (when {@link #configure(ConfigurationPhase)} is called).
	 * 
	 * <p>
	 * When using {@link #standard()} and {@link #minimal()} factory methods,
	 * the list of configurers are automatically loaded from the classpath and
	 * registered. The priority is indicated through the {@link ConfigurerFor}
	 * annotation.
	 * 
	 * <p>
	 * The registered configurer will be executed at
	 * {@link ConfigurationPhase#BEFORE_BUILD} phase.
	 * 
	 * 
	 * @param configurer
	 *            the configurer to register
	 * @param priority
	 *            the configurer priority
	 * @return this instance for fluent chaining
	 */
	public MessagingBuilder register(MessagingConfigurer configurer, int priority) {
		return register(configurer, priority, ConfigurationPhase.BEFORE_BUILD);
	}

	/**
	 * Registers a configurer with a priority. Configuration order may be
	 * important. The priority is used to apply configurers in order. The
	 * configurer with highest priority (applied first) has the greatest value.
	 * 
	 * The configurer is applied on a this builder instance to configure it
	 * (when {@link #configure(ConfigurationPhase)} is called).
	 * 
	 * <p>
	 * When using {@link #standard()} and {@link #minimal()} factory methods,
	 * the list of configurers are automatically loaded from the classpath and
	 * registered. The priority is indicated through the {@link ConfigurerFor}
	 * annotation.
	 * 
	 * 
	 * @param configurer
	 *            the configurer to register
	 * @param priority
	 *            the configurer priority
	 * @param phase
	 *            register the configurer to be executed at the defined the
	 *            configuration phase
	 * @return this instance for fluent chaining
	 */
	public MessagingBuilder register(MessagingConfigurer configurer, int priority, ConfigurationPhase phase) {
		LOG.debug("[{}] registered for phase {} with priority={}", configurer, phase, priority);
		configurers.register(new ConfigurerWithPhase(configurer, phase), priority);
		return this;
	}

	/**
	 * Apply all registered configurers on this builder instance for the
	 * {@link ConfigurationPhase}.
	 * 
	 * <p>
	 * When using {@link #standard()} and {@link #minimal()} factory methods,
	 * this method is automatically called.
	 * 
	 * @param phase
	 *            the configuration phase
	 */
	public void configure(ConfigurationPhase phase) {
		if (alreadyConfigured(phase)) {
			return;
		}
		for (ConfigurerWithPhase configurerWithPhase : configurers.getOrdered()) {
			if (phase == configurerWithPhase.getPhase()) {
				LOG.debug("[{}] configuring for phase {}...", configurerWithPhase.getConfigurer(), phase);
				configurerWithPhase.getConfigurer().configure(this);
			}
		}
		alreadyConfigured.put(phase, true);
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

	/**
	 * Cconfigures resource resolution.
	 * 
	 * <p>
	 * Resource resolution consists of finding a file:
	 * <ul>
	 * <li>either on filesystem</li>
	 * <li>or in the classpath</li>
	 * <li>or anywhere else</li>
	 * </ul>
	 * 
	 * <p>
	 * To identify which resolution to use, each resolution is configured to
	 * handle one or several lookups prefixes. For example, if resolution is
	 * configured like this:
	 * 
	 * <pre>
	 * <code>
	 * .string()
	 *   .lookup("string:", "s:")
	 *   .and()
	 * .file()
	 *   .lookup("file:")
	 *   .and()
	 * .classpath()
	 *   .lookup("classpath:", "");
	 * </code>
	 * </pre>
	 * 
	 * Then you can reference a file that is in the classpath like this:
	 * 
	 * <pre>
	 * "classpath:foo/bar.html"
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Resource resolution is also able to handle path prefix and suffix. The
	 * aim is for example to have a folder that contains all templates. The
	 * developer then configures a path prefix for the folder. He can also
	 * configure a suffix to fix extension for templates. Thanks to those
	 * prefix/suffix, templates can now be referenced by the name of the file
	 * (without extension). It is useful to reference a template independently
	 * from where it is in reality (classpath, file or anywhere else) .
	 * Switching from classpath to file and conversely can be done easily (by
	 * updating the lookup).
	 * 
	 * For example:
	 * 
	 * <pre>
	 * .classpath().lookup("classpath:").pathPrefix("foo/").pathSuffix(".html");
	 * 
	 * resourceResolver.getResource("classpath:bar");
	 * </pre>
	 * 
	 * The real path is then {@code foo/bar.html}.
	 * 
	 * <p>
	 * This implementation is used by {@link MessagingBuilder} for general
	 * configuration. That configuration may be inherited (applied to other
	 * resource resolution builders).
	 * 
	 * @return the builder to configure resource resolution
	 */
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

	/**
	 * There are technical exceptions that are thrown by libraries used by
	 * Ogham. Those exceptions are often {@link RuntimeException}s. It can be
	 * difficult for developers of a big application to quickly identify what
	 * caused this {@link RuntimeException}. The stack trace doesn't always help
	 * to find the real source of the error. If enables, this option ensures
	 * that work done by Ogham will always throw a {@link MessagingException}
	 * even if it was a {@link RuntimeException} thrown by any component. It
	 * then helps the developer to know that the error comes from Ogham or a any
	 * used library and not something else in its application. The other benefit
	 * is that in your code you only catch a {@link MessagingException} and you
	 * are sure that it will handle all cases, no surprise with an unchecked
	 * exception that could make a big failure in your system because you didn't
	 * know this could happen. Sending a message is often not critical (if
	 * message can't be sent now, it can be sent later or manually). It it fails
	 * the whole system must keep on working. With this option enabled, your
	 * system will never fail due to an unchecked exception and you can handle
	 * the failure the same way as with checked exceptions.
	 * 
	 * Concretely, call of
	 * {@link MessagingService#send(fr.sii.ogham.core.message.Message)} catches
	 * all exceptions including {@link RuntimeException}. It wraps any
	 * exceptions into a {@link MessagingException}.
	 * 
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #wrapUncaught()}.
	 * 
	 * <pre>
	 * .wrapUncaught(false)
	 * .wrapUncaught()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <pre>
	 * .wrapUncaught(false)
	 * .wrapUncaught()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * In both cases, {@code wrapUncaught(false)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param enable
	 *            enable or disable catching of unchecked exceptions
	 * @return this instance for fluent chaining
	 */
	public MessagingBuilder wrapUncaught(Boolean enable) {
		wrapUncaughtValueBuilder.setValue(enable);
		return this;
	}

	/**
	 * There are technical exceptions that are thrown by libraries used by
	 * Ogham. Those exceptions are often {@link RuntimeException}s. It can be
	 * difficult for developers of a big application to quickly identify what
	 * caused this {@link RuntimeException}. The stack trace doesn't always help
	 * to find the real source of the error. If enables, this option ensures
	 * that work done by Ogham will always throw a {@link MessagingException}
	 * even if it was a {@link RuntimeException} thrown by any component. It
	 * then helps the developer to know that the error comes from Ogham or a any
	 * used library and not something else in its application. The other benefit
	 * is that in your code you only catch a {@link MessagingException} and you
	 * are sure that it will handle all cases, no surprise with an unchecked
	 * exception that could make a big failure in your system because you didn't
	 * know this could happen. Sending a message is often not critical (if
	 * message can't be sent now, it can be sent later or manually). It it fails
	 * the whole system must keep on working. With this option enabled, your
	 * system will never fail due to an unchecked exception and you can handle
	 * the failure the same way as with checked exceptions.
	 * 
	 * Concretely, call of
	 * {@link MessagingService#send(fr.sii.ogham.core.message.Message)} catches
	 * all exceptions including {@link RuntimeException}. It wraps any
	 * exceptions into a {@link MessagingException}.
	 * 
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .wrapUncaught()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #wrapUncaught(Boolean)} takes precedence
	 * over property values and default value.
	 * 
	 * <pre>
	 * .wrapUncaught(false)
	 * .wrapUncaught()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * The value {@code false} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<MessagingBuilder, Boolean> wrapUncaught() {
		return wrapUncaughtValueBuilder;
	}

	/**
	 * Configures how to send {@link Email} messages. It allows to:
	 * <ul>
	 * <li>register and configure several sender implementations</li>
	 * <li>register and configure several template engines for parsing templates
	 * as message content</li>
	 * <li>configure handling of missing {@link Email} information</li>
	 * <li>configure handling of file attachments</li>
	 * <li>configure CSS and image handling for {@link Email}s with an HTML
	 * body</li>
	 * </ul>
	 * 
	 * You can send an {@link Email} using the minimal behavior and using
	 * JavaMail implementation:
	 * 
	 * <pre>
	 * <code>
	 * // Instantiate the messaging service
	 * MessagingService service = new MessagingBuilder()
	 *   .email()
	 *     .sender(JavaMailBuilder.class)   // enable Email sending using JavaMail
	 *       .host("your SMTP server host")
	 *       .port("your SMTP server port")
	 *       .and()
	 *     .and()
	 *   .build();
	 * // send the email
	 * service.send(new Email()
	 *   .from("sender email address")
	 *   .subject("email subject")
	 *   .content("email body")
	 *   .to("recipient email address"));
	 * </code>
	 * </pre>
	 * 
	 * You can also send an {@link Email} using a template (using Freemarker for
	 * example):
	 * 
	 * The Freemarker template ("email/sample.html.ftl"):
	 * 
	 * <pre>
	 * &lt;html&gt;
	 * &lt;head&gt;
	 * &lt;/head&gt;
	 * &lt;body&gt;
	 * Email content with variables: ${name} ${value}
	 * &lt;/body&gt;
	 * &lt;/html&gt;
	 * </pre>
	 * 
	 * Then you can send the {@link Email} like this:
	 * 
	 * <pre>
	 * <code>
	 * // Instantiate the messaging service
	 * MessagingService service = new MessagingBuilder()
	 *   .email()
	 *     .sender(JavaMailBuilder.class)   // enable Email sending using JavaMail
	 *       .host("your SMTP server host")
	 *       .port("your SMTP server port")
	 *       .and()
	 *     .and()
	 *   .template(FreemarkerEmailBuilder.class)  // enable templating using Freemarker
	 *     .classpath()
	 *       .lookup("classpath:")   // search resources/templates in the classpath if a path is prefixed by "classpath:"
	 *       .and()
	 *     .and()
	 *   .build();
	 * // send the email
	 * service.send(new Email()
	 *   .from("sender email address")
	 *   .subject("email subject")
	 *   .content(new TemplateContent("classpath:email/sample.html.ftl", new SampleBean("foo", 42)))
	 *   .to("recipient email address"));
	 * </code>
	 * </pre>
	 * 
	 * <p>
	 * Instead of explicitly configures SMTP host and port in your code, it
	 * could be better to externalize the configuration in a properties file for
	 * example (for example a file named "email.properties" in the classpath).
	 * The previous example becomes:
	 * 
	 * <pre>
	 * <code>
	 * // Instantiate the messaging service
	 * MessagingService service = new MessagingBuilder()
	 *   .environment()
	 *     .properties("email.properties")
	 *     .and()
	 *   .email()
	 *     .sender(JavaMailBuilder.class)   // enable Email sending using JavaMail
	 *       .host().properties("${mail.host}").and()
	 *       .port().properties("${mail.port}").and()
	 *       .and()
	 *     .and()
	 *   .template(FreemarkerEmailBuilder.class)  // enable templating using Freemarker
	 *     .classpath()
	 *       .lookup("classpath:")   // search resources/templates in the classpath if a path is prefixed by "classpath:"
	 *       .and()
	 *     .and()
	 *   .build();
	 * // send the email
	 * service.send(new Email()
	 *   .from("sender email address")
	 *   .subject("email subject")
	 *   .content(new TemplateContent("classpath:email/sample.html.ftl", new SampleBean("foo", 42)))
	 *   .to("recipient email address"));
	 * </code>
	 * </pre>
	 * 
	 * The content of the file "email.properties":
	 * 
	 * <pre>
	 * mail.host=your STMP server host
	 * mail.port=your STMP server port
	 * </pre>
	 * 
	 * 
	 * Some fields of the Email may be automatically filled by a default value
	 * if they are not defined. For example, the sender address could be
	 * configured only once for your application:
	 * 
	 * <pre>
	 * <code>
	 * // Instantiate the messaging service
	 * MessagingService service = new MessagingBuilder()
	 *   .environment()
	 *     .properties("email.properties")
	 *     .and()
	 *   .email()
	 *     .sender(JavaMailBuilder.class)   // enable Email sending using JavaMail
	 *       .host().properties("${mail.host}").and()
	 *       .port().properties("${mail.port}").and()
	 *       .and()
	 *     .autofill()    // enables and configures autofilling
	 *       .from()
	 *         .defaultValue().properties("${email.sender.address}").and()
	 *         .and()
	 *     .and()
	 *   .template(FreemarkerEmailBuilder.class)  // enable templating using Freemarker
	 *     .classpath()
	 *       .lookup("classpath:")   // search resources/templates in the classpath if a path is prefixed by "classpath:"
	 *       .and()
	 *     .and()
	 *   .build();
	 * // send the email (now the sender address can be omitted)
	 * service.send(new Email()
	 *   .subject("email subject")
	 *   .content(new TemplateContent("classpath:email/sample.html.ftl", new SampleBean("foo", 42)))
	 *   .to("recipient email address"));
	 * </code>
	 * </pre>
	 * 
	 * The content of the file "email.properties":
	 * 
	 * <pre>
	 * mail.host=your STMP server host
	 * mail.port=your STMP server port
	 * email.sender.address=sender email address
	 * </pre>
	 * 
	 * 
	 * 
	 * Another very useful automatic filling is for providing the email subject:
	 * 
	 * <pre>
	 * <code>
	 * // Instantiate the messaging service
	 * MessagingService service = new MessagingBuilder()
	 *   .environment()
	 *     .properties("email.properties")
	 *     .and()
	 *   .email()
	 *     .sender(JavaMailBuilder.class)   // enable Email sending using JavaMail
	 *       .host().properties("${mail.host}").and()
	 *       .port().properties("${mail.port}").and()
	 *       .and()
	 *     .autofill()    // enables and configures autofilling
	 *       .from()
	 *         .defaultValue().properties("${email.sender.address}").and()
	 *         .and()
	 *       .subject()
	 *         .htmlTitle(true)    // enables use of html title tag as subject
	 *     .and()
	 *   .template(FreemarkerEmailBuilder.class)  // enable templating using Freemarker
	 *     .classpath()
	 *       .lookup("classpath:")   // search resources/templates in the classpath if a path is prefixed by "classpath:"
	 *       .and()
	 *     .and()
	 *   .build();
	 * // send the email (now the subject can be omitted)
	 * service.send(new Email()
	 *   .content(new TemplateContent("classpath:email/sample.html.ftl", new SampleBean("foo", 42)))
	 *   .to("recipient email address"));
	 * </code>
	 * </pre>
	 * 
	 * Change your template:
	 * 
	 * <pre>
	 * &lt;html&gt;
	 * &lt;head&gt;
	 *   &lt;title&gt;email subject - ${name}&lt;/title&gt;
	 * &lt;/head&gt;
	 * &lt;body&gt;
	 * Email content with variables: ${name} ${value}
	 * &lt;/body&gt;
	 * &lt;/html&gt;
	 * </pre>
	 * 
	 * The obvious advantage is that you have a single place to handle email
	 * content (body + subject). There is another benefit: you can also use
	 * variables in the subject.
	 * 
	 * 
	 * There many other configuration possibilities:
	 * <ul>
	 * <li>for configuring {@link Email}s with HTML content with a text fallback
	 * (useful for smartphones preview of your email for example)</li>
	 * <li>for configuring attachments handling</li>
	 * <li>for configuring image and css handling</li>
	 * </ul>
	 * 
	 * <p>
	 * All the previous examples are provided to understand what can be
	 * configured. Hopefully, Ogham provides auto-configuration with a default
	 * behavior that fits 95% of usages. This auto-configuration is provided by
	 * {@link MessagingConfigurer}s. Those configurers are automatically applied
	 * when using predefined {@link MessagingBuilder}s like
	 * {@link MessagingBuilder#minimal()} and
	 * {@link MessagingBuilder#standard()}.
	 * 
	 * The previous sample using standard configuration becomes:
	 * 
	 * <pre>
	 * <code>
	 * // Instantiate the messaging service
	 * MessagingService service = new MessagingBuilder()
	 *   .environment()
	 *     .properties("email.properties")
	 *     .and()
	 *   .build();
	 * // send the email
	 * service.send(new Email()
	 *   .content(new TemplateContent("classpath:email/sample.html.ftl", new SampleBean("foo", 42)))
	 *   .to("recipient email address"));
	 * </code>
	 * </pre>
	 * 
	 * The new content of the file "email.properties":
	 * 
	 * <pre>
	 * mail.host=your STMP server host
	 * mail.port=your STMP server port
	 * ogham.email.from.default-value=sender email address
	 * </pre>
	 * 
	 * <p>
	 * You can also use the auto-configuration for benefit from default
	 * behaviors and override some behaviors for your needs:
	 * 
	 * <pre>
	 * <code>
	 * // Instantiate the messaging service
	 * MessagingService service = new MessagingBuilder()
	 *   .environment()
	 *     .properties("email.properties")
	 *     .and()
	 *   .email()
	 *     .autofill()
	 *       .from()
	 *         .defaultValue().properties("${email.sender.address}").and()   // overrides default sender email address property
	 *         .and()
	 *       .and()
	 *     .and()
	 *   .build();
	 * // send the email
	 * service.send(new Email()
	 *   .content(new TemplateContent("classpath:email/sample.html.ftl", new SampleBean("foo", 42)))
	 *   .to("recipient email address"));
	 * </code>
	 * </pre>
	 * 
	 * The new content of the file "email.properties":
	 * 
	 * <pre>
	 * mail.host=your STMP server host
	 * mail.port=your STMP server port
	 * email.sender.address=sender email address
	 * </pre>
	 * 
	 * 
	 * @return the builder to configure how Email are handled
	 */
	public EmailBuilder email() {
		if (emailBuilder == null) {
			emailBuilder = new EmailBuilder(this, buildContext);
		}
		return emailBuilder;
	}

	/**
	 * Configures how to send {@link Sms} messages. It allows to:
	 * <ul>
	 * <li>register and configure several sender implementations</li>
	 * <li>register and configure several template engines for parsing templates
	 * as message content</li>
	 * <li>configure handling of missing {@link Sms} information</li>
	 * <li>configure number format handling</li>
	 * </ul>
	 * 
	 * You can send a {@link Sms} using the minimal behavior and using
	 * Cloudhopper implementation:
	 * 
	 * <pre>
	 * <code>
	 * // Instantiate the messaging service
	 * MessagingService service = new MessagingBuilder()
	 *   .sms()
	 *     .sender(CloudhopperBuilder.class)   // enable SMS sending using Cloudhopper
	 *       .host("your SMPP server host")
	 *       .port("your SMPP server port")
	 *       .systemId("your SMPP system_id")
	 *       .password("an optional password")
	 *       .and()
	 *     .and()
	 *   .build();
	 * // send the sms
	 * service.send(new Sms()
	 *   .from("sender phone number")
	 *   .content("sms content")
	 *   .to("recipient phone number"));
	 * </code>
	 * </pre>
	 * 
	 * You can also send a {@link Sms} using a template (using Freemarker for
	 * example):
	 * 
	 * The Freemarker template ("sms/sample.txt.ftl"):
	 * 
	 * <pre>
	 * Sms content with variables: ${name} ${value}
	 * </pre>
	 * 
	 * Then you can send the {@link Sms} like this:
	 * 
	 * <pre>
	 * <code>
	 * // Instantiate the messaging service
	 * MessagingService service = new MessagingBuilder()
	 *   .sms()
	 *     .sender(CloudhopperBuilder.class)   // enable SMS sending using Cloudhopper
	 *       .host("your SMPP server host")
	 *       .port("your SMPP server port")
	 *       .systemId("your SMPP system_id")
	 *       .password("an optional password")
	 *       .and()
	 *     .and()
	 *   .template(FreemarkerSmsBuilder.class)  // enable templating using Freemarker
	 *     .classpath()
	 *       .lookup("classpath:")   // search resources/templates in the classpath if a path is prefixed by "classpath:"
	 *       .and()
	 *     .and()
	 *   .build();
	 * // send the sms
	 * service.send(new Sms()
	 *   .from("sender phone number")
	 *   .content(new TemplateContent("classpath:sms/sample.txt.ftl", new SampleBean("foo", 42)))
	 *   .to("recipient phone number"));
	 * </code>
	 * </pre>
	 * 
	 * <p>
	 * Instead of explicitly configures SMPP host/port/system_id/password in
	 * your code, it could be better to externalize the configuration in a
	 * properties file for example (for example a file named "sms.properties" in
	 * the classpath). The previous example becomes:
	 * 
	 * <pre>
	 * <code>
	 * // Instantiate the messaging service
	 * MessagingService service = new MessagingBuilder()
	 *   .environment()
	 *     .properties("sms.properties")
	 *     .and()
	 *   .sms()
	 *     .sender(CloudhopperBuilder.class)   // enable SMS sending using Cloudhopper
	 *       .host().properties("${smpp.host}").and()
	 *       .port().properties("${smpp.port}").and()
	 *       .systemId().properties("${smpp.system-id}").and()
	 *       .password().properties("${smpp.password}").and()
	 *       .and()
	 *     .and()
	 *   .template(FreemarkerSmsBuilder.class)  // enable templating using Freemarker
	 *     .classpath()
	 *       .lookup("classpath:")   // search resources/templates in the classpath if a path is prefixed by "classpath:"
	 *       .and()
	 *     .and()
	 *   .build();
	 * // send the sms
	 * service.send(new Sms()
	 *   .from("sender phone number")
	 *   .content(new TemplateContent("classpath:sms/sample.txt.ftl", new SampleBean("foo", 42)))
	 *   .to("recipient phone number"));
	 * </code>
	 * </pre>
	 * 
	 * The content of the file "sms.properties":
	 * 
	 * <pre>
	 * smpp.host=your SMPP server host
	 * smpp.port=your SMPP server port
	 * smpp.system-id=your SMPP system_id
	 * smpp.password=an optional password
	 * </pre>
	 * 
	 * 
	 * Some fields of the SMS may be automatically filled by a default value if
	 * they are not defined. For example, the sender phone number could be
	 * configured only once for your application:
	 * 
	 * <pre>
	 * <code>
	 * // Instantiate the messaging service
	 * MessagingService service = new MessagingBuilder()
	 *   .environment()
	 *     .properties("sms.properties")
	 *     .and()
	 *   .sms()
	 *     .sender(CloudhopperBuilder.class)   // enable SMS sending using Cloudhopper
	 *       .host().properties("${smpp.host}").and()
	 *       .port().properties("${smpp.port}").and()
	 *       .systemId().properties("${smpp.system-id}").and()
	 *       .password().properties("${smpp.password}").and()
	 *       .and()
	 *     .autofill()    // enables and configures autofilling
	 *       .from()
	 *         .defaultValue().properties("${sms.sender.number}").and()
	 *         .and()
	 *       .and()
	 *     .and()
	 *   .template(FreemarkerSmsBuilder.class)  // enable templating using Freemarker
	 *     .classpath()
	 *       .lookup("classpath:")   // search resources/templates in the classpath if a path is prefixed by "classpath:"
	 *       .and()
	 *     .and()
	 *   .build();
	 * // send the sms (now the sender phone number can be omitted)
	 * service.send(new Sms()
	 *   .content(new TemplateContent("classpath:sms/sample.txt.ftl", new SampleBean("foo", 42)))
	 *   .to("recipient phone number"));
	 * </code>
	 * </pre>
	 * 
	 * The new content of the file "sms.properties":
	 * 
	 * <pre>
	 * smpp.host=your SMPP server host
	 * smpp.port=your SMPP server port
	 * smpp.system-id=your SMPP system_id
	 * smpp.password=an optional password
	 * sms.sender.number=the sender phone number
	 * </pre>
	 * 
	 * <p>
	 * All the previous examples are provided to understand what can be
	 * configured. Hopefully, Ogham provides auto-configuration with a default
	 * behavior that fits 95% of usages. This auto-configuration is provided by
	 * {@link MessagingConfigurer}s. Those configurers are automatically applied
	 * when using predefined {@link MessagingBuilder}s like
	 * {@link MessagingBuilder#minimal()} and
	 * {@link MessagingBuilder#standard()}.
	 * 
	 * The previous sample using standard configuration becomes:
	 * 
	 * <pre>
	 * <code>
	 * // Instantiate the messaging service
	 * MessagingService service = MessagingBuilder.standard()
	 *   .environment()
	 *     .properties("sms.properties")
	 *     .and()
	 *   .build();
	 * // send the sms
	 * service.send(new Sms()
	 *   .content(new TemplateContent("classpath:sms/sample.txt.ftl", new SampleBean("foo", 42)))
	 *   .to("recipient phone number"));
	 * </code>
	 * </pre>
	 * 
	 * The new content of the file "sms.properties":
	 * 
	 * <pre>
	 * ogham.sms.smpp.host=your SMPP server host
	 * ogham.sms.smpp.port=your SMPP server port
	 * ogham.sms.smpp.system-id=your SMPP system_id
	 * ogham.sms.smpp.password=an optional password
	 * ogham.sms.from.default-value=the sender phone number
	 * </pre>
	 * 
	 * <p>
	 * You can also use the auto-configuration for benefit from default
	 * behaviors and override some behaviors for your needs:
	 * 
	 * <pre>
	 * <code>
	 * // Instantiate the messaging service
	 * MessagingService service = MessagingBuilder.standard()
	 *   .environment()
	 *     .properties("sms.properties")
	 *     .and()
	 *   .sms()
	 *     .autofill()
	 *       .from()
	 *         .defaultValue().properties("${sms.sender.number}").and()   // overrides default sender phone number property
	 *         .and()
	 *       .and()
	 *     .and()
	 *   .build();
	 * // send the sms
	 * service.send(new Sms()
	 *   .content(new TemplateContent("classpath:sms/sample.txt.ftl", new SampleBean("foo", 42)))
	 *   .to("recipient phone number"));
	 * </code>
	 * </pre>
	 * 
	 * The new content of the file "sms.properties":
	 * 
	 * <pre>
	 * ogham.sms.smpp.host=your SMPP server host
	 * ogham.sms.smpp.port=your SMPP server port
	 * ogham.sms.smpp.system-id=your SMPP system_id
	 * ogham.sms.smpp.password=an optional password
	 * sms.sender.number=the sender phone number
	 * </pre>
	 * 
	 * @return the builder to configure how SMS are handled
	 */
	public SmsBuilder sms() {
		if (smsBuilder == null) {
			smsBuilder = new SmsBuilder(this, buildContext);
		}
		return smsBuilder;
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
	@SuppressWarnings("squid:S5411")
	public MessagingService build() {
		if (autoconfigure) {
			configure(ConfigurationPhase.BEFORE_BUILD);
		}
		LOG.info("Using service that calls all registered senders");
		List<ConditionalSender> senders = buildSenders();
		LOG.debug("Registered senders: {}", senders);
		MessagingService service = new EverySupportingMessagingService(senders);
		if (wrapUncaughtValueBuilder.getValue(false)) {
			service = new WrapExceptionMessagingService(service);
		}
		return service;
	}

	/**
	 * Static factory method that initializes a {@link MessagingBuilder}
	 * instance with no auto-configuration at all.
	 * 
	 * @return the empty builder that provides no behavior at all that needs to
	 *         be configured
	 */
	public static MessagingBuilder empty() {
		return new MessagingBuilder(false);
	}

	/**
	 * Static factory method that initializes a {@link MessagingBuilder}
	 * instance and auto-configures it with a predefined behavior named
	 * "standard".
	 * 
	 * Usage example:
	 * 
	 * <pre>
	 * <code>
	 * MessagingService service = MessagingBuilder.standard()
	 *   .environment()
	 *     .properties("application.properties")
	 *     .and()
	 *   .build();
	 * </code>
	 * </pre>
	 * 
	 * <p>
	 * Basically, the standard behavior:
	 * <ul>
	 * <li>Enables all template engines that are present in the classpath and
	 * configures them</li>
	 * <li>Enables all {@link Email} sender implementations that are present in
	 * the classpath and configures them</li>
	 * <li>Enables all {@link Sms} sender implementations that are present in
	 * the classpath and configures them</li>
	 * <li>Catches all uncaught exception to wrap them in order to avoid
	 * unwanted unchecked exception</li>
	 * <li>Uses system properties</li>
	 * <li>Enables and configures useful auto-filling mechanisms (using property
	 * values and providing email subject from templates)</li>
	 * <li>Enables mimetype detection</li>
	 * <li>Enables locating templates, css, images and all other resources using
	 * lookup prefix ("file:" for files that are present on the filesystem,
	 * "classpath:" and "" for files that are present in the classpath)</li>
	 * <li>Enables use of some properties to provide path prefix/suffix for
	 * locating resources</li>
	 * <li>Enables images and css inlining used by HTML {@link Email}s</li>
	 * </ul>
	 * 
	 * <p>
	 * The auto-configurers ( {@link MessagingConfigurer}s) are automatically
	 * loaded from the classpath. Only configurers that are in "fr.sii.ogham"
	 * package and sub-packages are loaded. If you want to load from other
	 * packages, use {@link #standard(String...)}. Some Ogham modules are
	 * optional meaning that according to used modules, the behavior will vary.
	 * 
	 * Loaded {@link MessagingConfigurer}s are applied to the
	 * {@link MessagingBuilder} only if they are for the "standard" builder. It
	 * is accomplished thanks to the {@link ConfigurerFor} annotation (only
	 * configurers annotated and with {@link ConfigurerFor#targetedBuilder()}
	 * set to "standard").
	 * 
	 * Loaded configurers with priorities are (if all Ogham modules are used):
	 * <ul>
	 * <li><code>DefaultMessagingConfigurer</code>: 100000</li>
	 * <li><code>DefaultThymeleafEmailConfigurer</code>: 90000</li>
	 * <li><code>DefaultFreemarkerEmailConfigurer</code>: 80000</li>
	 * <li><code>DefaultThymeleafSmsConfigurer</code>: 70000</li>
	 * <li><code>DefaultFreemarkerSmsConfigurer</code>: 60000</li>
	 * <li><code>DefaultJavaMailConfigurer</code>: 50000</li>
	 * <li><code>DefaultCloudhopperConfigurer</code>: 40000</li>
	 * <li><code>DefaultSendGridConfigurer</code>: 30000</li>
	 * <li><code>DefaultOvhSmsConfigurer</code>: 20000</li>
	 * </ul>
	 * 
	 * TODO: link to whole configuration that is applied by standard
	 * 
	 * <p>
	 * The auto-configured {@link MessagingBuilder} will provide a default
	 * behavior that fits 95% of usages. You can still override some behaviors
	 * for your needs.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * <code>
	 * MessagingService service = MessagingBuilder.standard()
	 *   .environment()
	 *     .properties("application.properties")
	 *     .and()
	 *   .wrapUncaught(false)    // overrides and disables wrapUncaught option
	 *   .build();
	 * </code>
	 * </pre>
	 * 
	 * @return the messaging builder that can be customized
	 */
	public static MessagingBuilder standard() {
		return standard(BASE_PACKAGE);
	}

	/**
	 * Static factory method that initializes a {@link MessagingBuilder}
	 * instance and auto-configures it with a predefined behavior named
	 * "standard".
	 * 
	 * Usage example:
	 * 
	 * <pre>
	 * <code>
	 * MessagingService service = MessagingBuilder.standard()
	 *   .environment()
	 *     .properties("application.properties")
	 *     .and()
	 *   .build();
	 * </code>
	 * </pre>
	 * 
	 * <p>
	 * Basically, the standard behavior:
	 * <ul>
	 * <li>Enables all template engines that are present in the classpath and
	 * configures them</li>
	 * <li>Enables all {@link Email} sender implementations that are present in
	 * the classpath and configures them</li>
	 * <li>Enables all {@link Sms} sender implementations that are present in
	 * the classpath and configures them</li>
	 * <li>Catches all uncaught exception to wrap them in order to avoid
	 * unwanted unchecked exception</li>
	 * <li>Uses system properties</li>
	 * <li>Enables and configures useful auto-filling mechanisms (using property
	 * values and providing email subject from templates)</li>
	 * <li>Enables mimetype detection</li>
	 * <li>Enables locating templates, css, images and all other resources using
	 * lookup prefix ("file:" for files that are present on the filesystem,
	 * "classpath:" and "" for files that are present in the classpath)</li>
	 * <li>Enables use of some properties to provide path prefix/suffix for
	 * locating resources</li>
	 * <li>Enables images and css inlining used by HTML {@link Email}s</li>
	 * </ul>
	 * 
	 * <p>
	 * The auto-configurers ( {@link MessagingConfigurer}s) are automatically
	 * loaded from the classpath. Only configurers that are in the provided
	 * packages and sub-packages are loaded. Some Ogham modules are optional
	 * meaning that according to used modules, the behavior will vary.
	 * 
	 * Loaded {@link MessagingConfigurer}s are applied to the
	 * {@link MessagingBuilder} only if they are for the "standard" builder. It
	 * is accomplished thanks to the {@link ConfigurerFor} annotation (only
	 * configurers annotated and with {@link ConfigurerFor#targetedBuilder()}
	 * set to "standard").
	 * 
	 * Loaded configurers with priorities are (if all Ogham modules are used):
	 * <ul>
	 * <li><code>DefaultMessagingConfigurer</code>: 100000</li>
	 * <li><code>DefaultThymeleafEmailConfigurer</code>: 90000</li>
	 * <li><code>DefaultFreemarkerEmailConfigurer</code>: 80000</li>
	 * <li><code>DefaultThymeleafSmsConfigurer</code>: 70000</li>
	 * <li><code>DefaultFreemarkerSmsConfigurer</code>: 60000</li>
	 * <li><code>DefaultJavaMailConfigurer</code>: 50000</li>
	 * <li><code>DefaultCloudhopperConfigurer</code>: 40000</li>
	 * <li><code>DefaultSendGridConfigurer</code>: 30000</li>
	 * <li><code>DefaultOvhSmsConfigurer</code>: 20000</li>
	 * </ul>
	 * 
	 * TODO: link to whole configuration that is applied by standard
	 * 
	 * <p>
	 * The auto-configured {@link MessagingBuilder} will provide a default
	 * behavior that fits 95% of usages. You can still override some behaviors
	 * for your needs.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * <code>
	 * MessagingService service = MessagingBuilder.standard()
	 *   .environment()
	 *     .properties("application.properties")
	 *     .and()
	 *   .wrapUncaught(false)    // overrides and disables wrapUncaught option
	 *   .build();
	 * </code>
	 * </pre>
	 * 
	 * @param basePackages
	 *            the base packages that are scanned to find
	 *            {@link MessagingConfigurer} implementations
	 * @return the messaging builder that can be customized
	 */
	public static MessagingBuilder standard(String... basePackages) {
		return standard(true, basePackages);
	}

	/**
	 * Static factory method that initializes a {@link MessagingBuilder}
	 * instance and registers auto-configures but doesn't apply them if
	 * autoconfigure parameter is false. The
	 * {@link #configure(ConfigurationPhase)} method must be called manually.
	 * 
	 * Usage example:
	 * 
	 * <pre>
	 * <code>
	 * MessagingService service = MessagingBuilder.standard()
	 *   .environment()
	 *     .properties("application.properties")
	 *     .and()
	 *   .build();
	 * </code>
	 * </pre>
	 * 
	 * <p>
	 * Basically, the standard behavior:
	 * <ul>
	 * <li>Enables all template engines that are present in the classpath and
	 * configures them</li>
	 * <li>Enables all {@link Email} sender implementations that are present in
	 * the classpath and configures them</li>
	 * <li>Enables all {@link Sms} sender implementations that are present in
	 * the classpath and configures them</li>
	 * <li>Catches all uncaught exception to wrap them in order to avoid
	 * unwanted unchecked exception</li>
	 * <li>Uses system properties</li>
	 * <li>Enables and configures useful auto-filling mechanisms (using property
	 * values and providing email subject from templates)</li>
	 * <li>Enables mimetype detection</li>
	 * <li>Enables locating templates, css, images and all other resources using
	 * lookup prefix ("file:" for files that are present on the filesystem,
	 * "classpath:" and "" for files that are present in the classpath)</li>
	 * <li>Enables use of some properties to provide path prefix/suffix for
	 * locating resources</li>
	 * <li>Enables images and css inlining used by HTML {@link Email}s</li>
	 * </ul>
	 * 
	 * <p>
	 * The auto-configurers ( {@link MessagingConfigurer}s) are automatically
	 * loaded from the classpath. Only configurers that are in "fr.sii.ogham"
	 * package and sub-packages are loaded. If you want to load from other
	 * packages, use {@link #standard(String...)}. Some Ogham modules are
	 * optional meaning that according to used modules, the behavior will vary.
	 * 
	 * Loaded {@link MessagingConfigurer}s are applied to the
	 * {@link MessagingBuilder} only if they are for the "standard" builder. It
	 * is accomplished thanks to the {@link ConfigurerFor} annotation (only
	 * configurers annotated and with {@link ConfigurerFor#targetedBuilder()}
	 * set to "standard").
	 * 
	 * Loaded configurers with priorities are (if all Ogham modules are used):
	 * <ul>
	 * <li><code>DefaultMessagingConfigurer</code>: 100000</li>
	 * <li><code>DefaultThymeleafEmailConfigurer</code>: 90000</li>
	 * <li><code>DefaultFreemarkerEmailConfigurer</code>: 80000</li>
	 * <li><code>DefaultThymeleafSmsConfigurer</code>: 70000</li>
	 * <li><code>DefaultFreemarkerSmsConfigurer</code>: 60000</li>
	 * <li><code>DefaultJavaMailConfigurer</code>: 50000</li>
	 * <li><code>DefaultCloudhopperConfigurer</code>: 40000</li>
	 * <li><code>DefaultSendGridConfigurer</code>: 30000</li>
	 * <li><code>DefaultOvhSmsConfigurer</code>: 20000</li>
	 * </ul>
	 * 
	 * TODO: link to whole configuration that is applied by standard
	 * 
	 * <p>
	 * The auto-configured {@link MessagingBuilder} will provide a default
	 * behavior that fits 95% of usages. You can still override some behaviors
	 * for your needs.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * <code>
	 * MessagingService service = MessagingBuilder.standard()
	 *   .environment()
	 *     .properties("application.properties")
	 *     .and()
	 *   .wrapUncaught(false)    // overrides and disables wrapUncaught option
	 *   .build();
	 * </code>
	 * </pre>
	 * 
	 * @param autoconfigure
	 *            true to automatically apply found configurers, false to
	 *            configure manually later by calling
	 *            {@link #configure(ConfigurationPhase)}
	 * @return the messaging builder that can be customized
	 */
	public static MessagingBuilder standard(boolean autoconfigure) {
		return standard(autoconfigure, BASE_PACKAGE);
	}

	/**
	 * Static factory method that initializes a {@link MessagingBuilder}
	 * instance and registers auto-configures but doesn't apply them if
	 * autoconfigure parameter is false. The
	 * {@link #configure(ConfigurationPhase)} method must be called manually.
	 * 
	 * Usage example:
	 * 
	 * <pre>
	 * <code>
	 * MessagingService service = MessagingBuilder.standard()
	 *   .environment()
	 *     .properties("application.properties")
	 *     .and()
	 *   .build();
	 * </code>
	 * </pre>
	 * 
	 * <p>
	 * Basically, the standard behavior:
	 * <ul>
	 * <li>Enables all template engines that are present in the classpath and
	 * configures them</li>
	 * <li>Enables all {@link Email} sender implementations that are present in
	 * the classpath and configures them</li>
	 * <li>Enables all {@link Sms} sender implementations that are present in
	 * the classpath and configures them</li>
	 * <li>Catches all uncaught exception to wrap them in order to avoid
	 * unwanted unchecked exception</li>
	 * <li>Uses system properties</li>
	 * <li>Enables and configures useful auto-filling mechanisms (using property
	 * values and providing email subject from templates)</li>
	 * <li>Enables mimetype detection</li>
	 * <li>Enables locating templates, css, images and all other resources using
	 * lookup prefix ("file:" for files that are present on the filesystem,
	 * "classpath:" and "" for files that are present in the classpath)</li>
	 * <li>Enables use of some properties to provide path prefix/suffix for
	 * locating resources</li>
	 * <li>Enables images and css inlining used by HTML {@link Email}s</li>
	 * </ul>
	 * 
	 * <p>
	 * The auto-configurers ( {@link MessagingConfigurer}s) are automatically
	 * loaded from the classpath. Only configurers that are in the provided
	 * packages and sub-packages are loaded. Some Ogham modules are optional
	 * meaning that according to used modules, the behavior will vary.
	 * 
	 * Loaded {@link MessagingConfigurer}s are applied to the
	 * {@link MessagingBuilder} only if they are for the "standard" builder. It
	 * is accomplished thanks to the {@link ConfigurerFor} annotation (only
	 * configurers annotated and with {@link ConfigurerFor#targetedBuilder()}
	 * set to "standard").
	 * 
	 * Loaded configurers with priorities are (if all Ogham modules are used):
	 * <ul>
	 * <li><code>DefaultMessagingConfigurer</code>: 100000</li>
	 * <li><code>DefaultThymeleafEmailConfigurer</code>: 90000</li>
	 * <li><code>DefaultFreemarkerEmailConfigurer</code>: 80000</li>
	 * <li><code>DefaultThymeleafSmsConfigurer</code>: 70000</li>
	 * <li><code>DefaultFreemarkerSmsConfigurer</code>: 60000</li>
	 * <li><code>DefaultJavaMailConfigurer</code>: 50000</li>
	 * <li><code>DefaultCloudhopperConfigurer</code>: 40000</li>
	 * <li><code>DefaultSendGridConfigurer</code>: 30000</li>
	 * <li><code>DefaultOvhSmsConfigurer</code>: 20000</li>
	 * </ul>
	 * 
	 * TODO: link to whole configuration that is applied by standard
	 * 
	 * <p>
	 * The auto-configured {@link MessagingBuilder} will provide a default
	 * behavior that fits 95% of usages. You can still override some behaviors
	 * for your needs.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * <code>
	 * MessagingService service = MessagingBuilder.standard()
	 *   .environment()
	 *     .properties("application.properties")
	 *     .and()
	 *   .wrapUncaught(false)    // overrides and disables wrapUncaught option
	 *   .build();
	 * </code>
	 * </pre>
	 * 
	 * @param autoconfigure
	 *            true to automatically apply found configurers, false to
	 *            configure manually later by calling
	 *            {@link #configure(ConfigurationPhase)}
	 * @param basePackages
	 *            the base packages that are scanned to find
	 *            {@link MessagingConfigurer} implementations
	 * @return the messaging builder that can be customized
	 */
	public static MessagingBuilder standard(boolean autoconfigure, String... basePackages) {
		MessagingBuilder builder = new MessagingBuilder(autoconfigure);
		findAndRegister(builder, "standard", basePackages);
		if (autoconfigure) {
			builder.configure(ConfigurationPhase.AFTER_INIT);
		}
		return builder;
	}

	/**
	 * Static factory method that initializes a {@link MessagingBuilder}
	 * instance and auto-configures it with a predefined behavior named
	 * "minimal".
	 * 
	 * Usage example:
	 * 
	 * <pre>
	 * <code>
	 * MessagingService service = MessagingBuilder.minimal()
	 *   .environment()
	 *     .properties("application.properties")
	 *     .and()
	 *   .build();
	 * </code>
	 * </pre>
	 * 
	 * <p>
	 * Basically, the minimal behavior:
	 * <ul>
	 * <li>Enables all template engines that are present in the classpath and
	 * configures them</li>
	 * <li>Catches all uncaught exception to wrap them in order to avoid
	 * unwanted unchecked exception</li>
	 * <li>Uses system properties</li>
	 * <li>Enables and configures useful auto-filling mechanisms (using property
	 * values and providing email subject from templates)</li>
	 * <li>Enables mimetype detection</li>
	 * <li>Enables locating templates, css, images and all other resources using
	 * lookup prefix ("file:" for files that are present on the filesystem,
	 * "classpath:" and "" for files that are present in the classpath)</li>
	 * <li>Enables use of some properties to provide path prefix/suffix for
	 * locating resources</li>
	 * <li>Enables images and css inlining used by HTML {@link Email}s</li>
	 * </ul>
	 * 
	 * The minimal behavior doesn't automatically auto-configure sender
	 * implementations.
	 * 
	 * <p>
	 * The auto-configurers ( {@link MessagingConfigurer}s) are automatically
	 * loaded from the classpath. Only configurers that are in "fr.sii.ogham"
	 * package and sub-packages are loaded. If you want to load from other
	 * packages, use {@link #minimal(String...)}. Some Ogham modules are
	 * optional meaning that according to used modules, the behavior will vary.
	 * 
	 * Loaded {@link MessagingConfigurer}s are applied to the
	 * {@link MessagingBuilder} only if they are for the "minimal" builder. It
	 * is accomplished thanks to the {@link ConfigurerFor} annotation (only
	 * configurers annotated and with {@link ConfigurerFor#targetedBuilder()}
	 * set to "minimal").
	 * 
	 * Loaded configurers with priorities are (if all Ogham modules are used):
	 * <ul>
	 * <li><code>DefaultMessagingConfigurer</code>: 100000</li>
	 * <li><code>DefaultThymeleafEmailConfigurer</code>: 90000</li>
	 * <li><code>DefaultFreemarkerEmailConfigurer</code>: 80000</li>
	 * <li><code>DefaultThymeleafSmsConfigurer</code>: 70000</li>
	 * <li><code>DefaultFreemarkerSmsConfigurer</code>: 60000</li>
	 * </ul>
	 * 
	 * TODO: link to whole configuration that is applied by minimal
	 * 
	 * <p>
	 * The auto-configured {@link MessagingBuilder} will provide a default
	 * behavior with no sender implementation. This is useful if you only want
	 * to use a particular implementation or your custom sender implementation.
	 * You can still override some behaviors for your needs.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * <code>
	 * MessagingService service = MessagingBuilder.minimal()
	 *   .environment()
	 *     .properties("application.properties")
	 *     .and()
	 *   .wrapUncaught(false)    // overrides and disables wrapUncaught option
	 *   .build();
	 * </code>
	 * </pre>
	 * 
	 * @return the messaging builder that can be customized
	 */
	public static MessagingBuilder minimal() {
		return minimal(BASE_PACKAGE);
	}

	/**
	 * Static factory method that initializes a {@link MessagingBuilder}
	 * instance and auto-configures it with a predefined behavior named
	 * "minimal".
	 * 
	 * Usage example:
	 * 
	 * <pre>
	 * <code>
	 * MessagingService service = MessagingBuilder.minimal()
	 *   .environment()
	 *     .properties("application.properties")
	 *     .and()
	 *   .build();
	 * </code>
	 * </pre>
	 * 
	 * <p>
	 * Basically, the minimal behavior:
	 * <ul>
	 * <li>Enables all template engines that are present in the classpath and
	 * configures them</li>
	 * <li>Catches all uncaught exception to wrap them in order to avoid
	 * unwanted unchecked exception</li>
	 * <li>Uses system properties</li>
	 * <li>Enables and configures useful auto-filling mechanisms (using property
	 * values and providing email subject from templates)</li>
	 * <li>Enables mimetype detection</li>
	 * <li>Enables locating templates, css, images and all other resources using
	 * lookup prefix ("file:" for files that are present on the filesystem,
	 * "classpath:" and "" for files that are present in the classpath)</li>
	 * <li>Enables use of some properties to provide path prefix/suffix for
	 * locating resources</li>
	 * <li>Enables images and css inlining used by HTML {@link Email}s</li>
	 * </ul>
	 * 
	 * The minimal behavior doesn't automatically auto-configure sender
	 * implementations.
	 * 
	 * <p>
	 * The auto-configurers ( {@link MessagingConfigurer}s) are automatically
	 * loaded from the classpath. Only configurers that are in the provided
	 * packages and sub-packages are loaded. Some Ogham modules are optional
	 * meaning that according to used modules, the behavior will vary.
	 * 
	 * Loaded {@link MessagingConfigurer}s are applied to the
	 * {@link MessagingBuilder} only if they are for the "minimal" builder. It
	 * is accomplished thanks to the {@link ConfigurerFor} annotation (only
	 * configurers annotated and with {@link ConfigurerFor#targetedBuilder()}
	 * set to "minimal").
	 * 
	 * Loaded configurers with priorities are (if all Ogham modules are used):
	 * <ul>
	 * <li><code>DefaultMessagingConfigurer</code>: 100000</li>
	 * <li><code>DefaultThymeleafEmailConfigurer</code>: 90000</li>
	 * <li><code>DefaultFreemarkerEmailConfigurer</code>: 80000</li>
	 * <li><code>DefaultThymeleafSmsConfigurer</code>: 70000</li>
	 * <li><code>DefaultFreemarkerSmsConfigurer</code>: 60000</li>
	 * </ul>
	 * 
	 * TODO: link to whole configuration that is applied by minimal
	 * 
	 * <p>
	 * The auto-configured {@link MessagingBuilder} will provide a default
	 * behavior with no sender implementation. This is useful if you only want
	 * to use a particular implementation or your custom sender implementation.
	 * You can still override some behaviors for your needs.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * <code>
	 * MessagingService service = MessagingBuilder.minimal()
	 *   .environment()
	 *     .properties("application.properties")
	 *     .and()
	 *   .wrapUncaught(false)    // overrides and disables wrapUncaught option
	 *   .build();
	 * </code>
	 * </pre>
	 * 
	 * @param basePackages
	 *            the base packages that are scanned to find
	 *            {@link MessagingConfigurer} implementations
	 * @return the messaging builder that can be customized
	 */
	public static MessagingBuilder minimal(String... basePackages) {
		return minimal(true, basePackages);
	}

	/**
	 * Static factory method that initializes a {@link MessagingBuilder}
	 * instance and registers auto-configures but doesn't apply them if
	 * autoconfigure parameter is false. The
	 * {@link #configure(ConfigurationPhase)} method must be called manually.
	 * 
	 * Usage example:
	 * 
	 * <pre>
	 * <code>
	 * MessagingService service = MessagingBuilder.minimal()
	 *   .environment()
	 *     .properties("application.properties")
	 *     .and()
	 *   .build();
	 * </code>
	 * </pre>
	 * 
	 * <p>
	 * Basically, the minimal behavior:
	 * <ul>
	 * <li>Enables all template engines that are present in the classpath and
	 * configures them</li>
	 * <li>Catches all uncaught exception to wrap them in order to avoid
	 * unwanted unchecked exception</li>
	 * <li>Uses system properties</li>
	 * <li>Enables and configures useful auto-filling mechanisms (using property
	 * values and providing email subject from templates)</li>
	 * <li>Enables mimetype detection</li>
	 * <li>Enables locating templates, css, images and all other resources using
	 * lookup prefix ("file:" for files that are present on the filesystem,
	 * "classpath:" and "" for files that are present in the classpath)</li>
	 * <li>Enables use of some properties to provide path prefix/suffix for
	 * locating resources</li>
	 * <li>Enables images and css inlining used by HTML {@link Email}s</li>
	 * </ul>
	 * 
	 * The minimal behavior doesn't automatically auto-configure sender
	 * implementations.
	 * 
	 * <p>
	 * The auto-configurers ( {@link MessagingConfigurer}s) are automatically
	 * loaded from the classpath. Only configurers that are in "fr.sii.ogham"
	 * package and sub-packages are loaded. If you want to load from other
	 * packages, use {@link #minimal(String...)}. Some Ogham modules are
	 * optional meaning that according to used modules, the behavior will vary.
	 * 
	 * Loaded {@link MessagingConfigurer}s are applied to the
	 * {@link MessagingBuilder} only if they are for the "minimal" builder. It
	 * is accomplished thanks to the {@link ConfigurerFor} annotation (only
	 * configurers annotated and with {@link ConfigurerFor#targetedBuilder()}
	 * set to "minimal").
	 * 
	 * Loaded configurers with priorities are (if all Ogham modules are used):
	 * <ul>
	 * <li><code>DefaultMessagingConfigurer</code>: 100000</li>
	 * <li><code>DefaultThymeleafEmailConfigurer</code>: 90000</li>
	 * <li><code>DefaultFreemarkerEmailConfigurer</code>: 80000</li>
	 * <li><code>DefaultThymeleafSmsConfigurer</code>: 70000</li>
	 * <li><code>DefaultFreemarkerSmsConfigurer</code>: 60000</li>
	 * </ul>
	 * 
	 * TODO: link to whole configuration that is applied by minimal
	 * 
	 * <p>
	 * The auto-configured {@link MessagingBuilder} will provide a default
	 * behavior with no sender implementation. This is useful if you only want
	 * to use a particular implementation or your custom sender implementation.
	 * You can still override some behaviors for your needs.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * <code>
	 * MessagingService service = MessagingBuilder.minimal()
	 *   .environment()
	 *     .properties("application.properties")
	 *     .and()
	 *   .wrapUncaught(false)    // overrides and disables wrapUncaught option
	 *   .build();
	 * </code>
	 * </pre>
	 * 
	 * @param autoconfigure
	 *            true to automatically apply found configurers, false to
	 *            configure manually later by calling
	 *            {@link #configure(ConfigurationPhase)}
	 * @return the messaging builder that can be customized
	 */
	public static MessagingBuilder minimal(boolean autoconfigure) {
		return minimal(autoconfigure, BASE_PACKAGE);
	}

	/**
	 * Static factory method that initializes a {@link MessagingBuilder}
	 * instance and registers auto-configures but doesn't apply them if
	 * autoconfigure parameter is false. The
	 * {@link #configure(ConfigurationPhase)} method must be called manually.
	 * 
	 * 
	 * Usage example:
	 * 
	 * <pre>
	 * <code>
	 * MessagingService service = MessagingBuilder.minimal()
	 *   .environment()
	 *     .properties("application.properties")
	 *     .and()
	 *   .build();
	 * </code>
	 * </pre>
	 * 
	 * <p>
	 * Basically, the minimal behavior:
	 * <ul>
	 * <li>Enables all template engines that are present in the classpath and
	 * configures them</li>
	 * <li>Catches all uncaught exception to wrap them in order to avoid
	 * unwanted unchecked exception</li>
	 * <li>Uses system properties</li>
	 * <li>Enables and configures useful auto-filling mechanisms (using property
	 * values and providing email subject from templates)</li>
	 * <li>Enables mimetype detection</li>
	 * <li>Enables locating templates, css, images and all other resources using
	 * lookup prefix ("file:" for files that are present on the filesystem,
	 * "classpath:" and "" for files that are present in the classpath)</li>
	 * <li>Enables use of some properties to provide path prefix/suffix for
	 * locating resources</li>
	 * <li>Enables images and css inlining used by HTML {@link Email}s</li>
	 * </ul>
	 * 
	 * The minimal behavior doesn't automatically auto-configure sender
	 * implementations.
	 * 
	 * <p>
	 * The auto-configurers ( {@link MessagingConfigurer}s) are automatically
	 * loaded from the classpath. Only configurers that are in the provided
	 * packages and sub-packages are loaded. Some Ogham modules are optional
	 * meaning that according to used modules, the behavior will vary.
	 * 
	 * Loaded {@link MessagingConfigurer}s are applied to the
	 * {@link MessagingBuilder} only if they are for the "minimal" builder. It
	 * is accomplished thanks to the {@link ConfigurerFor} annotation (only
	 * configurers annotated and with {@link ConfigurerFor#targetedBuilder()}
	 * set to "minimal").
	 * 
	 * Loaded configurers with priorities are (if all Ogham modules are used):
	 * <ul>
	 * <li><code>DefaultMessagingConfigurer</code>: 100000</li>
	 * <li><code>DefaultThymeleafEmailConfigurer</code>: 90000</li>
	 * <li><code>DefaultFreemarkerEmailConfigurer</code>: 80000</li>
	 * <li><code>DefaultThymeleafSmsConfigurer</code>: 70000</li>
	 * <li><code>DefaultFreemarkerSmsConfigurer</code>: 60000</li>
	 * </ul>
	 * 
	 * TODO: link to whole configuration that is applied by minimal
	 * 
	 * <p>
	 * The auto-configured {@link MessagingBuilder} will provide a default
	 * behavior with no sender implementation. This is useful if you only want
	 * to use a particular implementation or your custom sender implementation.
	 * You can still override some behaviors for your needs.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * <code>
	 * MessagingService service = MessagingBuilder.minimal()
	 *   .environment()
	 *     .properties("application.properties")
	 *     .and()
	 *   .wrapUncaught(false)    // overrides and disables wrapUncaught option
	 *   .build();
	 * </code>
	 * </pre>
	 * 
	 * @param autoconfigure
	 *            true to automatically apply found configurers, false to
	 *            configure manually later by calling
	 *            {@link #configure(ConfigurationPhase)}
	 * @param basePackages
	 *            the base packages that are scanned to find
	 *            {@link MessagingConfigurer} implementations
	 * @return the messaging builder that can be customized
	 */
	public static MessagingBuilder minimal(boolean autoconfigure, String... basePackages) {
		MessagingBuilder builder = new MessagingBuilder(autoconfigure);
		findAndRegister(builder, "minimal", basePackages);
		if (autoconfigure) {
			builder.configure(ConfigurationPhase.AFTER_INIT);
		}
		return builder;
	}

	/**
	 * You can use this method if {@link #standard()} and {@link #minimal()}
	 * factory methods doesn't fit your needs. The aim is to auto-configure a
	 * builder with matching configurers.
	 * 
	 * Utility method that searches all {@link MessagingConfigurer}s that are in
	 * the classpath. Only configurers that are in the provided packages (and
	 * sub-packages) are loaded.
	 * 
	 * Once configurers are found, they are filtered thanks to information
	 * provided by {@link ConfigurerFor} annotation. Only configurers with
	 * {@link ConfigurerFor#targetedBuilder()} value that matches the
	 * builderName parameter are kept.
	 * 
	 * The found and filtered configurers are registered into the provided
	 * builder instance.
	 * 
	 * @param builder
	 *            the builder to configure with matching configurers
	 * @param builderName
	 *            the name that is referenced by
	 *            {@link ConfigurerFor#targetedBuilder()}
	 * @param basePackages
	 *            the packages that are scanned to find
	 *            {@link MessagingConfigurer} implementations
	 */
	public static void findAndRegister(MessagingBuilder builder, String builderName, String... basePackages) {
		Reflections reflections = new Reflections(basePackages, new SubTypesScanner());
		Set<Class<? extends MessagingConfigurer>> configurerClasses = reflections.getSubTypesOf(MessagingConfigurer.class);
		for (Class<? extends MessagingConfigurer> configurerClass : configurerClasses) {
			ConfigurerFor annotation = configurerClass.getAnnotation(ConfigurerFor.class);
			if (annotation != null && asList(annotation.targetedBuilder()).contains(builderName)) {
				register(builder, builderName, configurerClass, annotation);
			}
		}
	}

	private static void register(MessagingBuilder builder, String builderName, Class<? extends MessagingConfigurer> configurerClass, ConfigurerFor annotation) {
		try {
			builder.register(configurerClass.newInstance(), annotation.priority(), annotation.phase());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new BuildException("Failed to register custom auto-discovered configurer (" + configurerClass.getSimpleName() + ") for " + builderName + " messaging builder", e);
		}
	}

	private List<ConditionalSender> buildSenders() {
		List<ConditionalSender> senders = new ArrayList<>();
		if (emailBuilder != null) {
			LOG.debug("building email sender with {}", emailBuilder);
			senders.add(emailBuilder.build());
		}
		if (smsBuilder != null) {
			LOG.debug("building email sender with {}", emailBuilder);
			senders.add(smsBuilder.build());
		}
		return senders;
	}

	private boolean alreadyConfigured(ConfigurationPhase phase) {
		Boolean configured = alreadyConfigured.get(phase);
		return configured != null && configured;
	}

	private static class ConfigurerWithPhase {
		private final MessagingConfigurer configurer;
		private final ConfigurationPhase phase;

		public ConfigurerWithPhase(MessagingConfigurer configurer, ConfigurationPhase phase) {
			super();
			this.configurer = configurer;
			this.phase = phase;
		}

		public MessagingConfigurer getConfigurer() {
			return configurer;
		}

		public ConfigurationPhase getPhase() {
			return phase;
		}

	}
}
