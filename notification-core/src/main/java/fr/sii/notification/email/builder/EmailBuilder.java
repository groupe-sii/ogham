package fr.sii.notification.email.builder;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.beanutils.ConvertUtils;

import fr.sii.notification.core.builder.NotificationSenderBuilder;
import fr.sii.notification.core.condition.Condition;
import fr.sii.notification.core.condition.RequiredClassCondition;
import fr.sii.notification.core.converter.EmailAddressConverter;
import fr.sii.notification.core.exception.BuildException;
import fr.sii.notification.core.filler.PropertiesFiller;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.sender.ConditionalSender;
import fr.sii.notification.core.sender.FillerSender;
import fr.sii.notification.core.sender.NotificationSender;
import fr.sii.notification.email.message.EmailAddress;
import fr.sii.notification.email.sender.EmailSender;
import fr.sii.notification.email.sender.impl.JavaMailSender;

public class EmailBuilder implements NotificationSenderBuilder<ConditionalSender> {

	private ConditionalSender sender;
	
	private EmailSender emailSender;

	public EmailBuilder() {
		super();
		sender = emailSender = new EmailSender();
	}
	
	@Override
	public List<ConditionalSender> build() throws BuildException {
		return Arrays.asList(sender);
	}

	public EmailBuilder registerImplementation(Condition<Message> condition, NotificationSender implementation) {
		emailSender.addImplementation(condition, implementation);
		return this;
	}
	
	public EmailBuilder registerDefaultImplementations() {
		try {
			registerImplementation(new RequiredClassCondition<Message>("javax.mail.Transport"), new JavaMailSender());
		} catch(Throwable e) {
			// nothing to do
		}
		return this;
	}
	
	public EmailBuilder withConfigurationFiller(Properties props, String baseKey) {
		ConvertUtils.register(new EmailAddressConverter(), EmailAddress.class);
		sender = new FillerSender(new PropertiesFiller(props, baseKey), sender);
		return this;
	}
	
	public EmailBuilder withConfigurationFiller(Properties props) {
		withConfigurationFiller(props, "notification.email");
		return this;
	}
	
	public EmailBuilder withConfigurationFiller() {
		withConfigurationFiller(System.getProperties());
		return this;
	}
}
