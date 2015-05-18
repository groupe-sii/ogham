package fr.sii.notification.core.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.core.sender.ConditionalSender;
import fr.sii.notification.core.service.ChainNotificationService;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.core.util.BuilderUtil;
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

	public NotificationBuilder useAllDefaults() {
		return useAllDefaults(BuilderUtil.getDefaultProperties());
	}

	public NotificationBuilder useAllDefaults(Properties properties) {
		useEmailDefaults(properties);
		useSmsDefaults(properties);
		return this;
	}

	public NotificationBuilder useEmailDefaults() {
		return useEmailDefaults(BuilderUtil.getDefaultProperties());
	}

	public NotificationBuilder useEmailDefaults(Properties properties) {
		withEmail();
		emailBuilder.useDefaults(properties);
		return this;
	}

	public NotificationBuilder useSmsDefaults() {
		return useSmsDefaults(BuilderUtil.getDefaultProperties());
	}

	public NotificationBuilder useSmsDefaults(Properties properties) {
		withSms();
		smsBuilder.useDefaults(properties);
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
