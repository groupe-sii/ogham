package fr.sii.ogham.sms.builder.ovh;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilderDelegate;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.util.BuilderUtils;
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
 *       .host("${custom.property.for.url}")
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
	private List<String> urls;
	private List<String> accounts;
	private List<String> logins;
	private List<String> passwords;
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
		urls = new ArrayList<>();
		accounts = new ArrayList<>();
		logins = new ArrayList<>();
		passwords = new ArrayList<>();
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
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .url("https://www.ovh.com/cgi-bin/sms/http2sms.cgi");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .host("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param url
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public OvhSmsBuilder url(String... url) {
		for (String u : url) {
			if (u != null) {
				urls.add(u);
			}
		}
		return this;
	}

	/**
	 * Set the OVH account identifier.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .account("foo");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .account("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param account
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public OvhSmsBuilder account(String... account) {
		for (String a : account) {
			if (a != null) {
				accounts.add(a);
			}
		}
		return this;
	}

	/**
	 * Set the OVH username.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .login("foo");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .login("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param login
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public OvhSmsBuilder login(String... login) {
		for (String l : login) {
			if (l != null) {
				logins.add(l);
			}
		}
		return this;
	}

	/**
	 * Set the OVH password.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .password("bar");
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
	public OvhSmsBuilder password(String... password) {
		for (String p : password) {
			if (p != null) {
				passwords.add(p);
			}
		}
		return this;
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
		try {
			String url = BuilderUtils.evaluate(urls, propertyResolver, String.class);
			if (url != null) {
				return new URL(url);
			}
			return null;
		} catch (MalformedURLException e) {
			throw new BuildException("Failed to create OVH SMS sender due to invalid URL", e);
		}
	}

	private OvhAuthParams buildAuth(PropertyResolver propertyResolver) {
		String account = BuilderUtils.evaluate(accounts, propertyResolver, String.class);
		String login = BuilderUtils.evaluate(logins, propertyResolver, String.class);
		String password = BuilderUtils.evaluate(passwords, propertyResolver, String.class);
		return new OvhAuthParams(account, login, password);
	}

	private OvhOptions buildOptions() {
		return ovhOptionsBuilder.build();
	}
}
