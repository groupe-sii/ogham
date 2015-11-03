package fr.sii.ogham.sms.builder.cloudhopper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSessionConfiguration;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilderDelegate;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.retry.Retry;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.sms.builder.SmsBuilder;
import fr.sii.ogham.sms.message.addressing.translator.CompositePhoneNumberTranslator;
import fr.sii.ogham.sms.message.addressing.translator.DefaultHandler;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;
import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;
import fr.sii.ogham.sms.sender.impl.cloudhopper.CloudhopperCharsetHandler;
import fr.sii.ogham.sms.sender.impl.cloudhopper.CloudhopperOptions;

// TODO: default PhoneNumberTranslator
// TODO: ssl
// TODO: logs config
// TODO: system type + bindtype
public class CloudhopperBuilder extends AbstractParent<SmsBuilder> implements Builder<CloudhopperSMPPSender> {
	private static final Logger LOG = LoggerFactory.getLogger(CloudhopperBuilder.class);
	
	private EnvironmentBuilder<CloudhopperBuilder> environmentBuilder;
	private List<String> systemIds;
	private List<String> passwords;
	private List<String> hosts;
	private List<String> ports;
	private Integer port;
	private List<String> sessionNames;
	private Byte interfaceVersion;
	private List<String> interfaceVersions;
	private SessionBuilder sessionBuilder;
	private SmppSessionConfiguration sessionConfiguration;
	private CharsetBuilder charsetBuilder;

	public CloudhopperBuilder() {
		this(null);
		environmentBuilder = new SimpleEnvironmentBuilder<>(this);
	}

	public CloudhopperBuilder(SmsBuilder parent) {
		super(parent);
		systemIds = new ArrayList<>();
		passwords = new ArrayList<>();
		hosts = new ArrayList<>();
		ports = new ArrayList<>();
		interfaceVersions = new ArrayList<>();
		sessionNames = new ArrayList<>();
	}

	public EnvironmentBuilder<CloudhopperBuilder> environment() {
		if(environmentBuilder==null) {
			environmentBuilder = new SimpleEnvironmentBuilder<>(this);
		}
		return environmentBuilder;
	}
	
	public CloudhopperBuilder environment(EnvironmentBuilder<?> builder) {
		environmentBuilder = new EnvironmentBuilderDelegate<>(this, builder);
		return this;
	}

	public CloudhopperBuilder systemId(String... systemId) {
		systemIds.addAll(Arrays.asList(systemId));
		return this;
	}
	
	public CloudhopperBuilder password(String... password) {
		passwords.addAll(Arrays.asList(password));
		return this;
	}
	
	public CloudhopperBuilder host(String... host) {
		hosts.addAll(Arrays.asList(host));
		return this;
	}
	
	public CloudhopperBuilder port(String... port) {
		ports.addAll(Arrays.asList(port));
		return this;
	}
	
	public CloudhopperBuilder port(int port) {
		this.port = port;
		return this;
	}
	
	public CloudhopperBuilder sessionName(String... sessionName) {
		sessionNames.addAll(Arrays.asList(sessionName));
		return this;
	}
	
	public SslBuilder ssl() {
		return null;
	}
	
	public CloudhopperBuilder interfaceVersion(byte version) {
		interfaceVersion = version;
		return this;
	}
	
	public CloudhopperBuilder interfaceVersion(String... version) {
		interfaceVersions.addAll(Arrays.asList(version));
		return this;
	}
	
	public SessionBuilder session() {
		if(sessionBuilder==null) {
			sessionBuilder = new SessionBuilder(this, environmentBuilder);
		}
		return sessionBuilder;
	}
	
	public CloudhopperBuilder session(SmppSessionConfiguration session) {
		this.sessionConfiguration = session;
		return this;
	}
	
	public CharsetBuilder charset() {
		if(charsetBuilder==null) {
			charsetBuilder = new CharsetBuilder(this, environmentBuilder);
		}
		return charsetBuilder;
	}

	@Override
	public CloudhopperSMPPSender build() throws BuildException {
		PropertyResolver propertyResolver = environmentBuilder.build();
		CloudhopperSessionOptions sessionOpts = sessionBuilder.build();
		SmppSessionConfiguration session = buildSession(sessionOpts, propertyResolver);
		if(session.getHost()==null || session.getPort()==0) {
			return null;
		}
		CloudhopperOptions options = buildOptions(sessionOpts);
		CloudhopperCharsetHandler charsetHandler = buildCharsetHandler();
		PhoneNumberTranslator phoneNumberTranslator = buildPhoneNumberTranslator();
		return new CloudhopperSMPPSender(session, options, charsetHandler, phoneNumberTranslator);
	}
	
	private PhoneNumberTranslator buildPhoneNumberTranslator() {
		return new CompositePhoneNumberTranslator(new DefaultHandler());
	}

	private CloudhopperCharsetHandler buildCharsetHandler() {
		if(charsetBuilder==null) {
			return new CharsetBuilder(this, environmentBuilder)
					.defaultCharset("UTF-8")
					.convert("UTF-8", CharsetUtil.NAME_GSM)
					.build();
		}
		return charsetBuilder.build();
	}
	
	private SmppSessionConfiguration buildSession(CloudhopperSessionOptions sessionOpts, PropertyResolver propertyResolver) {
		if(sessionConfiguration!=null) {
			return sessionConfiguration;
		}
		SmppSessionConfiguration session = new SmppSessionConfiguration(SmppBindType.TRANSMITTER, getStringValue(propertyResolver, systemIds), getStringValue(propertyResolver, passwords));
		session.setHost(getHost(propertyResolver));
		session.setPort(getPort(propertyResolver));
		session.setBindTimeout(sessionOpts.getBindTimeout());
		session.setConnectTimeout(sessionOpts.getConnectTimeout());
		session.setInterfaceVersion(interfaceVersion==null ? getInterfaceVersion(propertyResolver, interfaceVersions) : interfaceVersion);
		session.setName(getStringValue(propertyResolver, sessionNames));
		session.setRequestExpiryTimeout(sessionOpts.getRequestExpiryTimeout());
		session.setWindowMonitorInterval(sessionOpts.getWindowMonitorInterval());
		session.setWindowSize(sessionOpts.getWindowSize());
		session.setWindowWaitTimeout(sessionOpts.getWindowWaitTimeout());
		session.setWriteTimeout(sessionOpts.getWriteTimeout());
		return session;
	}

	private Byte getInterfaceVersion(PropertyResolver propertyResolver, List<String> interfaceVersions) {
		String version = getStringValue(propertyResolver, interfaceVersions);
		if(version==null) {
			return SmppConstants.VERSION_3_4;
		}
		try {
			String fieldName = "VERSION_"+version.replaceAll("[.]", "_");
			Field field = SmppConstants.class.getField(fieldName);
			return field.getByte(SmppConstants.class);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// nothing to do
		}
		if("3.3".equals(version)) {
			return SmppConstants.VERSION_3_3;
		}
		if("3.4".equals(version)) {
			return SmppConstants.VERSION_3_4;
		}
		if("5.0".equals(version)) {
			return SmppConstants.VERSION_5_0;
		}
		throw new BuildException("Unknown interface version ("+version+") for Cloudhopper session configuration");
	}

	private int getPort(PropertyResolver propertyResolver) {
		if(this.port!=null) {
			return this.port;
		}
		Integer port = getIntValue(propertyResolver, ports);
		if(port!=null) {
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
		Long responseTimeout = sessionOpts.getResponseTimeout()==null ? 5000L : sessionOpts.getResponseTimeout();
		Long unbindTimeout = sessionOpts.getUnbindTimeout()==null ? 5000L : sessionOpts.getUnbindTimeout();
		Retry connectRetry = sessionOpts.getConnectRetry();
		return new CloudhopperOptions(responseTimeout, unbindTimeout, connectRetry);
	}
}
