package fr.sii.ogham.email.sendgrid.builder;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;

import javax.activation.MimetypesFileTypeMap;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;

import com.sendgrid.SendGrid;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilderDelegate;
import fr.sii.ogham.core.builder.mimetype.SimpleMimetypeDetectionBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.email.sendgrid.sender.SendGridSender;

@SuppressWarnings("squid:S00119")
public abstract class AbstractSendGridBuilder<MYSELF extends AbstractSendGridBuilder<MYSELF, EmailBuilder>, EmailBuilder> extends AbstractParent<EmailBuilder> implements Builder<SendGridSender> {
	protected final MYSELF myself;
	protected final BuildContext buildContext;
	protected MimetypeDetectionBuilder<MYSELF> mimetypeBuilder;
	protected final ConfigurationValueBuilderHelper<MYSELF, String> apiKeyValueBuilder;
	protected final ConfigurationValueBuilderHelper<MYSELF, URL> urlValueBuilder;
	protected CloseableHttpClient httpClient;

	@SuppressWarnings("unchecked")
	protected AbstractSendGridBuilder(Class<?> selfType, EmailBuilder parent, BuildContext buildContext, MimetypeDetectionBuilder<?> mimetypeBuilder) {
		super(parent);
		myself = (MYSELF) selfType.cast(this);
		this.buildContext = buildContext;
		apiKeyValueBuilder = new ConfigurationValueBuilderHelper<>(myself, String.class, buildContext);
		urlValueBuilder = new ConfigurationValueBuilderHelper<>(myself, URL.class, buildContext);
		if (mimetypeBuilder != null) {
			mimetype(mimetypeBuilder);
		}
	}

	public AbstractSendGridBuilder(Class<?> selfType, EmailBuilder parent, BuildContext buildContext) {
		this(selfType, parent, buildContext, null);
		mimetype();
	}

	/**
	 * Set SendGrid <a href=
	 * "https://sendgrid.com/docs/Classroom/Send/How_Emails_Are_Sent/api_keys.html">API
	 * key</a>.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #apiKey()}.
	 * 
	 * <pre>
	 * .apiKey("my-key")
	 * .apiKey()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-key")
	 * </pre>
	 * 
	 * <pre>
	 * .apiKey("my-key")
	 * .apiKey()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-key")
	 * </pre>
	 * 
	 * In both cases, {@code apiKey("my-key")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param apiKey
	 *            the API key to use
	 * @return this instance for fluent chaining
	 */
	public MYSELF apiKey(String apiKey) {
		apiKeyValueBuilder.setValue(apiKey);
		return myself;
	}

	/**
	 * Set SendGrid <a href=
	 * "https://sendgrid.com/docs/Classroom/Send/How_Emails_Are_Sent/api_keys.html">API
	 * key</a>.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .apiKey()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-key")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #apiKey(String)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .apiKey("my-key")
	 * .apiKey()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-key")
	 * </pre>
	 * 
	 * The value {@code "my-key"} is used regardless of the value of the
	 * properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<MYSELF, String> apiKey() {
		return apiKeyValueBuilder;
	}

	/**
	 * Set username for SendGrid HTTP API.
	 * 
	 * <p>
	 * <strong>WARNING:</strong> SendGrid v4 doesn't use username/password
	 * anymore. You must use an {@link #apiKey(String)}.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #username()}.
	 * 
	 * <pre>
	 * .username("my-username")
	 * .username()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-username")
	 * </pre>
	 * 
	 * <pre>
	 * .username("my-username")
	 * .username()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-username")
	 * </pre>
	 * 
	 * In both cases, {@code username("my-username")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param username
	 *            the user name for SendGrid HTTP API
	 * @return this instance for fluent chaining
	 * 
	 */
	public abstract MYSELF username(String username);

	/**
	 * Set username for SendGrid HTTP API.
	 * 
	 * <p>
	 * <strong>WARNING:</strong> SendGrid v4 doesn't use username/password
	 * anymore. You must use an {@link #apiKey(String)}.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .username()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-username")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #username(String)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .username("my-username")
	 * .username()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-username")
	 * </pre>
	 * 
	 * The value {@code "my-username"} is used regardless of the value of the
	 * properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public abstract ConfigurationValueBuilder<MYSELF, String> username();

	/**
	 * Set password for SendGrid HTTP API.
	 * 
	 * <p>
	 * <strong>WARNING:</strong> SendGrid v4 doesn't use username/password
	 * anymore. You must use an {@link #apiKey(String)}.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #password()}.
	 * 
	 * <pre>
	 * .password("my-password")
	 * .password()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-password")
	 * </pre>
	 * 
	 * <pre>
	 * .password("my-password")
	 * .password()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-password")
	 * </pre>
	 * 
	 * In both cases, {@code password("my-password")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param password
	 *            the password value
	 * @return this instance for fluent chaining
	 */
	public abstract MYSELF password(String password);

	/**
	 * Set password for SendGrid HTTP API.
	 * 
	 * <p>
	 * <strong>WARNING:</strong> SendGrid v4 doesn't use username/password
	 * anymore. You must use an {@link #apiKey(String)}.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .password()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-password")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #password(String)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .password("my-password")
	 * .password()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-password")
	 * </pre>
	 * 
	 * The value {@code "my-password"} is used regardless of the value of the
	 * properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public abstract ConfigurationValueBuilder<MYSELF, String> password();

	/**
	 * Set SendGrid API base URL.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #url()}.
	 * 
	 * <pre>
	 * .url("http://localhost/sendgrid")
	 * .url()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("http://api.sendgrid.com")
	 * </pre>
	 * 
	 * <pre>
	 * .url("http://localhost/sendgrid")
	 * .url()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("http://api.sendgrid.com")
	 * </pre>
	 * 
	 * In both cases, {@code url("http://localhost/sendgrid")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param url
	 *            the base URL for SendGrid HTTP API
	 * @return this instance for fluent chaining
	 */
	public MYSELF url(URL url) {
		urlValueBuilder.setValue(url);
		return myself;
	}

	/**
	 * Set SendGrid API base URL.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #url()}.
	 * 
	 * <pre>
	 * .url("http://localhost/sendgrid")
	 * .url()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("http://api.sendgrid.com")
	 * </pre>
	 * 
	 * <pre>
	 * .url("http://localhost/sendgrid")
	 * .url()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("http://api.sendgrid.com")
	 * </pre>
	 * 
	 * In both cases, {@code url("http://localhost/sendgrid")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param url
	 *            the base URL for SendGrid HTTP API
	 * @return this instance for fluent chaining
	 * @throws IllegalArgumentException
	 *             if URL is malformed
	 */
	public MYSELF url(String url) {
		try {
			return url(new URL(url));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid URL " + url, e);
		}
	}

	/**
	 * Set SendGrid API base URL.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .url()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("http://api.sendgrid.com")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #url(URL)} takes precedence over property
	 * values and default value.
	 * 
	 * <pre>
	 * .url("http://localhost/sendgrid")
	 * .url()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("http://api.sendgrid.com")
	 * </pre>
	 * 
	 * The value {@code "http://localhost/sendgrid"} is used regardless of the
	 * value of the properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<MYSELF, URL> url() {
		return urlValueBuilder;
	}

	/**
	 * By default, calling SendGrid HTTP API is done through the default
	 * {@link SendGrid} implementation that uses default {@link HttpClient}
	 * (calling {@code HttpClientBuilder.create().build()}). If you want to use
	 * another HTTP client implementation, you can extend the
	 * {@link CloseableHttpClient} class and provide it:
	 * 
	 * <pre>
	 * .client(new MyCustomHttpClient())
	 * </pre>
	 * 
	 * @param httpClient
	 *            the custom implementation of {@link HttpClient} used to call
	 *            SendGrid HTTP API. SendGrid requires a
	 *            {@link CloseableHttpClient}.
	 * @return this instance for fluent chaining
	 */
	public MYSELF httpClient(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
		return myself;
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
	public MimetypeDetectionBuilder<MYSELF> mimetype() {
		if (mimetypeBuilder == null) {
			mimetypeBuilder = new SimpleMimetypeDetectionBuilder<>(myself, buildContext);
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
	public MYSELF mimetype(MimetypeDetectionBuilder<?> builder) {
		mimetypeBuilder = new MimetypeDetectionBuilderDelegate<>(myself, builder);
		return myself;
	}

}
