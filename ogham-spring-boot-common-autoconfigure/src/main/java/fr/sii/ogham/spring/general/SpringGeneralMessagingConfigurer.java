package fr.sii.ogham.spring.general;

import static fr.sii.ogham.spring.util.PropertiesUtils.asArray;
import static java.util.Optional.ofNullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.CoreConstants;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;
import fr.sii.ogham.spring.common.OghamMimetypeProperties;
import fr.sii.ogham.spring.common.SpringMessagingConfigurer;
import fr.sii.ogham.spring.email.OghamEmailProperties;
import fr.sii.ogham.spring.sms.OghamSmsProperties;

/**
 * This configurer is useful to support property naming variants (see <a href=
 * "https://github.com/spring-projects/spring-boot/wiki/relaxed-binding-2.0">Relaxed
 * Binding</a>).
 * 
 * @author Aurélien Baudet
 *
 */
public class SpringGeneralMessagingConfigurer implements SpringMessagingConfigurer {
	private static final Logger LOG = LoggerFactory.getLogger(SpringGeneralMessagingConfigurer.class);
	
	private final MessagingProperties messagingProperties;
	private final OghamEmailProperties emailProperties;
	private final OghamSmsProperties smsProperties;
	private final OghamMimetypeProperties mimetypeProperties;

	public SpringGeneralMessagingConfigurer(MessagingProperties messagingProperties, OghamEmailProperties emailProperties, OghamSmsProperties smsProperties,
			OghamMimetypeProperties mimetypeProperties) {
		super();
		this.messagingProperties = messagingProperties;
		this.emailProperties = emailProperties;
		this.smsProperties = smsProperties;
		this.mimetypeProperties = mimetypeProperties;
	}

	@Override
	public void configure(MessagingBuilder builder) {
		LOG.debug("[{}] apply general configuration properties to {}", this, builder);
		// @formatter:off
		builder
			.wrapUncaught().value(ofNullable(messagingProperties.getWrapUncaughtExceptions().isEnable()));
		// @formatter:on
		configure(builder.email());
		configure(builder.sms());
		configure(builder.mimetype());
	}

	private void configure(EmailBuilder builder) {
		// @formatter:off
		builder
			.autofill()
				.subject()
					.defaultValue().value(ofNullable(emailProperties.getSubject().getDefaultValue())).and()
					.htmlTitle().value(ofNullable(emailProperties.getSubject().getExtractHtmlTitle().isEnable())).and()
					.text().value(ofNullable(emailProperties.getSubject().getExtractFromText().getFirstLinePrefix())).and()
					.and()
				.from()
					.defaultValue().value(ofNullable(emailProperties.getFrom().getDefaultValue())).and()
					.and()
				.to()
					.defaultValue().value(ofNullable(asArray(emailProperties.getTo().getDefaultValue(), String.class))).and()
					.and()
				.cc()
					.defaultValue().value(ofNullable(asArray(emailProperties.getCc().getDefaultValue(), String.class))).and()
					.and()
				.bcc()
					.defaultValue().value(ofNullable(asArray(emailProperties.getBcc().getDefaultValue(), String.class))).and()
					.and()
				.and()
			.autoRetry()
				.fixedDelay()
					.maxRetries().value(ofNullable(emailProperties.getSendRetry().getMaxAttempts())).and()
					.delay().value(ofNullable(emailProperties.getSendRetry().getDelayBetweenAttempts())).and()
					.and()
				.exponentialDelay()
					.maxRetries().value(ofNullable(emailProperties.getSendRetry().getMaxAttempts())).and()
					.initialDelay().value(ofNullable(emailProperties.getSendRetry().getExponentialInitialDelay())).and()
					.and()
				.perExecutionDelay()
					.maxRetries().value(ofNullable(emailProperties.getSendRetry().getMaxAttempts())).and()
					.delays().value(ofNullable(asArray(emailProperties.getSendRetry().getPerExecutionDelays(), Long.class))).and()
					.and()
				.fixedInterval()
					.maxRetries().value(ofNullable(emailProperties.getSendRetry().getMaxAttempts())).and()
					.interval().value(ofNullable(emailProperties.getSendRetry().getExecutionInterval()));
		// @formatter:on
	}

	private void configure(SmsBuilder builder) {
		// @formatter:off
		builder
			.autofill()
				.from()
					.defaultValue().value(ofNullable(smsProperties.getFrom().getDefaultValue())).and()
					.and()
				.to()
					.defaultValue().value(ofNullable(asArray(smsProperties.getTo().getDefaultValue(), String.class))).and()
					.and()
				.and()
			.numbers()
				.from()
					.format()
						.alphanumericCode().value(ofNullable(smsProperties.getFrom().getAlphanumericCodeFormat().getEnable())).and()
						.shortCode().value(ofNullable(smsProperties.getFrom().getShortCodeFormat().getEnable())).and()
						.internationalNumber().value(ofNullable(smsProperties.getFrom().getInternationalFormat().getEnable())).and()
						.and()
					.and()
				.to()
					.format()
						.internationalNumber().value(ofNullable(smsProperties.getTo().getInternationalFormat().getEnable())).and()
						.and()
					.and()
				.and()
			.autoRetry()
				.fixedDelay()
					.maxRetries().value(ofNullable(smsProperties.getSendRetry().getMaxAttempts())).and()
					.delay().value(ofNullable(smsProperties.getSendRetry().getDelayBetweenAttempts())).and()
					.and()
				.exponentialDelay()
					.maxRetries().value(ofNullable(smsProperties.getSendRetry().getMaxAttempts())).and()
					.initialDelay().value(ofNullable(smsProperties.getSendRetry().getExponentialInitialDelay())).and()
					.and()
				.perExecutionDelay()
					.maxRetries().value(ofNullable(smsProperties.getSendRetry().getMaxAttempts())).and()
					.delays().value(ofNullable(asArray(smsProperties.getSendRetry().getPerExecutionDelays(), Long.class))).and()
					.and()
				.fixedInterval()
					.maxRetries().value(ofNullable(smsProperties.getSendRetry().getMaxAttempts())).and()
					.interval().value(ofNullable(smsProperties.getSendRetry().getExecutionInterval()));
		// @formatter:on
	}

	private void configure(MimetypeDetectionBuilder<?> builder) {
		// @formatter:off
		builder
			.tika()
				.failIfOctetStream().value(ofNullable(mimetypeProperties.getTika().isFailIfOctetStream())).and()
				.and()
			.defaultMimetype().value(ofNullable(mimetypeProperties.getDefaultMimetype()));
		// @formatter:on
	}

	@Override
	public int getOrder() {
		return CoreConstants.DEFAULT_MESSAGING_CONFIGURER_PRIORITY + 1000;
	}

}
