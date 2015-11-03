package fr.sii.ogham.core.builder.configurer;

import org.apache.tika.Tika;
import org.thymeleaf.TemplateEngine;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.core.convert.DefaultConverter;
import fr.sii.ogham.core.id.generator.SequentialIdGenerator;
import fr.sii.ogham.core.message.content.EmailVariant;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;
import fr.sii.ogham.template.freemarker.FreeMarkerTemplateDetector;
import freemarker.template.TemplateExceptionHandler;

@ConfigurerFor(targetedBuilder={"minimal", "standard"}, priority=1000)
public class DefaultMessagingConfigurer extends MessagingConfigurerAdapter {

	@Override
	public void configure(MessagingBuilder builder) {
		super.configure(builder);
		builder.catchAll(true);
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
			.string("string:", "s:")
			.file("file:")
			.classpath("classpath:", "");
		// @formatter:on
	}

	@Override
	public void configure(EmailBuilder builder) {
		// configure resource resolution for attachments, css and images
		configure(builder.attachments());
		configure(builder.css().inline());
		configure(builder.images().inline());
		configure(builder.template().thymeleaf());
		configure(builder.template().freemarker());
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
					.base64()
						.and()
					.and()
				.and()
			.template()
				.thymeleaf()
					.pathPrefix("${ogham.email.thymeleaf.prefix}", "${ogham.email.template.prefix}")
					.pathSuffix("${ogham.email.thymeleaf.suffix}", "${ogham.email.template.suffix}")
					.variant(EmailVariant.HTML, "html")
					.variant(EmailVariant.TEXT, "txt")
					.engine(new TemplateEngine())
					.and()
				.freemarker()
					.pathPrefix("${ogham.email.freemarker.prefix}", "${ogham.email.template.prefix}")
					.pathSuffix("${ogham.email.freemarker.suffix}", "${ogham.email.template.suffix}")
					.variant(EmailVariant.HTML, "html.ftl")
					.variant(EmailVariant.TEXT, "txt.ftl")
					.configuration()
						.defaultEncoding("${ogham.freemarker.default-encoding}", "UTF-8")
						.templateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)
						.and()
					.detector(new FreeMarkerTemplateDetector(".ftl"))
					.and()
				.missingVariant(false);
		// @formatter:on
	}

	@Override
	public void configure(SmsBuilder builder) {
		// configure resource resolution for attachments, css and images
		configure(builder.template().thymeleaf());
		configure(builder.template().freemarker());
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
						.internationalNumber("${ogham.sms.to.format-enable-international}", "true")
						.and()
					.and()
				.and()
			.template()
				.thymeleaf()
					.pathPrefix("${ogham.sms.thymeleaf.prefix}", "${ogham.sms.template.prefix}")
					.pathSuffix("${ogham.sms.thymeleaf.suffix}", "${ogham.sms.template.suffix}")
					.engine(new TemplateEngine())
					.and()
				.freemarker()
					.pathPrefix("${ogham.sms.freemarker.prefix}", "${ogham.sms.template.prefix}")
					.pathSuffix("${ogham.sms.freemarker.suffix}", "${ogham.sms.template.suffix}")
					.configuration()
						.defaultEncoding("${ogham.freemarker.default-encoding}", "UTF-8")
						.templateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)
						.and()
					.detector(new FreeMarkerTemplateDetector(".ftl"));
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
