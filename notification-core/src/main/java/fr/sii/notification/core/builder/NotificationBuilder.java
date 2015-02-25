package fr.sii.notification.core.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import fr.sii.notification.core.exception.BuildException;
import fr.sii.notification.core.sender.ConditionalSender;
import fr.sii.notification.core.service.ChainNotificationService;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.builder.EmailBuilder;
import fr.sii.notification.sms.builder.SmsBuilder;

public class NotificationBuilder implements NotificationServiceBuilder {

	private List<NotificationSenderBuilder<ConditionalSender>> builders;
	private SmsBuilder smsBuilder;
	private EmailBuilder emailBuilder;
	
	public NotificationBuilder() {
		super();
		builders = new ArrayList<NotificationSenderBuilder<ConditionalSender>>();
	}
	
	@Override
	public NotificationService build() throws BuildException {
		List<ConditionalSender> senders = new ArrayList<ConditionalSender>();
		for(NotificationSenderBuilder<ConditionalSender> builder : builders) {
			senders.addAll(builder.build());
		}
		return new ChainNotificationService(senders);
	}

	public NotificationBuilder withAllDefaults() {
		withEmailDefaults();
		withSmsDefaults();
		return this;
	}

	public NotificationBuilder withAllDefaults(Properties properties) {
		withEmailDefaults(properties);
		withSmsDefaults(properties);
		return this;
	}

	public NotificationBuilder withEmailDefaults() {
		withEmail();
		emailBuilder.registerDefaultImplementations();
		emailBuilder.withConfigurationFiller();
		return this;
	}

	public NotificationBuilder withEmailDefaults(Properties properties) {
		withEmail();
		emailBuilder.registerDefaultImplementations();
		emailBuilder.withConfigurationFiller(properties);
		return this;
	}

	public NotificationBuilder withSmsDefaults() {
		withSms();
		smsBuilder.registerDefaultImplementations();
		smsBuilder.withConfigurationFiller();
		return this;
	}

	public NotificationBuilder withSmsDefaults(Properties properties) {
		withSms();
		smsBuilder.registerDefaultImplementations(properties);
		smsBuilder.withConfigurationFiller(properties);
		return this;
	}
	
	public NotificationBuilder withEmail() {
		emailBuilder = new EmailBuilder();
		builders.add(emailBuilder);
		return this;
	}
	
	public NotificationBuilder withSms() {
		smsBuilder = new SmsBuilder();
		builders.add(smsBuilder);
		return this;
	}
	
	public SmsBuilder getSmsBuilder() {
		return smsBuilder;
	}

	public EmailBuilder getEmailBuilder() {
		return emailBuilder;
	}
}
