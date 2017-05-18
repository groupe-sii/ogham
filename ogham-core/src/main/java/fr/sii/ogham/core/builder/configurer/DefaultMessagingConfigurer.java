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
					.classpath()
						.pathPrefix("${ogham.email.thymeleaf.classpath.prefix}", "${ogham.email.template.classpath.prefix}", "${ogham.email.thymeleaf.prefix}", "${ogham.email.template.prefix}")
						.pathSuffix("${ogham.email.thymeleaf.classpath.suffix}", "${ogham.email.template.classpath.suffix}", "${ogham.email.thymeleaf.suffix}", "${ogham.email.template.suffix}")
						.and()
					.file()
						.pathPrefix("${ogham.email.thymeleaf.file.prefix}", "${ogham.email.template.file.prefix}", "${ogham.email.thymeleaf.prefix}", "${ogham.email.template.prefix}")
						.pathSuffix("${ogham.email.thymeleaf.file.suffix}", "${ogham.email.template.file.suffix}", "${ogham.email.thymeleaf.suffix}", "${ogham.email.template.suffix}")
						.and()
					.variant(EmailVariant.HTML, "html")
					.variant(EmailVariant.TEXT, "txt")
					.engine(new TemplateEngine())
					.and()
				.freemarker()
					.classpath()
						.pathPrefix("${ogham.email.freemarker.classpath.prefix}", "${ogham.email.template.classpath.prefix}", "${ogham.email.freemarker.prefix}", "${ogham.email.template.prefix}")
						.pathSuffix("${ogham.email.freemarker.classpath.suffix}", "${ogham.email.template.classpath.suffix}", "${ogham.email.freemarker.suffix}", "${ogham.email.template.suffix}")
						.and()
					.file()
						.pathPrefix("${ogham.email.freemarker.file.prefix}", "${ogham.email.template.file.prefix}", "${ogham.email.freemarker.prefix}", "${ogham.email.template.prefix}")
						.pathSuffix("${ogham.email.freemarker.file.suffix}", "${ogham.email.template.file.suffix}", "${ogham.email.freemarker.suffix}", "${ogham.email.template.suffix}")
						.and()
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
					.classpath()
						.pathPrefix("${ogham.sms.thymeleaf.classpath.prefix}", "${ogham.sms.template.classpath.prefix}", "${ogham.sms.thymeleaf.prefix}", "${ogham.sms.template.prefix}")
						.pathSuffix("${ogham.sms.thymeleaf.classpath.suffix}", "${ogham.sms.template.classpath.suffix}", "${ogham.sms.thymeleaf.suffix}", "${ogham.sms.template.suffix}")
						.and()
					.file()
						.pathPrefix("${ogham.sms.thymeleaf.file.prefix}", "${ogham.sms.template.file.prefix}", "${ogham.sms.thymeleaf.prefix}", "${ogham.sms.template.prefix}")
						.pathSuffix("${ogham.sms.thymeleaf.file.suffix}", "${ogham.sms.template.file.suffix}", "${ogham.sms.thymeleaf.suffix}", "${ogham.sms.template.suffix}")
						.and()
					.engine(new TemplateEngine())
					.and()
				.freemarker()
					.classpath()
						.pathPrefix("${ogham.sms.freemarker.classpath.prefix}", "${ogham.sms.template.classpath.prefix}", "${ogham.sms.freemarker.prefix}", "${ogham.sms.template.prefix}")
						.pathSuffix("${ogham.sms.freemarker.classpath.suffix}", "${ogham.sms.template.classpath.suffix}", "${ogham.sms.freemarker.suffix}", "${ogham.sms.template.suffix}")
						.and()
					.file()
						.pathPrefix("${ogham.sms.freemarker.file.prefix}", "${ogham.sms.template.file.prefix}", "${ogham.sms.freemarker.prefix}", "${ogham.sms.template.prefix}")
						.pathSuffix("${ogham.sms.freemarker.file.suffix}", "${ogham.sms.template.file.suffix}", "${ogham.sms.freemarker.suffix}", "${ogham.sms.template.suffix}")
						.and()
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
