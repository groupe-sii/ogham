package fr.sii.notification.sms.builder;

import java.util.Properties;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSessionConfiguration;

import fr.sii.notification.core.builder.Builder;
import fr.sii.notification.core.charset.FixedCharsetProvider;
import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.core.util.BuilderUtils;
import fr.sii.notification.sms.SmsConstants;
import fr.sii.notification.sms.SmsConstants.SmppConstants.CloudhopperConstants;
import fr.sii.notification.sms.SmsConstants.SmppConstants.TimeoutConstants;
import fr.sii.notification.sms.exception.message.EncodingException;
import fr.sii.notification.sms.message.addressing.translator.PhoneNumberTranslator;
import fr.sii.notification.sms.sender.impl.CloudhopperSMPPSender;
import fr.sii.notification.sms.sender.impl.cloudhopper.CloudhopperOptions;
import fr.sii.notification.sms.sender.impl.cloudhopper.MapCloudhopperCharsetHandler;

/**
 * Builder that helps to construct the Cloudhopper SMPP implementation.
 * 
 * @author Aurélien Baudet
 *
 */
public class CloudhopperSMPPBuilder implements Builder<CloudhopperSMPPSender> {
	private static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * Properties that is used to initialize the session
	 */
	private Properties properties;
	
	/**
	 * The configuration for SMPP session to use
	 */
	private SmppSessionConfiguration sessionConfiguration;
	
	/**
	 * The Clouhopper specific options to use
	 */
	private CloudhopperOptions options;

	@Override
	public CloudhopperSMPPSender build() throws BuildException {
		if(options==null) {
			options = new CloudhopperOptions(CloudhopperConstants.DEFAULT_RESPONSE_TIMEOUT, CloudhopperConstants.DEFAULT_UNBIND_TIMEOUT);
		}
		
		// Default cloud hopper charset handler (UTF8 --> GSM)
		FixedCharsetProvider defaultCharsetProvider = new FixedCharsetProvider();
		MapCloudhopperCharsetHandler charsetHandler = new MapCloudhopperCharsetHandler(defaultCharsetProvider);
		try {
			charsetHandler.addCharset(DEFAULT_CHARSET, CharsetUtil.NAME_GSM);
		} catch (EncodingException e) {
			throw new BuildException("Unable to build default charset handler", e);
		}
		
		// Default phone numbertranslator
		PhoneNumberTranslator fallbackPhoneNumberTranslator = new DefaultPhoneNumberTranslatorBuilder().useFallbackDefaults().build();

		return new CloudhopperSMPPSender(sessionConfiguration, options, charsetHandler, fallbackPhoneNumberTranslator);
	}

	/**
	 * Tells the builder to use all default behaviors and values:
	 * <ul>
	 * <li>Use the system properties</li>
	 * <li>Create configuration for SMPP session based on system properties</li>
	 * <li>Create Cloudhopper options based on system properties</li>
	 * </ul>
	 * 
	 * @return this instance for fluent use
	 */
	public CloudhopperSMPPBuilder useDefaults() {
		useDefaults(BuilderUtils.getDefaultProperties());
		return this;
	}

	/**
	 * Tells the builder to use all default behaviors and values:
	 * <ul>
	 * <li>Use the provided properties</li>
	 * <li>Create configuration for SMPP session based on provided properties</li>
	 * <li>Create Cloudhopper options based on provided properties</li>
	 * </ul>
	 * 
	 * @param props
	 *            the properties to use
	 * @return this instance for fluent use
	 */
	public CloudhopperSMPPBuilder useDefaults(Properties props) {
		withProperties(props);
		generateOptionsFrom(props);
		generateSmppSessionConfigurationFrom(props);
		return this;
	}

	/**
	 * Set the properties to use for configuring Cloudhopper implementation.
	 * 
	 * @param properties
	 *            the properties to use
	 * @return this instance for fluent use
	 */
	public CloudhopperSMPPBuilder withProperties(Properties properties) {
		this.properties = properties;
		return this;
	}
	
	/**
	 * Provide your own options.
	 * 
	 * @param options
	 *            the options to use for Cloudhopper implementation
	 * @return this instance for fluent use
	 */
	public CloudhopperSMPPBuilder withOptions(CloudhopperOptions options) {
		this.options = options;
		return this;
	}
	
	/**
	 * Provide your own configuration for SMPP session.
	 * 
	 * @param configuration
	 *            the configuration for SMPP session used by Cloudhopper
	 *            implementation
	 * @return this instance for fluent use
	 */
	public CloudhopperSMPPBuilder withSmppSessionConfiguration(SmppSessionConfiguration configuration) {
		this.sessionConfiguration = configuration;
		return this;
	}

	/**
	 * Generate additional options from properties.
	 * 
	 * @param props
	 *            the properties to use for generating Cloudhopper options
	 * @return this instance for fluent use
	 */
	public CloudhopperSMPPBuilder generateOptionsFrom(Properties props) {
		options = new CloudhopperOptions(getProperty(props, CloudhopperConstants.RESPONSE_TIMEOUT_PROPERTY, CloudhopperConstants.DEFAULT_RESPONSE_TIMEOUT),
				getProperty(props, TimeoutConstants.UNBIND_PROPERTY, CloudhopperConstants.DEFAULT_UNBIND_TIMEOUT));
		return this;
	}
	
	/**
	 * Generate configuration for SMPP session from properties.
	 * 
	 * @param props
	 *            the properties to use for generating configuration for SMPP
	 *            session
	 * @return this instance for fluent use
	 */
	public CloudhopperSMPPBuilder generateSmppSessionConfigurationFrom(Properties props) {
		sessionConfiguration = new SmppSessionConfiguration(SmppBindType.TRANSMITTER, props.getProperty(SmsConstants.SmppConstants.SYSTEMID_PROPERTY), props.getProperty(SmsConstants.SmppConstants.PASSWORD_PROPERTY));
		sessionConfiguration.setHost(props.getProperty(SmsConstants.SmppConstants.HOST_PROPERTY));
		sessionConfiguration.setPort(Integer.parseInt(props.getProperty(SmsConstants.SmppConstants.PORT_PROPERTY)));
		sessionConfiguration.setBindTimeout(getProperty(props, TimeoutConstants.BIND_PROPERTY, SmppConstants.DEFAULT_BIND_TIMEOUT));
		sessionConfiguration.setConnectTimeout(getProperty(props, TimeoutConstants.CONNECTION_PROPERTY, SmppConstants.DEFAULT_CONNECT_TIMEOUT));
		String version = props.getProperty(SmsConstants.SmppConstants.INTERFACE_VERSION_PROPERTY, String.valueOf(SmppConstants.VERSION_3_4));
		switch(version) {
			case "3.3":
				sessionConfiguration.setInterfaceVersion(SmppConstants.VERSION_3_3);
			break;
			case "3.4":
			default:
				sessionConfiguration.setInterfaceVersion(SmppConstants.VERSION_3_4);
			break;
		}
		sessionConfiguration.setName(props.getProperty(CloudhopperConstants.SESSION_NAME_PROPERTY));
		sessionConfiguration.setRequestExpiryTimeout(getProperty(props, TimeoutConstants.REQUEST_EXPIRY_PROPERTY, SmppConstants.DEFAULT_REQUEST_EXPIRY_TIMEOUT));
		// TODO: manage ssl properties
//		sessionConfiguration.setSslConfiguration(value);
//		sessionConfiguration.setUseSsl(value);
		// TODO: allow to configure system type and bind type ?
//		sessionConfiguration.setSystemType(value);
//		sessionConfiguration.setType(bindType);
		sessionConfiguration.setWindowMonitorInterval(getProperty(props, SmsConstants.SmppConstants.WINDOW_MONITOR_INTERVAL_PROPERTY, SmppConstants.DEFAULT_WINDOW_MONITOR_INTERVAL));
		sessionConfiguration.setWindowSize(getProperty(props, SmsConstants.SmppConstants.WINDOW_SIZE_PROPERTY, SmppConstants.DEFAULT_WINDOW_SIZE));
		sessionConfiguration.setWindowWaitTimeout(getProperty(props, TimeoutConstants.WINDOW_WAIT_PROPERTY, SmppConstants.DEFAULT_WINDOW_WAIT_TIMEOUT));
		sessionConfiguration.setWriteTimeout(getProperty(props, CloudhopperConstants.WRITE_TIMEOUT_PROPERTY, SmppConstants.DEFAULT_WRITE_TIMEOUT));
		
		// TODO: externalize logs options
//		sessionConfiguration.getLoggingOptions().setLogBytes(false);
//		sessionConfiguration.setCountersEnabled(false);
		return this;
	}
	
	private int getProperty(Properties props, String key, int defaultValue) {
		return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
	}
	
	private long getProperty(Properties props, String key, long defaultValue) {
		return Long.parseLong(properties.getProperty(key, String.valueOf(defaultValue)));
	}
}
