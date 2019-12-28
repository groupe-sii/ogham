package fr.sii.ogham.sms.builder.ovh;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilderDelegate;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.sms.builder.SmsBuilder;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.OvhSmsSender;
import fr.sii.ogham.sms.sender.impl.ovh.DefaultSmsCodingDetector;
import fr.sii.ogham.sms.sender.impl.ovh.OvhAuthParams;
import fr.sii.ogham.sms.sender.impl.ovh.OvhOptions;
import fr.sii.ogham.sms.sender.impl.ovh.SmsCoding;

/**
 * Configures how to send {@link Sms} using OVH HTTP API.
 * 
 * <p>
 * To send {@link Sms} using OVH, you need to register this builder into a
 * {@link MessagingBuilder} like this:
 * 
 * <pre>
 * <code>
 * MessagingBuilder msgBuilder = ...
 * msgBuilder.sms()
 *    .sender(OvhSmsBuilder.class)    // registers the builder and accesses to that builder for configuring it
 * </code>
 * </pre>
 * 
 * Once the builder is registered, sending sms through OVH requires URL,
 * account, login and password values. You can define it using:
 * 
 * <pre>
 * <code>
 * msgBuilder.email()
 *    .sender(OvhSmsBuilder.class)    // registers the builder and accesses to that builder for configuring it
 *       .url("https://www.ovh.com/cgi-bin/sms/http2sms.cgi")
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
 *       .set("custom.property.for.url", "https://www.ovh.com/cgi-bin/sms/http2sms.cgi")
 *       .and()
 *    .and()
 * .email()
 *    .sender(OvhSmsBuilder.class)    // registers the builder and accesses to that builder for configuring it
 *       .url()
 *       	.properties("${custom.property.for.url}")
 * </code>
 * </pre>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class OvhSmsBuilder extends AbstractParent<SmsBuilder> implements Builder<OvhSmsSender> {
	private static final Logger LOG = LoggerFactory.getLogger(OvhSmsBuilder.class);

	private EnvironmentBuilder<OvhSmsBuilder> environmentBuilder;
	private final ConfigurationValueBuilderHelper<OvhSmsBuilder, URL> urlValueBuilder;
	private final ConfigurationValueBuilderHelper<OvhSmsBuilder, String> accountValueBuilder;
	private final ConfigurationValueBuilderHelper<OvhSmsBuilder, String> loginValueBuilder;
	private final ConfigurationValueBuilderHelper<OvhSmsBuilder, String> passwordValueBuilder;
	private OvhOptionsBuilder ovhOptionsBuilder;

	/**
	 * Default constructor when using OVH SMS sender without all Ogham work.
	 * 
	 * <strong>WARNING: use is only if you know what you are doing !</strong>
	 */
	public OvhSmsBuilder() {
		this(null);
		environmentBuilder = new SimpleEnvironmentBuilder<>(this);
	}

	/**
	 * Constructor that is called when using Ogham builder:
	 * 
	 * <pre>
	 * MessagingBuilder msgBuilder = ...
	 * msgBuilder
	 * .email()
	 *    .sender(OvhSmsBuilder.class)
	 * </pre>
	 * 
	 * @param parent
	 *            the parent builder instance for fluent chaining
	 */
	public OvhSmsBuilder(SmsBuilder parent) {
		super(parent);
		urlValueBuilder = new ConfigurationValueBuilderHelper<>(this, URL.class);
		accountValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class);
		loginValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class);
		passwordValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class);
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
	public EnvironmentBuilder<OvhSmsBuilder> environment() {
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
	 * whole coherence (see {@link DefaultOvhSmsConfigurer} for an example of
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
	public OvhSmsBuilder environment(EnvironmentBuilder<?> builder) {
		environmentBuilder = new EnvironmentBuilderDelegate<>(this, builder);
		return this;
	}

	/**
	 * Set the URL of the OVH SMS HTTP API.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #url()}.
	 * 
	 * <pre>
	 * .url(new URL("http://localhost"))
	 * .url()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(new URL("https://www.ovh.com/cgi-bin/sms/http2sms.cgi"))
	 * </pre>
	 * 
	 * <pre>
	 * .url(new URL("http://localhost"))
	 * .url()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(new URL("https://www.ovh.com/cgi-bin/sms/http2sms.cgi"))
	 * </pre>
	 * 
	 * In both cases, {@code url(new URL("http://localhost"))} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param url
	 *            the url for Ovh HTTP API
	 * @return this instance for fluent chaining
	 */
	public OvhSmsBuilder url(URL url) {
		urlValueBuilder.setValue(url);
		return this;
	}

	/**
	 * Set the URL of the OVH SMS HTTP API.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #url()}.
	 * 
	 * <pre>
	 * .url("http://localhost")
	 * .url()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("https://www.ovh.com/cgi-bin/sms/http2sms.cgi")
	 * </pre>
	 * 
	 * <pre>
	 * .url("http://localhost")
	 * .url()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("https://www.ovh.com/cgi-bin/sms/http2sms.cgi")
	 * </pre>
	 * 
	 * In both cases, {@code url("http://localhost")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param url
	 *            the url for Ovh HTTP API
	 * @return this instance for fluent chaining
	 * @throws IllegalArgumentException
	 *             when URL is not valid
	 */
	public OvhSmsBuilder url(String url) {
		try {
			return url(new URL(url));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid URL " + url, e);
		}
	}

	/**
	 * Set the URL of the OVH SMS HTTP API.
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
	 *   .defaultValue(new URL("https://www.ovh.com/cgi-bin/sms/http2sms.cgi"))
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #url(URL)} takes precedence over property
	 * values and default value.
	 * 
	 * <pre>
	 * .url(new URL("http://localhost"))
	 * .url()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(new URL("https://www.ovh.com/cgi-bin/sms/http2sms.cgi"))
	 * </pre>
	 * 
	 * The value {@code new URL("http://localhost")} is used regardless of the
	 * value of the properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<OvhSmsBuilder, URL> url() {
		return urlValueBuilder;
	}

	/**
	 * Set the OVH account identifier.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #account()}.
	 * 
	 * <pre>
	 * .account("my-account")
	 * .account()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-account")
	 * </pre>
	 * 
	 * <pre>
	 * .account("my-account")
	 * .account()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-account")
	 * </pre>
	 * 
	 * In both cases, {@code account("my-account")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param account
	 *            the account identifier
	 * @return this instance for fluent chaining
	 */
	public OvhSmsBuilder account(String account) {
		accountValueBuilder.setValue(account);
		return this;
	}

	/**
	 * Set the OVH account identifier.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .account()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-account")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #account(String)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .account("my-account")
	 * .account()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-account")
	 * </pre>
	 * 
	 * The value {@code "my-account"} is used regardless of the value of the
	 * properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<OvhSmsBuilder, String> account() {
		return accountValueBuilder;
	}

	/**
	 * Set the OVH username.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #login()}.
	 * 
	 * <pre>
	 * .login("my-username")
	 * .login()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-username")
	 * </pre>
	 * 
	 * <pre>
	 * .login("my-username")
	 * .login()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-username")
	 * </pre>
	 * 
	 * In both cases, {@code login("my-username")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param login
	 *            the OVH username
	 * @return this instance for fluent chaining
	 */
	public OvhSmsBuilder login(String login) {
		loginValueBuilder.setValue(login);
		return this;
	}

	/**
	 * Set the OVH username.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .login()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-username")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #login(String)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .login("my-username")
	 * .login()
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
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<OvhSmsBuilder, String> login() {
		return loginValueBuilder;
	}

	/**
	 * Set the OVH password.
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
	 *            the OVH password
	 * @return this instance for fluent chaining
	 */
	public OvhSmsBuilder password(String password) {
		passwordValueBuilder.setValue(password);
		return this;
	}

	/**
	 * Set the OVH password.
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
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<OvhSmsBuilder, String> password() {
		return passwordValueBuilder;
	}

	/**
	 * Configures OVH SMS options:
	 * <ul>
	 * <li>Enable/disable the "STOP" indication at the end of the message
	 * (useful to disable for non-commercial SMS)</li>
	 * <li>Define the SMS encoding (see {@link SmsCoding}): 1 for 7bit encoding,
	 * 2 for 8bit encoding (UTF-8). If you use UTF-8, your SMS will have a
	 * maximum size of 70 characters instead of 160</li>
	 * <li>Define a tag to mark sent messages (a 20 maximum character
	 * string)</li>
	 * </ul>
	 * 
	 * @return the builder to configure OVH SMS options
	 */
	public OvhOptionsBuilder options() {
		if (ovhOptionsBuilder == null) {
			ovhOptionsBuilder = new OvhOptionsBuilder(this, environmentBuilder);
		}
		return ovhOptionsBuilder;
	}

	@Override
	public OvhSmsSender build() {
		PropertyResolver propertyResolver = environmentBuilder.build();
		URL url = buildUrl(propertyResolver);
		OvhAuthParams authParams = buildAuth(propertyResolver);
		if (url == null || authParams.getAccount() == null || authParams.getLogin() == null || authParams.getPassword() == null) {
			return null;
		}
		LOG.info("Sending SMS using OVH API is registered");
		LOG.debug("OVH account: account={}, login={}", authParams.getAccount(), authParams.getLogin());
		return new OvhSmsSender(url, authParams, buildOptions(), new DefaultSmsCodingDetector());
	}

	private URL buildUrl(PropertyResolver propertyResolver) {
		return urlValueBuilder.getValue(propertyResolver);
	}

	private OvhAuthParams buildAuth(PropertyResolver propertyResolver) {
		String accountValue = accountValueBuilder.getValue(propertyResolver);
		String loginValue = loginValueBuilder.getValue(propertyResolver);
		String passwordValue = passwordValueBuilder.getValue(propertyResolver);
		return new OvhAuthParams(accountValue, loginValue, passwordValue);
	}

	private OvhOptions buildOptions() {
		return ovhOptionsBuilder.build();
	}
}
