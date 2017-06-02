package fr.sii.ogham.sms.builder.cloudhopper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.pdu.Pdu;
import com.cloudhopper.smpp.ssl.SslConfiguration;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.LoggingOptions;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilderDelegate;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.retry.RetryExecutor;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.sms.builder.SmsBuilder;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.message.addressing.translator.CompositePhoneNumberTranslator;
import fr.sii.ogham.sms.message.addressing.translator.DefaultHandler;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;
import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;
import fr.sii.ogham.sms.sender.impl.cloudhopper.CloudhopperCharsetHandler;
import fr.sii.ogham.sms.sender.impl.cloudhopper.CloudhopperOptions;

/**
 * Configures Cloudhopper:
 * 
 * <ul>
 * <li>SMPP protocol parameters (host, port, systemId, password,
 * version...)</li>
 * <li>Session management (name, bind, timeouts, retry...)</li>
 * <li>SSL configuration</li>
 * <li>Logging options</li>
 * </ul>
 * 
 * <p>
 * To send {@link Sms} using Cloudhopper, you need to register this builder into
 * a {@link MessagingBuilder} like this:
 * 
 * <pre>
 * <code>
 * MessagingBuilder msgBuilder = ...
 * msgBuilder.sms()
 *    .sender(CloudhopperBuilder.class)    // registers the builder and accesses to that builder for configuring it
 * </code>
 * </pre>
 * 
 * Once the builder is registered, sending sms through Cloudhopper requires at
 * least host of the SMPP server. You can define it using:
 * 
 * <pre>
 * <code>
 * msgBuilder.sms()
 *    .sender(CloudhopperBuilder.class)    // registers the builder and accesses to that builder for configuring it
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
 * .sms()
 *    .sender(CloudhopperBuilder.class)    // registers the builder and accesses to that builder for configuring it
 *       .host("${custom.property.for.host}")
 * </code>
 * </pre>
 * 
 * You can do the same with port of the SMPP server.
 * 
 * 
 * <p>
 * SMPP server may require authentication. In most cases, authentication is done
 * using system_id/password. You can use this builder to quickly provide your
 * system_id and password:
 * 
 * <pre>
 * <code>
 * .sender(CloudhopperBuilder.class)
 *        .systemId("foo")
 *        .password("bar")
 * </code>
 * </pre>
 * 
 * 
 * @author Aurélien Baudet
 */
// TODO: be able to configure PhoneNumberTranslator
public class CloudhopperBuilder extends AbstractParent<SmsBuilder> implements Builder<CloudhopperSMPPSender> {
	private static final Logger LOG = LoggerFactory.getLogger(CloudhopperBuilder.class);

	private EnvironmentBuilder<CloudhopperBuilder> environmentBuilder;
	private List<String> systemIds;
	private List<String> passwords;
	private List<String> hosts;
	private List<String> ports;
	private Integer port;
	private List<String> systemTypes;
	private Byte interfaceVersion;
	private List<String> interfaceVersions;
	private List<String> bindTypes;
	private SmppBindType bindType;
	private SessionBuilder sessionBuilder;
	private SmppSessionConfiguration sessionConfiguration;
	private CharsetBuilder charsetBuilder;
	private Address addressRange;
	private SslBuilder sslBuilder;
	private LoggingBuilder loggingBuilder;

	/**
	 * Default constructor when using without all Ogham work.
	 * 
	 * <strong>WARNING: use is only if you know what you are doing !</strong>
	 */
	public CloudhopperBuilder() {
		this(null);
		environmentBuilder = new SimpleEnvironmentBuilder<>(this);
	}

	/**
	 * Constructor that is called when using Ogham builder:
	 * 
	 * <pre>
	 * MessagingBuilder msgBuilder = ...
	 * msgBuilder
	 * .sms()
	 *    .sender(CloudhopperBuilder.class)
	 * </pre>
	 * 
	 * @param parent
	 *            the parent builder instance for fluent chaining
	 */
	public CloudhopperBuilder(SmsBuilder parent) {
		super(parent);
		systemIds = new ArrayList<>();
		passwords = new ArrayList<>();
		hosts = new ArrayList<>();
		ports = new ArrayList<>();
		interfaceVersions = new ArrayList<>();
		systemTypes = new ArrayList<>();
		bindTypes = new ArrayList<>();
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
	public EnvironmentBuilder<CloudhopperBuilder> environment() {
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
	 * whole coherence (see {@link DefaultCloudhopperConfigurer} for an example
	 * of use).
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
	public CloudhopperBuilder environment(EnvironmentBuilder<?> builder) {
		environmentBuilder = new EnvironmentBuilderDelegate<>(this, builder);
		return this;
	}

	/**
	 * The system_id parameter is used to identify an ESME ( External Short
	 * Message Entity) or an SMSC (Short Message Service Centre) at bind time.
	 * An ESME system_id identifies the ESME or ESME agent to the SMSC. The SMSC
	 * system_id provides an identification of the SMSC to the ESME.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .systemId("foo");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .systemId("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * 
	 * @param systemId
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder systemId(String... systemId) {
		systemIds.addAll(Arrays.asList(systemId));
		return this;
	}

	/**
	 * The system_type parameter is used to categorize the type of ESME that is
	 * binding to the SMSC. Examples include “VMS” (voice mail system) and “OTA”
	 * (over-the-air activation system). Specification of the system_type is
	 * optional - some SMSC’s may not require ESME’s to provide this detail. In
	 * this case, the ESME can set the system_type to NULL. The system_type
	 * (optional) may be used to categorize the system, e.g., “EMAIL”, “WWW”,
	 * etc.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .systemType("foo");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .systemType("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * 
	 * @param systemType
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder systemType(String... systemType) {
		systemTypes.addAll(Arrays.asList(systemType));
		return this;
	}

	/**
	 * The password parameter is used by the SMSC to authenticate the identity
	 * of the binding ESME. The Service Provider may require ESME’s to provide a
	 * password when binding to the SMSC. This password is normally issued by
	 * the SMSC system administrator. The password parameter may also be used by
	 * the ESME to authenticate the identity of the binding SMSC (e.g. in the
	 * case of the outbind operation).
	 * 
	 * The ESME may set the password to NULL to gain insecure access (if allowed
	 * by SMSC administration).
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
	public CloudhopperBuilder password(String... password) {
		passwords.addAll(Arrays.asList(password));
		return this;
	}

	/**
	 * The SMPP server host (IP or address).
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .host("localhost");
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
	 * @param host
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder host(String... host) {
		hosts.addAll(Arrays.asList(host));
		return this;
	}

	/**
	 * Set the SMPP server port.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .port("2775");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .port("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param port
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder port(String... port) {
		ports.addAll(Arrays.asList(port));
		return this;
	}

	/**
	 * Set the SMPP server port.
	 * 
	 * This value preempts any other value defined by calling
	 * {@link #port(String...)} method.
	 * 
	 * If this method is called several times, only the last value is used.
	 * 
	 * @param port
	 *            the port to use
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder port(int port) {
		this.port = port;
		return this;
	}

	/**
	 * The SMPP protocol version (one of {@link SmppConstants#VERSION_3_3},
	 * {@link SmppConstants#VERSION_3_4}, {@link SmppConstants#VERSION_5_0}).
	 * 
	 * This value preempts any other value defined by calling
	 * {@link #interfaceVersion(String...)} method.
	 * 
	 * @param version
	 *            the version as byte value
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder interfaceVersion(byte version) {
		interfaceVersion = version;
		return this;
	}

	/**
	 * Set the SMPP protocol version.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .interfaceVersion("3.4");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .interfaceVersion("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param version
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder interfaceVersion(String... version) {
		interfaceVersions.addAll(Arrays.asList(version));
		return this;
	}

	/**
	 * Configures how Cloudhopper will handle charset encoding for SMS messages.
	 * Charsets defined by the SMPP protocol may be different from NIO charsets.
	 * 
	 * This builder configures detection of the NIO charset defined by the SMS
	 * content handle by the Java application.
	 * 
	 * This builder also configures how conversion from NIO charset to SMPP
	 * charset is handled.
	 * 
	 * @return the builder to configure the charset handling
	 */
	public CharsetBuilder charset() {
		if (charsetBuilder == null) {
			charsetBuilder = new CharsetBuilder(this, environmentBuilder);
		}
		return charsetBuilder;
	}

	/**
	 * The bind command type (see {@link SmppBindType}).
	 * 
	 * Default value is {@link SmppBindType#TRANSMITTER}.
	 * 
	 * You can specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .bindType("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * 
	 * @param bindType
	 *            one or several property keys
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder bindType(String... bindType) {
		bindTypes.addAll(Arrays.asList(bindType));
		return this;
	}

	/**
	 * Set the bind command type.
	 * 
	 * This value preempts any other value defined by calling
	 * {@link #bindType(String...)} method.
	 * 
	 * If this method is called several times, only the last value is used.
	 * 
	 * Default value is {@link SmppBindType#TRANSMITTER}.
	 * 
	 * @param bindType
	 *            the type of the bind command
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder bindType(SmppBindType bindType) {
		this.bindType = bindType;
		return this;
	}

	/**
	 * Configures Cloudhopper session management (timeouts, retry, session
	 * name...).
	 * 
	 * @return the builder to configure the session management
	 */
	public SessionBuilder session() {
		if (sessionBuilder == null) {
			sessionBuilder = new SessionBuilder(this, environmentBuilder);
		}
		return sessionBuilder;
	}

	/**
	 * Overrides any previously defined Cloudhopper parameters to use the
	 * provided session.
	 * 
	 * <p>
	 * If this method is called several times, only the last session is used.
	 * 
	 * @param session
	 *            the Cloudhopper session to use
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder session(SmppSessionConfiguration session) {
		this.sessionConfiguration = session;
		return this;
	}

	/**
	 * The address_range parameter is used in the bind_receiver and
	 * bind_transceiver command to specify a set of SME addresses serviced by
	 * the ESME client. A single SME address may also be specified in the
	 * address_range parameter. UNIX Regular Expression notation should be used
	 * to specify a range of addresses. Messages addressed to any destination in
	 * this range shall be routed to the ESME.
	 * 
	 * Default to {@code null}.
	 * 
	 * Note: For IP addresses, it is only possible to specify a single IP
	 * address. A range of IP addresses are not allowed. IP version 6.0 is not
	 * currently supported in this version of the protocol.
	 * 
	 * Note: It is likely that the addr_range field is not supported or
	 * deliberately ignored on most Message Centres. The reason for this is that
	 * most carriers will not allow an ESME control the message routing as this
	 * can carry the risk of mis-routing mesages. In such circumstances, the
	 * ESME will be requested to set the field to NULL.
	 * 
	 * @param range
	 *            the address range
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder addressRange(Address range) {
		this.addressRange = range;
		return this;
	}

	/**
	 * Enable or disable SSL configuration and configure how SSL is handled.
	 * 
	 * See <a href=
	 * "https://github.com/fizzed/cloudhopper-smpp/blob/master/SSL.md">How to
	 * use SSL with cloudhopper-smpp</a>
	 * 
	 * @return the builder to configure SSL
	 */
	public SslBuilder ssl() {
		if (sslBuilder == null) {
			sslBuilder = new SslBuilder(this);
		}
		return sslBuilder;
	}

	/**
	 * Configure logs:
	 * <ul>
	 * <li>Enable/disable log of {@link Pdu}s</li>
	 * <li>Enable/disable log of bytes</li>
	 * </ul>
	 * 
	 * @return the builder to enable/disable some logs
	 */
	public LoggingBuilder logging() {
		if (loggingBuilder == null) {
			loggingBuilder = new LoggingBuilder(this);
		}
		return loggingBuilder;
	}

	public CloudhopperSMPPSender build() throws BuildException {
		PropertyResolver propertyResolver = buildPropertyResolver();
		CloudhopperSessionOptions sessionOpts = sessionBuilder.build();
		SmppSessionConfiguration session = buildSession(sessionOpts, propertyResolver);
		if (session.getHost() == null || session.getPort() == 0) {
			return null;
		}
		CloudhopperOptions options = buildOptions(sessionOpts);
		CloudhopperCharsetHandler charsetHandler = buildCharsetHandler();
		PhoneNumberTranslator phoneNumberTranslator = buildPhoneNumberTranslator();
		LOG.info("Sending SMS using Cloudhopper is registered");
		LOG.debug("SMPP server address: {}:{}", session.getHost(), session.getPort());
		return new CloudhopperSMPPSender(session, options, charsetHandler, phoneNumberTranslator);
	}

	private PropertyResolver buildPropertyResolver() {
		return environmentBuilder.build();
	}

	private PhoneNumberTranslator buildPhoneNumberTranslator() {
		return new CompositePhoneNumberTranslator(new DefaultHandler());
	}

	private CloudhopperCharsetHandler buildCharsetHandler() {
		if (charsetBuilder == null) {
			// @formatter:off
			return new CharsetBuilder(this, environmentBuilder)
					.convert("UTF-8", CharsetUtil.NAME_GSM)
					.detector()
						.defaultCharset("UTF-8")
						.and()
					.build();
			// @formatter:on
		}
		return charsetBuilder.build();
	}

	private SmppSessionConfiguration buildSession(CloudhopperSessionOptions sessionOpts, PropertyResolver propertyResolver) {
		if (sessionConfiguration != null) {
			return sessionConfiguration;
		}
		SmppSessionConfiguration session = new SmppSessionConfiguration(buildBindType(propertyResolver), getStringValue(propertyResolver, systemIds), getStringValue(propertyResolver, passwords));
		session.setHost(getHost(propertyResolver));
		session.setPort(getPort(propertyResolver));
		session.setSystemType(getStringValue(propertyResolver, systemTypes));
		session.setBindTimeout(sessionOpts.getBindTimeout());
		session.setConnectTimeout(sessionOpts.getConnectTimeout());
		session.setInterfaceVersion(interfaceVersion == null ? getInterfaceVersion(propertyResolver, interfaceVersions) : interfaceVersion);
		session.setName(sessionOpts.getSessionName());
		session.setRequestExpiryTimeout(sessionOpts.getRequestExpiryTimeout());
		session.setWindowMonitorInterval(sessionOpts.getWindowMonitorInterval());
		session.setWindowSize(sessionOpts.getWindowSize());
		session.setWindowWaitTimeout(sessionOpts.getWindowWaitTimeout());
		session.setWriteTimeout(sessionOpts.getWriteTimeout());
		session.setAddressRange(addressRange);
		configureSsl(session);
		configureLogs(session);
		return session;
	}

	private void configureLogs(SmppSessionConfiguration session) {
		if (loggingBuilder == null) {
			return;
		}
		LoggingOptions options = loggingBuilder.build();
		if (options != null) {
			session.setLoggingOptions(options);
		}
	}

	private void configureSsl(SmppSessionConfiguration session) {
		if (sslBuilder == null) {
			return;
		}
		SslConfiguration sslConfiguration = sslBuilder.build();
		session.setUseSsl(sslConfiguration != null);
		if (sslConfiguration != null) {
			session.setSslConfiguration(sslConfiguration);
		}
	}

	private SmppBindType buildBindType(PropertyResolver propertyResolver) {
		if (bindType != null) {
			return bindType;
		}
		String type = getStringValue(propertyResolver, bindTypes);
		if (type != null) {
			return SmppBindType.valueOf(type);
		}
		return SmppBindType.TRANSMITTER;
	}

	private Byte getInterfaceVersion(PropertyResolver propertyResolver, List<String> interfaceVersions) {
		String version = getStringValue(propertyResolver, interfaceVersions);
		if (version == null) {
			return SmppConstants.VERSION_3_4;
		}
		try {
			String fieldName = "VERSION_" + version.replaceAll("[.]", "_");
			Field field = SmppConstants.class.getField(fieldName);
			return field.getByte(SmppConstants.class);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			LOG.trace("Failed to get interface version using reflection", e);
		}
		if ("3.3".equals(version)) {
			return SmppConstants.VERSION_3_3;
		}
		if ("3.4".equals(version)) {
			return SmppConstants.VERSION_3_4;
		}
		if ("5.0".equals(version)) {
			return SmppConstants.VERSION_5_0;
		}
		throw new BuildException("Unknown interface version (" + version + ") for Cloudhopper session configuration");
	}

	private int getPort(PropertyResolver propertyResolver) {
		if (this.port != null) {
			return this.port;
		}
		Integer port = getIntValue(propertyResolver, ports);
		if (port != null) {
			return port;
		}
		return 0;
	}

	private String getHost(PropertyResolver propertyResolver) {
		return getStringValue(propertyResolver, hosts);
	}

	private String getStringValue(PropertyResolver propertyResolver, List<String> props) {
		return getValue(propertyResolver, props, String.class);
	}

	private Integer getIntValue(PropertyResolver propertyResolver, List<String> props) {
		return getValue(propertyResolver, props, Integer.class);
	}

	private <T> T getValue(PropertyResolver propertyResolver, List<String> props, Class<T> targetType) {
		return BuilderUtils.evaluate(props, propertyResolver, targetType);
	}

	private CloudhopperOptions buildOptions(CloudhopperSessionOptions sessionOpts) {
		Long responseTimeout = sessionOpts.getResponseTimeout() == null ? 5000L : sessionOpts.getResponseTimeout();
		Long unbindTimeout = sessionOpts.getUnbindTimeout() == null ? 5000L : sessionOpts.getUnbindTimeout();
		RetryExecutor connectRetry = sessionOpts.getConnectRetry();
		return new CloudhopperOptions(responseTimeout, unbindTimeout, connectRetry);
	}
}
