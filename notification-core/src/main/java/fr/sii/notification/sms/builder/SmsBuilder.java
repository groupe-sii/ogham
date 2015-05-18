package fr.sii.notification.sms.builder;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import fr.sii.notification.core.builder.NotificationSenderBuilder;
import fr.sii.notification.core.condition.Condition;
import fr.sii.notification.core.condition.RequiredPropertyCondition;
import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.core.filler.PropertiesFiller;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.sender.ConditionalSender;
import fr.sii.notification.core.sender.FillerSender;
import fr.sii.notification.core.sender.NotificationSender;
import fr.sii.notification.core.util.BuilderUtil;
import fr.sii.notification.sms.sender.SmsSender;
import fr.sii.notification.sms.sender.impl.OvhSmsSender;

public class SmsBuilder implements NotificationSenderBuilder<ConditionalSender> {

	private static final String PROPERTIES_PREFIX = "notification.sms";

	private ConditionalSender sender;
	
	private SmsSender smsSender;

	public SmsBuilder() {
		super();
		sender = smsSender = new SmsSender();
	}
	
	@Override
	public List<ConditionalSender> build() throws BuildException {
		return Arrays.asList(sender);
	}
	
	public SmsBuilder useDefaults() {
		return useDefaults(BuilderUtil.getDefaultProperties());
	}

	public SmsBuilder useDefaults(Properties properties) {
		registerDefaultImplementations(properties);
		withConfigurationFiller(properties);
		return null;
	}

	public SmsBuilder registerImplementation(Condition<Message> condition, NotificationSender implementation) {
		smsSender.addImplementation(condition, implementation);
		return this;
	}
	
	public SmsBuilder registerDefaultImplementations() {
		return registerDefaultImplementations(System.getProperties());
	}
	
	public SmsBuilder registerDefaultImplementations(Properties properties) {
		try {
			registerImplementation(new RequiredPropertyCondition<Message>("notification.sms.ovh.app.key", properties), new OvhSmsSender());
		} catch(Throwable e) {
			// nothing to do
		}
		return this;
	}
	
	public SmsBuilder withConfigurationFiller(Properties props, String baseKey) {
		sender = new FillerSender(new PropertiesFiller(props, baseKey), sender);
		return this;
	}
	
	public SmsBuilder withConfigurationFiller(Properties props) {
		sender = new FillerSender(new PropertiesFiller(props, PROPERTIES_PREFIX), sender);
		return this;
	}
	
	public SmsBuilder withConfigurationFiller() {
		withConfigurationFiller(BuilderUtil.getDefaultProperties());
		return this;
	}
}
