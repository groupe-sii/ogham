package fr.sii.ogham.email.builder.sendgrid;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.MimetypesFileTypeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.SendGrid;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilderDelegate;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilderDelegate;
import fr.sii.ogham.core.builder.mimetype.SimpleMimetypeDetectionBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.sender.impl.SendGridSender;
import fr.sii.ogham.email.sender.impl.sendgrid.client.DelegateSendGridClient;
import fr.sii.ogham.email.sender.impl.sendgrid.client.SendGridClient;
import fr.sii.ogham.email.sender.impl.sendgrid.client.SendGridInterceptor;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.MapContentHandler;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.MultiContentHandler;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.StringContentHandler;

/**
 * Configures how SendGrid implementation will send {@link Email}s.
 * 
 * This implementation uses SendGrid HTTP API.
 * 
 * <p>
 * To send {@link Email} using SendGrid, you need to register this builder into
 * a {@link MessagingBuilder} like this:
 * 
 * <pre>
 * <code>
 * MessagingBuilder msgBuilder = ...
 * msgBuilder.email()
 *    .sender(SendGridBuilder.class)    // registers the builder and accesses to that builder for configuring it
 * </code>
 * </pre>
 * 
 * Once the builder is registered, sending email through SendGrid requires
 * either an API key or a username/password pair. You can define it using:
 * 
 * <pre>
 * <code>
 * msgBuilder.email()
 *    .sender(SendGridBuilder.class)    // registers the builder and accesses to that builder for configuring it
 *       .apiKey("foo")
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
 *       .set("custom.property.for.api-key", "foo")
 *       .and()
 *    .and()
 * .email()
 *    .sender(SendGridBuilder.class)    // registers the builder and accesses to that builder for configuring it
 *       .host("${custom.property.for.api-key}")
 * </code>
 * </pre>
 * 
 * <p>
 * Finally, Ogham will transform general {@link Email} object into
 * {@link SendGrid}.Email object. This transformation will fit almost all use
 * cases but you may need to customize a part of the SendGrid message. Instead
 * of doing again the same work Ogham does, this builder allows you to intercept
 * the message to modify it just before sending it:
 * 
 * <pre>
 * <code>
 * .sender(SendGridBuilder.class)
 *    .intercept(new MyCustomInterceptor())
 * </code>
 * </pre>
 * 
 * See {@link SendGridInterceptor} for more information.
 * 
 * @author Aurélien Baudet
 *
 */
public class SendGridBuilder extends AbstractParent<EmailBuilder> implements Builder<SendGridSender> {
	private static final Logger LOG = LoggerFactory.getLogger(SendGridBuilder.class);
	
	private EnvironmentBuilder<SendGridBuilder> environmentBuilder;
	private MimetypeDetectionBuilder<SendGridBuilder> mimetypeBuilder;
	private List<String> apiKeys;
	private List<String> usernames;
	private List<String> passwords;
	private SendGridClient client;
	private SendGridInterceptor interceptor;

	/**
	 * Default constructor when using SendGrid sender without all Ogham work.
	 * 
	 * <strong>WARNING: use is only if you know what you are doing !</strong>
	 */
	public SendGridBuilder() {
		this(null);
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
	 *    .sender(SendGridBuilder.class)
	 * </pre>
	 * 
	 * @param parent
	 *            the parent builder instance for fluent chaining
	 */
	public SendGridBuilder(EmailBuilder parent) {
		super(parent);
		apiKeys = new ArrayList<>();
		usernames = new ArrayList<>();
		passwords = new ArrayList<>();
	}

	/**
	 * Set SendGrid <a href=
	 * "https://sendgrid.com/docs/Classroom/Send/How_Emails_Are_Sent/api_keys.html">API
	 * key</a>.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .apiKey("localhost");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .apiKey("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param key
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public SendGridBuilder apiKey(String... key) {
		for (String k : key) {
			apiKeys.add(k);
		}
		return this;
	}

	/**
	 * Set username for SendGrid HTTP API.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .username("foo");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .username("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param username
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public SendGridBuilder username(String... username) {
		for (String u : username) {
			usernames.add(u);
		}
		return this;
	}

	/**
	 * Set password for SendGrid HTTP API.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .password("foo");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .password("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param password
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public SendGridBuilder password(String... password) {
		for (String p : password) {
			passwords.add(p);
		}
		return this;
	}

	/**
	 * By default, calling SendGrid HTTP API is done through the default
	 * {@link SendGrid} implementation. If you want to use another client
	 * implementation (creating your custom HTTP API caller for example), you
	 * can implement the {@link SendGridClient} interface and provide it:
	 * 
	 * <pre>
	 * .client(new MyCustomHttpApiCaller())
	 * </pre>
	 * 
	 * NOTE: if you provide your custom implementation, any defined properties
	 * and values using {@link #apiKey(String...)}, {@link #username(String...)}
	 * or {@link #password(String...)} won't be used at all. You then have to
	 * handle it by yourself.
	 * 
	 * @param client
	 *            the custom client used to call SendGrid HTTP API
	 * @return this instance for fluent chaining
	 */
	public SendGridBuilder client(SendGridClient client) {
		this.client = client;
		return this;
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
	 * If no mimetype detector was previously defined, it creates a new one.
	 * Then each time you call {@link #mimetype()}, the same instance is used.
	 * </p>
	 * 
	 * @return the builder to configure mimetype detection
	 */
	public MimetypeDetectionBuilder<SendGridBuilder> mimetype() {
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
	 * coherence (see {@link DefaultSendGridConfigurer} for an example of use).
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
	public SendGridBuilder mimetype(MimetypeDetectionBuilder<?> builder) {
		mimetypeBuilder = new MimetypeDetectionBuilderDelegate<>(this, builder);
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
	public EnvironmentBuilder<SendGridBuilder> environment() {
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
	 * whole coherence (see {@link DefaultSendGridConfigurer} for an example of
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
	public SendGridBuilder environment(EnvironmentBuilder<?> builder) {
		environmentBuilder = new EnvironmentBuilderDelegate<>(this, builder);
		return this;
	}

	/**
	 * Ogham will transform general {@link Email} object into
	 * {@link SendGrid}.Email objects. This transformation will fit almost all
	 * use cases but you may need to customize a part of the SendGrid message.
	 * Instead of doing again the same work Ogham does, this builder allows you
	 * to intercept the message to modify it just before sending it:
	 * 
	 * <pre>
	 * .sender(SendGridBuilder.class)
	 *    .intercept(new MyCustomInterceptor())
	 * </pre>
	 * 
	 * See {@link SendGridInterceptor} for more information.
	 * 
	 * @param interceptor
	 *            the custom interceptor used to modify {@link SendGrid}.Email
	 * @return this instance for fluent chaining
	 */
	public SendGridBuilder intercept(SendGridInterceptor interceptor) {
		this.interceptor = interceptor;
		return this;
	}

	@Override
	public SendGridSender build() throws BuildException {
		PropertyResolver propertyResolver = environmentBuilder.build();
		String apiKey = BuilderUtils.evaluate(this.apiKeys, propertyResolver, String.class);
		String username = BuilderUtils.evaluate(this.usernames, propertyResolver, String.class);
		String password = BuilderUtils.evaluate(this.passwords, propertyResolver, String.class);
		if (apiKey == null && (username == null || password == null) && client == null) {
			return null;
		}
		SendGridClient client = buildClient(apiKey, username, password);
		LOG.info("Sending email using SendGrid API is registered");
		LOG.debug("SendGrid account: apiKey={}, username={}", apiKey, username);
		return new SendGridSender(client, buildContentHandler(), interceptor);
	}

	private SendGridClient buildClient(String apiKey, String username, String password) {
		if (client == null) {
			if (username != null && password != null) {
				return new DelegateSendGridClient(username, password);
			} else {
				return new DelegateSendGridClient(apiKey);
			}
		}
		return client;
	}

	private MapContentHandler buildContentHandler() {
		MimeTypeProvider mimetypeProvider = mimetypeBuilder.build();
		MapContentHandler contentHandler = new MapContentHandler();
		contentHandler.register(MultiContent.class, new MultiContentHandler(contentHandler));
		contentHandler.register(StringContent.class, new StringContentHandler(mimetypeProvider));
		return contentHandler;
	}
}
