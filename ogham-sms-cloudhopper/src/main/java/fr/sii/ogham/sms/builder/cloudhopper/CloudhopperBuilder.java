package fr.sii.ogham.sms.builder.cloudhopper;

import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM;
import static com.cloudhopper.smpp.SmppBindType.TRANSMITTER;
import static fr.sii.ogham.sms.builder.cloudhopper.InterfaceVersion.VERSION_3_4;

import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.SmppSessionHandler;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.Pdu;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.ssl.SslConfiguration;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.LoggingOptions;

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
import fr.sii.ogham.core.retry.RetryExecutor;
import fr.sii.ogham.sms.builder.SmsBuilder;
import fr.sii.ogham.sms.builder.cloudhopper.UserDataBuilder.UserDataPropValues;
import fr.sii.ogham.sms.encoder.Encoder;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.message.addressing.translator.CompositePhoneNumberTranslator;
import fr.sii.ogham.sms.message.addressing.translator.DefaultHandler;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;
import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;
import fr.sii.ogham.sms.sender.impl.cloudhopper.CloudhopperOptions;
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.CloudhopperCharsetSupportingEncoder;
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.NamedCharset;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.CharsetMapToCharacterEncodingGroupDataCodingProvider;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.DataCodingProvider;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.MessagePreparator;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.ShortMessagePreparator;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.TlvMessagePayloadMessagePreparator;
import fr.sii.ogham.sms.splitter.GsmMessageSplitter;
import fr.sii.ogham.sms.splitter.MessageSplitter;
import fr.sii.ogham.sms.splitter.NoSplitMessageSplitter;
import fr.sii.ogham.sms.splitter.ReferenceNumberGenerator;

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
 *       .host()
 *         .properties("${custom.property.for.host}")
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
public class CloudhopperBuilder extends AbstractParent<SmsBuilder> implements Builder<CloudhopperSMPPSender> {
	private static final long DEFAULT_UNBIND_TIMEOUT = 5000L;

	private static final long DEFAULT_RESPONSE_TIMEOUT = 5000L;

	private static final Logger LOG = LoggerFactory.getLogger(CloudhopperBuilder.class);

	private final ReadableEncoderBuilder sharedEncoderBuilder;
	private EnvironmentBuilder<CloudhopperBuilder> environmentBuilder;
	private final ConfigurationValueBuilderHelper<CloudhopperBuilder, String> systemIdValueBuilder;
	private final ConfigurationValueBuilderHelper<CloudhopperBuilder, String> passwordValueBuilder;
	private final ConfigurationValueBuilderHelper<CloudhopperBuilder, String> hostValueBuilder;
	private final ConfigurationValueBuilderHelper<CloudhopperBuilder, Integer> portValueBuilder;
	private final ConfigurationValueBuilderHelper<CloudhopperBuilder, String> systemTypeValueBuilder;
	private final ConfigurationValueBuilderHelper<CloudhopperBuilder, InterfaceVersion> interfaceVersionValueBuilder;
	private final ConfigurationValueBuilderHelper<CloudhopperBuilder, SmppBindType> bindTypeValueBuilder;
	private SessionBuilder sessionBuilder;
	private SmppSessionConfiguration sessionConfiguration;
	private Address addressRange;
	private SslBuilder sslBuilder;
	private LoggingBuilder loggingBuilder;
	private SmppClientSupplier clientSupplier;
	private SmppSessionHandlerSupplier smppSessionHandler;
	private MessageSplitterBuilder messageSplitterBuilder;
	private EncoderBuilder encoderBuilder;
	private UserDataBuilder userDataBuilder;
	private DataCodingSchemeBuilder dataCodingBuilder;
	private MessagePreparator preparator;

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
		sharedEncoderBuilder = new ReadableEncoderBuilder();
		systemIdValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class);
		passwordValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class);
		hostValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class);
		portValueBuilder = new ConfigurationValueBuilderHelper<>(this, Integer.class);
		interfaceVersionValueBuilder = new ConfigurationValueBuilderHelper<>(this, InterfaceVersion.class);
		systemTypeValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class);
		bindTypeValueBuilder = new ConfigurationValueBuilderHelper<>(this, SmppBindType.class);
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
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #systemId()}.
	 * 
	 * <pre>
	 * .systemId("my-system-id")
	 * .systemId()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-system-id")
	 * </pre>
	 * 
	 * <pre>
	 * .systemId("my-system-id")
	 * .systemId()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-system-id")
	 * </pre>
	 * 
	 * In both cases, {@code systemId("my-system-id")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param systemId
	 *            the system_id value
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder systemId(String systemId) {
		systemIdValueBuilder.setValue(systemId);
		return this;
	}
	
	
	/**
	 * The system_id parameter is used to identify an ESME ( External Short
	 * Message Entity) or an SMSC (Short Message Service Centre) at bind time.
	 * An ESME system_id identifies the ESME or ESME agent to the SMSC. The SMSC
	 * system_id provides an identification of the SMSC to the ESME.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .systemId()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-system-id")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #systemId(String)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .systemId("my-system-id")
	 * .systemId()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-system-id")
	 * </pre>
	 * 
	 * The value {@code "my-system-id"} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<CloudhopperBuilder, String> systemId() {
		return systemIdValueBuilder;
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
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #systemType()}.
	 * 
	 * <pre>
	 * .systemType("my-system-type")
	 * .systemType()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-system-type")
	 * </pre>
	 * 
	 * <pre>
	 * .systemType("my-system-type")
	 * .systemType()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-system-type")
	 * </pre>
	 * 
	 * In both cases, {@code systemType("my-system-type")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param systemType
	 *            the system type value
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder systemType(String systemType) {
		systemTypeValueBuilder.setValue(systemType);
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
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .systemType()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("defaut-system-type")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #systemType(String)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .systemType("my-system-type")
	 * .systemType()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("defaut-system-type")
	 * </pre>
	 * 
	 * The value {@code "my-system-type"} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<CloudhopperBuilder, String> systemType() {
		return systemTypeValueBuilder;
	}
	
	/**
	 * The password parameter is used by the SMSC to authenticate the identity
	 * of the binding ESME. The Service Provider may require ESME’s to provide a
	 * password when binding to the SMSC. This password is normally issued by
	 * the SMSC system administrator. The password parameter may also be used by
	 * the ESME to authenticate the identity of the binding SMSC (e.g. in the
	 * case of the outbind operation).
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
	 *            the password used to authenticate
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder password(String password) {
		passwordValueBuilder.setValue(password);
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
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .password()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-password")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #password(String)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .password("my-password")
	 * .password()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-password")
	 * </pre>
	 * 
	 * The value {@code "my-password"} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<CloudhopperBuilder, String> password() {
		return passwordValueBuilder;
	}
	
	/**
	 * The SMPP server host (IP or address).
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #host()}.
	 * 
	 * <pre>
	 * .host("localhost")
	 * .host()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-host")
	 * </pre>
	 * 
	 * <pre>
	 * .host("localhost")
	 * .host()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-host")
	 * </pre>
	 * 
	 * In both cases, {@code host("localhost")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param host
	 *            the host address
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder host(String host) {
		hostValueBuilder.setValue(host);
		return this;
	}

	
	/**
	 * The SMPP server host (IP or address).
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .host()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-host")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #host(String)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .host("localhost")
	 * .host()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-host")
	 * </pre>
	 * 
	 * The value {@code "localhost"} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<CloudhopperBuilder, String> host() {
		return hostValueBuilder;
	}
	
	/**
	 * Set the SMPP server port.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #port()}.
	 * 
	 * <pre>
	 * .port(2775)
	 * .port()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(1775)
	 * </pre>
	 * 
	 * <pre>
	 * .port(2775)
	 * .port()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(1775)
	 * </pre>
	 * 
	 * In both cases, {@code port(2775)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param port
	 *            the SMPP server port
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder port(Integer port) {
		portValueBuilder.setValue(port);
		return this;
	}

	
	/**
	 * Set the SMPP server port.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .port()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(1775)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #port(Integer)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .port(2775)
	 * .port()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(1775)
	 * </pre>
	 * 
	 * The value {@code 2775} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<CloudhopperBuilder, Integer> port() {
		return portValueBuilder;
	}
	
	/**
	 * The SMPP protocol version (one of {@link InterfaceVersion#VERSION_3_3},
	 * {@link InterfaceVersion#VERSION_3_4}, {@link InterfaceVersion#VERSION_5_0}).
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #interfaceVersion()}.
	 * 
	 * <pre>
	 * .interfaceVersion(InterfaceVersion.VERSION_5_0)
	 * .interfaceVersion()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(InterfaceVersion.VERSION_3_4)
	 * </pre>
	 * 
	 * <pre>
	 * .interfaceVersion(InterfaceVersion.VERSION_5_0)
	 * .interfaceVersion()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(InterfaceVersion.VERSION_3_4)
	 * </pre>
	 * 
	 * In both cases, {@code interfaceVersion(InterfaceVersion.VERSION_5_0)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param version
	 *            the version of the SMPP protocol
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder interfaceVersion(InterfaceVersion version) {
		interfaceVersionValueBuilder.setValue(version);
		return this;
	}
	
	/**
	 * The SMPP protocol version (one of {@link SmppConstants#VERSION_3_3},
	 * {@link SmppConstants#VERSION_3_4}, {@link SmppConstants#VERSION_5_0}).
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #interfaceVersion()}.
	 * 
	 * <pre>
	 * .interfaceVersion(SmppConstants.VERSION_5_0)
	 * .interfaceVersion()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(SmppConstants.VERSION_3_4)
	 * </pre>
	 * 
	 * <pre>
	 * .interfaceVersion(SmppConstants.VERSION_5_0)
	 * .interfaceVersion()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(SmppConstants.VERSION_3_4)
	 * </pre>
	 * 
	 * In both cases, {@code interfaceVersion(SmppConstants.VERSION_5_0)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param version
	 *            the version of the SMPP protocol
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder interfaceVersion(Byte version) {
		interfaceVersionValueBuilder.setValue(InterfaceVersion.fromValue(version));
		return this;
	}
	
	/**
	 * 	 * The SMPP protocol version (one of {@link InterfaceVersion#VERSION_3_3},
	 * {@link InterfaceVersion#VERSION_3_4}, {@link InterfaceVersion#VERSION_5_0}).

	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .interfaceVersion()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(InterfaceVersion.VERSION_3_4)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #interfaceVersion(InterfaceVersion)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .interfaceVersion(InterfaceVersion.VERSION_5_0)
	 * .interfaceVersion()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(InterfaceVersion.VERSION_3_4)
	 * </pre>
	 * 
	 * The value {@code InterfaceVersion.VERSION_5_0} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<CloudhopperBuilder, InterfaceVersion> interfaceVersion() {
		return interfaceVersionValueBuilder;
	}
	
	/**
	 * The bind command type (see {@link SmppBindType}).
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #bindType()}.
	 * 
	 * <pre>
	 * .bindType(SmppBindType.TRANSCEIVER)
	 * .bindType()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(SmppBindType.RECEIVER)
	 * </pre>
	 * 
	 * <pre>
	 * .bindType(SmppBindType.TRANSCEIVER)
	 * .bindType()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(SmppBindType.RECEIVER)
	 * </pre>
	 * 
	 * In both cases, {@code bindType(SmppBindType.TRANSCEIVER)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param bindType
	 *            the bind type
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder bindType(SmppBindType bindType) {
		bindTypeValueBuilder.setValue(bindType);
		return this;
	}

	
	/**
	 * The bind command type (see {@link SmppBindType}).
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .bindType()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(SmppBindType.RECEIVER)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #bindType(SmppBindType)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .bindType(SmppBindType.TRANSCEIVER)
	 * .bindType()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(SmppBindType.RECEIVER)
	 * </pre>
	 * 
	 * The value {@code SmppBindType.TRANSCEIVER} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<CloudhopperBuilder, SmppBindType> bindType() {
		return bindTypeValueBuilder;
	}

	/**
	 * Configures how Cloudhopper will encode SMS messages. Charsets defined by
	 * the SMPP protocol may be different from NIO charsets.
	 * 
	 * <p>
	 * The encoder will be used to transform Java {@link String} into a byte
	 * array that is understandable by SMPP servers.
	 * 
	 * <p>
	 * This builder configures encoders for both messages that are split and
	 * message that are not split.
	 * 
	 * <p>
	 * This builder allows to configure:
	 * <ul>
	 * <li>Enable/disable the standard GSM encoders (GSM 7-bit, GSM 8-bit and
	 * UCS-2) as defined in
	 * <a href="https://en.wikipedia.org/wiki/GSM_03.38">GSM 03.38
	 * specification</a>. It also allows to define different priority order</li>
	 * <li>Enable/disable automatic guessing of encoding (based on previously
	 * registered priorities).</li>
	 * <li>Define a fallback encoder based on {@link Charset}</li>
	 * <li>Provide custom {@link Encoder}s</li>
	 * </ul>
	 * 
	 * <pre>
	 * {@code
	 * .encoder()
	 *    .gsm7()
	 *      .properties("${ogham.sms.cloudhopper.encoder.gsm-7bit.priority}", "${ogham.sms.encoder.gsm-7bit.priority}")
	 *      .defaultValue(100000)
	 *      .and()
	 *    .gsm8()
	 *      .properties("${ogham.sms.cloudhopper.encoder.gsm-8bit.priority}", "${ogham.sms.encoder.gsm-8bit.priority}")
	 *      .defaultValue(99000)
	 *      .and()
	 *    .ucs2()
	 *      .properties("${ogham.sms.cloudhopper.encoder.ucs-2.priority}", "${ogham.sms.encoder.ucs-2.priority}")
	 *      .defaultValue(98000)
	 *      .and()
	 *    .autoGuess()
	 *      .properties("${ogham.sms.cloudhopper.encoder.auto-guess}", "${ogham.sms.encoder.auto-guess}")
	 *      .defaultValue(true)
	 *      .and()
	 *    .fallback()
	 *      .properties("${ogham.sms.cloudhopper.encoder.default-charset}")
	 *      .defaultValue(CharsetUtil.NAME_GSM)
	 *      .and()
	 *    .customEncoder(new MyCustomEncoder(), 50000)
	 * }
	 * </pre>
	 * 
	 * @return the builder to configure the encoder
	 */
	public EncoderBuilder encoder() {
		if (encoderBuilder == null) {
			encoderBuilder = new EncoderBuilder(this, environmentBuilder);
			sharedEncoderBuilder.update(encoderBuilder);
		}
		return encoderBuilder;
	}

	/**
	 * Configures how Cloudhopper will split messages.
	 * 
	 * <p>
	 * The splitter will check if the whole message can fit in a single segment.
	 * If not the splitter will split the whole message in several segments with
	 * a header to indicate splitting information such as number of segments,
	 * reference number and current segment number.
	 * 
	 * <p>
	 * {@link Encoder} configured using {@link #encoder()} is used to encode
	 * each segment.
	 * 
	 * <p>
	 * If automatic guessing of best standard encoder is enabled for
	 * {@link Encoder} (using {@code encoder().autoGuess(true)}), and message
	 * splitting is enabled, then standard message splitting is configured such
	 * as:
	 * <ul>
	 * <li>If GSM 7-bit encoder is enabled, {@link GsmMessageSplitter} is used
	 * to split messages that support this encoding. If whole message can fit in
	 * a single segment of 160 characters. Longer message is split into segments
	 * of either 153 characters or 152 characters (depending on reference number
	 * generation, see {@link ReferenceNumberGenerator})</li>
	 * <li>If GSM 8-bit encoder is enabled, {@link GsmMessageSplitter} is used
	 * to split messages that support this encoding. If whole message can fit in
	 * a single segment of 140 characters. Longer message is split into segments
	 * of either 134 characters or 133 characters (depending on reference number
	 * generation, see {@link ReferenceNumberGenerator})</li>
	 * <li>If UCS-2 encoder is enabled, {@link GsmMessageSplitter} is used to
	 * split messages that support this encoding. If whole message can fit in a
	 * single segment of 70 characters. Longer message is split into segments of
	 * either 67 characters or 66 characters (depending on reference number
	 * generation, see {@link ReferenceNumberGenerator})</li>
	 * </ul>
	 * 
	 * Each registered splitter uses the same priority as associated
	 * {@link Encoder}.
	 * 
	 * If you don't want standard message splitting based on supported
	 * {@link Encoder}s, you can either disable message splitting or provide a
	 * custom splitter with higher priority.
	 * 
	 * <p>
	 * This builder allows to configure:
	 * <ul>
	 * <li>Enable/disable message splitting</li>
	 * <li>Provide a custom split strategy</li>
	 * <li>Choose strategy for reference number generation</li>
	 * </ul>
	 * 
	 * <p>
	 * Examples of usage:
	 * 
	 * <pre>
	 * {@code
	 * .splitter()
	 *   .enable()
	 *     .properties("${ogham.sms.cloudhopper.split.enable}", "${ogham.sms.split.enable}")
	 *     .defaultValue(true)
	 *     .and()
	 *   .customSplitter(new MyCustomSplitter(), 100000)
	 *   .referenceNumber()
	 *     .random()
	 *     .random(new Random())
	 *     .generator(new MyCustomReferenceNumberGenerator())
	 * }
	 * </pre>
	 * 
	 * @return the builder to configure message splitting
	 */
	public MessageSplitterBuilder splitter() {
		if (messageSplitterBuilder == null) {
			messageSplitterBuilder = new MessageSplitterBuilder(this, environmentBuilder, sharedEncoderBuilder);
		}
		return messageSplitterBuilder;
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
	 * can carry the risk of mis-routing messages. In such circumstances, the
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
			sslBuilder = new SslBuilder(this, environmentBuilder);
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

	/**
	 * By default, {@link CloudhopperSMPPSender} uses {@link DefaultSmppClient}
	 * client. This option provides a way to use another {@link SmppClient}.
	 * 
	 * @param supplier
	 *            an implementation that provides an instance of a
	 *            {@link SmppClient}
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder clientSupplier(SmppClientSupplier supplier) {
		this.clientSupplier = supplier;
		return this;
	}

	/**
	 * By default, {@link CloudhopperSMPPSender} uses
	 * {@link DefaultSmppSessionHandler}. This option provides a way to use
	 * another {@link SmppSessionHandler}.
	 * 
	 * @param supplier
	 *            an implementation that provides an instance of a
	 *            {@link SmppSessionHandler}
	 * @return this instance for fluent chaining
	 */
	public CloudhopperBuilder smppSessionHandlerSupplier(SmppSessionHandlerSupplier supplier) {
		this.smppSessionHandler = supplier;
		return this;
	}

	/**
	 * {@link Sms} message is converted to {@link SubmitSm}(s) using a
	 * {@link MessagePreparator}.
	 * 
	 * <p>
	 * You can provide a custom {@link MessagePreparator} instance if the
	 * default behavior doesn't fit your needs.
	 * </p>
	 * 
	 * <p>
	 * If a custom {@link MessagePreparator} is set, any other preparator (using
	 * {@link #userData()}) is not used.
	 * </p>
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * </p>
	 * 
	 * <p>
	 * If {@code null} value is provided, then custom {@link MessagePreparator}
	 * is disabled. Other configured preparators are used (using
	 * {@link #userData()}).
	 * </p>
	 * 
	 * @param preparator
	 *            the custom preprator instance
	 * @return this instance for fluent chaining
	 * @see #userData()
	 */
	public CloudhopperBuilder messagePreparator(MessagePreparator preparator) {
		this.preparator = preparator;
		return this;
	}

	/**
	 * SMS message (named "User Data" in SMPP specification) can be transmitted
	 * using:
	 * <ul>
	 * <li>Either {@code short_message} field (standard field for "User
	 * Data").</li>
	 * <li>Or {@code message_payload} optional parameter.</li>
	 * </ul>
	 * 
	 * <p>
	 * This builder allow to configure which strategy to use for sending
	 * message:
	 * <ul>
	 * <li>Either use {@code short_message} field</li>
	 * <li>Or use {@code message_payload} field</li>
	 * </ul>
	 * 
	 * <p>
	 * The result of {@link #userData()} configuration affects the message
	 * preparation strategy.
	 * </p>
	 * 
	 * <p>
	 * Examples of usage:
	 * 
	 * <pre>
	 * {@code
	 * .userData()
	 *   .useShortMessage()
	 *     .properties("${ogham.sms.cloudhopper.user-data.use-short-message}", "${ogham.sms.user-data.use-short-message}")
	 *     .defaultValue(true)
	 *     .and()
	 *   .useTlvMessagePayload()
	 *     .properties("${ogham.sms.cloudhopper.user-data.use-tlv-message-payload}", "${ogham.sms.user-data.use-tlv-message-payload}")
	 *     .defaultValue(false)
	 * }
	 * </pre>
	 * 
	 * If any of {@code ogham.sms.cloudhopper.user-data.use-short-message}
	 * property or {@code ogham.sms.user-data.use-short-message} property is set
	 * to true, it uses {@code short_message} field.
	 * 
	 * If any of {@code ogham.sms.cloudhopper.user-data.use-tlv-message-payload}
	 * property or {@code ogham.sms.user-data.use-tlv-message-payload} property
	 * is set to true, it uses {@code message_payload} field.
	 * 
	 * If none of the above properties is set, it uses {@code short_message}
	 * field is used (last value of {@code shortMessage} is set to
	 * {@code "true"}).
	 * 
	 * <p>
	 * If {@link #userData()} is not configured at all, then default behavior is
	 * used ({@code short_message} field is used).
	 * </p>
	 * 
	 * @return the builder to configure how the "User Data" is sent
	 */
	public UserDataBuilder userData() {
		if (userDataBuilder == null) {
			userDataBuilder = new UserDataBuilder(this, environmentBuilder);
		}
		return userDataBuilder;
	}

	/**
	 * Data Coding Scheme is a one-octet field in Short Messages (SM) and Cell
	 * Broadcast Messages (CB) which carries a basic information how the
	 * recipient handset should process the received message. The information
	 * includes:
	 * <ul>
	 * <li>the character set or message coding which determines the encoding of
	 * the message user data</li>
	 * <li>the message class which determines to which component of the Mobile
	 * Station (MS) or User Equipment (UE) should be the message delivered</li>
	 * <li>the request to automatically delete the message after reading</li>
	 * <li>the state of flags indicating presence of unread voicemail, fax,
	 * e-mail or other messages</li>
	 * <li>the indication that the message content is compressed</li>
	 * <li>the language of the cell broadcast message</li>
	 * </ul>
	 * The field is described in 3GPP 23.040 and 3GPP 23.038 under the name
	 * TP-DCS (see <a href=
	 * "https://en.wikipedia.org/wiki/Data_Coding_Scheme#SMS_Data_Coding_Scheme">SMS
	 * Data Coding Scheme</a>).
	 * 
	 * SMPP 3.4 introduced a new list of {@code data_coding} values (see
	 * <a href="https://en.wikipedia.org/wiki/Short_Message_Peer-to-Peer">Short
	 * Message Peer to Peer</a>).
	 * 
	 * <p>
	 * This builder allows to configure how Data Coding Scheme value is
	 * determined:
	 * <ul>
	 * <li>Use automatic mode base on interface version (see
	 * {@link #interfaceVersion(InterfaceVersion)} and {@link #interfaceVersion(Byte)})
	 * and charset encoding (see {@link #encoder()}) used to encode the message
	 * ("User Data")</li>
	 * <li>Use a fixed value used for every message</li>
	 * <li>Use a custom implementation</li>
	 * </ul>
	 * 
	 * <p>
	 * Examples of usage:
	 * 
	 * <pre>
	 * {@code
	 * .dataCodingScheme()
	 *   .auto("${ogham.sms.cloudhopper.data-coding-scheme.auto.enable}")
	 *   .value("${ogham.sms.cloudhopper.data-coding-scheme.value}")
	 *   .custom(new MyCustomDataCodingProvider())
	 * }
	 * </pre>
	 * 
	 * See {@link DataCodingSchemeBuilder#auto(Boolean)},
	 * {@link DataCodingSchemeBuilder#value(Byte)} and
	 * {@link DataCodingSchemeBuilder#custom(DataCodingProvider)} for more
	 * information.
	 * 
	 * 
	 * @return the builder to configure how to determine Data Coding Scheme
	 *         value
	 */
	public DataCodingSchemeBuilder dataCodingScheme() {
		if (dataCodingBuilder == null) {
			dataCodingBuilder = new DataCodingSchemeBuilder(this, environmentBuilder, this::getInterfaceVersion);
		}
		return dataCodingBuilder;
	}

	@Override
	public CloudhopperSMPPSender build() {
		PropertyResolver propertyResolver = buildPropertyResolver();
		CloudhopperSessionOptions sessionOpts = sessionBuilder.build();
		SmppSessionConfiguration session = buildSession(sessionOpts, propertyResolver);
		if (session.getHost() == null || session.getPort() == 0) {
			return null;
		}
		CloudhopperOptions options = buildOptions(sessionOpts);
		LOG.info("Sending SMS using Cloudhopper is registered");
		LOG.debug("SMPP server address: {}:{}", session.getHost(), session.getPort());
		return new CloudhopperSMPPSender(session, options, buildPreparator(), buildClientSupplier(), buildSmppSessionHandler());
	}

	private MessagePreparator buildPreparator() {
		if (preparator != null) {
			return preparator;
		}
		if (userDataBuilder != null) {
			UserDataPropValues values = userDataBuilder.build();
			if (values.isUseShortMessage()) {
				return buildShortMessagePreparator();
			}
			if (values.isUseTlvMessagePayload()) {
				return buildTlvMessagePayloadMessagePreparator();
			}
		}
		return buildShortMessagePreparator();
	}

	private MessagePreparator buildShortMessagePreparator() {
		return new ShortMessagePreparator(buildSplitter(buildEncoder()), buildDataCodingProvider(), buildPhoneNumberTranslator());
	}

	private MessagePreparator buildTlvMessagePayloadMessagePreparator() {
		return new TlvMessagePayloadMessagePreparator(buildSplitter(buildEncoder()), buildDataCodingProvider(), buildPhoneNumberTranslator());
	}

	private Encoder buildEncoder() {
		if (encoderBuilder == null) {
			return new CloudhopperCharsetSupportingEncoder(NamedCharset.from(NAME_GSM));
		}
		return encoderBuilder.build();
	}

	private DataCodingProvider buildDataCodingProvider() {
		if (dataCodingBuilder == null) {
			return new CharsetMapToCharacterEncodingGroupDataCodingProvider();
		}
		return dataCodingBuilder.build();
	}

	private MessageSplitter buildSplitter(Encoder encoder) {
		if (messageSplitterBuilder == null) {
			return new NoSplitMessageSplitter(encoder);
		}
		MessageSplitter splitter = messageSplitterBuilder.build();
		if (splitter != null) {
			return splitter;
		}
		return new NoSplitMessageSplitter(encoder);
	}

	private SmppClientSupplier buildClientSupplier() {
		if (clientSupplier == null) {
			return DefaultSmppClient::new;
		}
		return clientSupplier;
	}

	private SmppSessionHandlerSupplier buildSmppSessionHandler() {
		if (smppSessionHandler == null) {
			return () -> null;
		}
		return smppSessionHandler;
	}

	private PropertyResolver buildPropertyResolver() {
		return environmentBuilder.build();
	}

	private PhoneNumberTranslator buildPhoneNumberTranslator() {
		// TODO: allow configuration of fallback phone number translator
		return new CompositePhoneNumberTranslator(new DefaultHandler());
	}

	private SmppSessionConfiguration buildSession(CloudhopperSessionOptions sessionOpts, PropertyResolver propertyResolver) {
		if (sessionConfiguration != null) {
			return sessionConfiguration;
		}
		SmppSessionConfiguration session = new SmppSessionConfiguration(buildBindType(propertyResolver), systemIdValueBuilder.getValue(propertyResolver), passwordValueBuilder.getValue(propertyResolver));
		session.setHost(getHost(propertyResolver));
		session.setPort(getPort(propertyResolver));
		session.setSystemType(systemTypeValueBuilder.getValue(propertyResolver));
		set(session::setBindTimeout, sessionOpts::getBindTimeout);
		set(session::setConnectTimeout, sessionOpts::getConnectTimeout);
		session.setInterfaceVersion(getInterfaceVersion(propertyResolver));
		set(session::setName, sessionOpts::getSessionName);
		set(session::setRequestExpiryTimeout, sessionOpts::getRequestExpiryTimeout);
		set(session::setWindowMonitorInterval, sessionOpts::getWindowMonitorInterval);
		set(session::setWindowSize, sessionOpts::getWindowSize);
		set(session::setWindowWaitTimeout, sessionOpts::getWindowWaitTimeout);
		set(session::setWriteTimeout, sessionOpts::getWriteTimeout);
		session.setAddressRange(addressRange);
		configureSsl(session);
		configureLogs(session);
		return session;
	}

	private static <T> void set(Consumer<T> setter, Supplier<T> getter) {
		T value = getter.get();
		if (value != null) {
			setter.accept(value);
		}
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
		return bindTypeValueBuilder.getValue(propertyResolver, TRANSMITTER);
	}

	private Byte getInterfaceVersion(PropertyResolver propertyResolver) {
		InterfaceVersion version = interfaceVersionValueBuilder.getValue(propertyResolver, VERSION_3_4);
		return version.value();
	}

	private int getPort(PropertyResolver propertyResolver) {
		return portValueBuilder.getValue(propertyResolver, 0);
	}

	private String getHost(PropertyResolver propertyResolver) {
		return hostValueBuilder.getValue(propertyResolver);
	}

	private static CloudhopperOptions buildOptions(CloudhopperSessionOptions sessionOpts) {
		Long responseTimeout = sessionOpts.getResponseTimeout() == null ? DEFAULT_RESPONSE_TIMEOUT : sessionOpts.getResponseTimeout();
		Long unbindTimeout = sessionOpts.getUnbindTimeout() == null ? DEFAULT_UNBIND_TIMEOUT : sessionOpts.getUnbindTimeout();
		RetryExecutor connectRetry = sessionOpts.getConnectRetry();
		return new CloudhopperOptions(responseTimeout, unbindTimeout, connectRetry, sessionOpts.isKeepSession());
	}

}
