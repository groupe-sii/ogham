package fr.sii.ogham.core.builder.configurer;

import org.apache.tika.Tika;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.core.convert.DefaultConverter;
import fr.sii.ogham.core.id.generator.SequentialIdGenerator;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;

@ConfigurerFor(targetedBuilder={"minimal", "standard"}, priority=1000)
public class DefaultMessagingConfigurer extends MessagingConfigurerAdapter {

	@Override
	public void configure(MessagingBuilder builder) {
		super.configure(builder);
		builder.wrapUncaught(true);
	}
	
	@Override
	public void configure(EnvironmentBuilder<?> builder) {
		// @formatter:off
		builder
			.systemProperties()
			.converter()
				.override(new DefaultConverter());
		// @formatter:on
	}

	@Override
	public void configure(ResourceResolutionBuilder<?> builder) {
		// @formatter:off
		builder
			.string()
				.lookup("string:", "s:")
				.and()
			.file()
				.lookup("file:")
				.and()
			.classpath()
				.lookup("classpath:", "");
		// @formatter:on
	}

	@Override
	public void configure(EmailBuilder builder) {
		// configure resource resolution for attachments, css and images
		configure(builder.attachments());
		configure(builder.css().inline());
		configure(builder.images().inline());
		// configure mimetype detection for images
		configure(builder.images().inline().mimetype());

		// @formatter:off
		builder
			.autofill()
				.subject()
					.defaultValueProperty("${ogham.email.subject}")
					.htmlTitle(true)
					.text("${ogham.email.subject-first-line-prefix}", "Subject:")
					.and()
				.from()
					.defaultValueProperty("${ogham.email.from}", "${mail.smtp.from}")
					.and()
				.to()
					.defaultValueProperty("${ogham.email.to}")
					.and()
				.cc()
					.defaultValueProperty("${ogham.email.cc}")
					.and()
				.bcc()
					.defaultValueProperty("${ogham.email.bcc}")
					.and()
				.and()
			.css()
				.inline()
					.jsoup()
					.and()
				.and()
			.images()
				.inline()
					.attach()
						.cid()
							.generator(new SequentialIdGenerator())
							.and()
						.and()
					.base64();
		// @formatter:on
	}

	@Override
	public void configure(SmsBuilder builder) {
		// @formatter:off
		builder
			.autofill()
				.from()
					.defaultValueProperty("${ogham.sms.from}")
					.and()
				.to()
					.defaultValueProperty("${ogham.sms.to}")
					.and()
				.and()
			.numbers()
				.from()
					.format()
						.alphanumericCode("${ogham.sms.from.format-enable-alphanumeric}", "true")
						.shortCode("${ogham.sms.from.format-enable-shortcode}", "true")
						.internationalNumber("${ogham.sms.from.format-enable-international}", "true")
						.and()
					.and()
				.to()
					.format()
						.internationalNumber("${ogham.sms.to.format-enable-international}", "true");
		// @formatter:on
	}

	public void configure(MimetypeDetectionBuilder<?> builder) {
		// @formatter:off
		builder
			.tika()
				.instance(new Tika())
				.failIfOctetStream(true)
				.and()
			.defaultMimetype("${ogham.mimetype.default}", "application/octet-stream");
		// @formatter:on
	}

}
