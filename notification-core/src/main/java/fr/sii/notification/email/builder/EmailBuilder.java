package fr.sii.notification.email.builder;

import java.util.Properties;

import org.apache.commons.beanutils.ConvertUtils;

import fr.sii.notification.core.builder.ContentTranslatorBuilder;
import fr.sii.notification.core.builder.NotificationSenderBuilder;
import fr.sii.notification.core.condition.Condition;
import fr.sii.notification.core.condition.RequiredClassCondition;
import fr.sii.notification.core.converter.EmailAddressConverter;
import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.core.filler.PropertiesFiller;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.sender.ConditionalSender;
import fr.sii.notification.core.sender.ContentTranslatorSender;
import fr.sii.notification.core.sender.FillerSender;
import fr.sii.notification.core.sender.NotificationSender;
import fr.sii.notification.core.translator.ContentTranslator;
import fr.sii.notification.core.util.BuilderUtil;
import fr.sii.notification.email.message.EmailAddress;
import fr.sii.notification.email.sender.EmailSender;

public class EmailBuilder implements NotificationSenderBuilder<ConditionalSender> {

	private static final String PROPERTIES_PREFIX = "notification.email";

	private ConditionalSender sender;
	
	private EmailSender emailSender;

	private JavaMailBuilder javaMailBuilder;
	
	public EmailBuilder() {
		super();
		sender = emailSender = new EmailSender();
		javaMailBuilder = new JavaMailBuilder().useDefaults();
	}
	
	@Override
	public ConditionalSender build() throws BuildException {
		return sender;
	}

	public EmailBuilder useDefaults() {
		return useDefaults(BuilderUtil.getDefaultProperties());
	}

	public EmailBuilder useDefaults(Properties properties) {
		setJavaMailBuilder(new JavaMailBuilder().useDefaults(properties));
		registerDefaultImplementations();
		withConfigurationFiller(properties);
		withTemplate();
		return this;
	}
	
	public EmailBuilder registerImplementation(Condition<Message> condition, NotificationSender implementation) {
		emailSender.addImplementation(condition, implementation);
		return this;
	}
	
	public EmailBuilder registerDefaultImplementations() {
		try {
			registerImplementation(new RequiredClassCondition<Message>("javax.mail.Transport"), javaMailBuilder.build());
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
		return withConfigurationFiller(props, PROPERTIES_PREFIX);
	}
	
	public EmailBuilder withConfigurationFiller() {
		return withConfigurationFiller(BuilderUtil.getDefaultProperties());
	}
	
	public EmailBuilder withTemplate() {
		return withTemplate(new ContentTranslatorBuilder().useDefaults());
	}
	
	public EmailBuilder withTemplate(ContentTranslator translator) {
		sender = new ContentTranslatorSender(translator, sender);
		return this;
	}
	
	public EmailBuilder withTemplate(ContentTranslatorBuilder builder) {
		return withTemplate(builder.build());
	}
	
	public EmailBuilder setJavaMailBuilder(JavaMailBuilder javaMailBuilder) {
		this.javaMailBuilder = javaMailBuilder;
		return this;
	}
}
