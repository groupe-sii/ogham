package fr.sii.ogham.spring.template;

import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.bind.RelaxedNames;
import org.thymeleaf.spring4.SpringTemplateEngine;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;
import fr.sii.ogham.spring.common.OghamTemplateProperties;
import fr.sii.ogham.spring.common.SpringMessagingConfigurer;
import fr.sii.ogham.spring.email.OghamEmailProperties;
import fr.sii.ogham.spring.sms.OghamSmsProperties;
import fr.sii.ogham.template.freemarker.builder.FreemarkerEmailBuilder;
import fr.sii.ogham.template.freemarker.builder.FreemarkerSmsBuilder;
import fr.sii.ogham.template.thymeleaf.ThymeleafConstants;
import fr.sii.ogham.template.thymeleaf.ThymeleafParser;
import fr.sii.ogham.template.thymeleaf.buider.AbstractThymeleafBuilder;
import fr.sii.ogham.template.thymeleaf.buider.ThymeleafEmailBuilder;
import fr.sii.ogham.template.thymeleaf.buider.ThymeleafSmsBuilder;

/**
 * Integrates with Spring templating system by using
 * {@link SpringTemplateEngine} object provided by Spring and by using Spring
 * properties defined with prefix {@code spring.thymeleaf} (see
 * {@link ThymeleafProperties}).
 * 
 * If both Spring property and Ogham property is defined, Spring property is
 * used.
 * 
 * For example, if the file application.properties contains the following
 * configuration:
 * 
 * <pre>
 * spring.thymeleaf.prefix=/email/
 * ogham.email.thymeleaf.path-prefix=/foo/
 * </pre>
 * 
 * The {@link ThymeleafParser} will use the templates in "/email/".
 * 
 * <p>
 * This configurer is also useful to support property naming variants (see
 * {@link RelaxedNames}).
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeLeafConfigurer extends MessagingConfigurerAdapter implements SpringMessagingConfigurer {
	private final SpringTemplateEngine springTemplateEngine;
	private final OghamCommonTemplateProperties templateProperties;
	private final OghamEmailProperties emailProperties;
	private final OghamSmsProperties smsProperties;
	private final ThymeleafProperties springProperties;

	public ThymeLeafConfigurer(SpringTemplateEngine springTemplateEngine, OghamCommonTemplateProperties templateProperties, OghamEmailProperties emailProperties, OghamSmsProperties smsProperties,
			ThymeleafProperties springProperties) {
		super();
		this.springTemplateEngine = springTemplateEngine;
		this.templateProperties = templateProperties;
		this.emailProperties = emailProperties;
		this.smsProperties = smsProperties;
		this.springProperties = springProperties;
	}

	@Override
	public void configure(MessagingBuilder builder) {
		// use same environment as parent builder
		builder.email().template(FreemarkerEmailBuilder.class).environment(builder.environment());
		builder.sms().template(FreemarkerSmsBuilder.class).environment(builder.environment());
		super.configure(builder);
	}

	@Override
	public void configure(EmailBuilder emailBuilder) {
		AbstractThymeleafBuilder<?, ?> builder = emailBuilder.template(ThymeleafEmailBuilder.class);
		emailBuilder.template(ThymeleafEmailBuilder.class).engine(springTemplateEngine);
		if (springProperties != null) {
			applySpringConfiguration(builder);
		}
		if (emailProperties != null) {
			applyOghamConfiguration(builder, emailProperties);
		}
	}

	@Override
	public void configure(SmsBuilder smsBuilder) {
		AbstractThymeleafBuilder<?, ?> builder = smsBuilder.template(ThymeleafSmsBuilder.class);
		smsBuilder.template(ThymeleafSmsBuilder.class).engine(springTemplateEngine);
		if (springProperties != null) {
			applySpringConfiguration(builder);
		}
		if (smsProperties != null) {
			applyOghamConfiguration(builder, smsProperties);
		}
	}

	@Override
	public int getOrder() {
		return ThymeleafConstants.DEFAULT_THYMELEAF_EMAIL_CONFIGURER_PRIORITY + 1000;
	}

	private void applyOghamConfiguration(AbstractThymeleafBuilder<?, ?> builder, OghamTemplateProperties props) {
		// @formatter:off
		builder
			.classpath()
				.pathPrefix(props.getThymeleaf().getClasspath().getPathPrefix(),
							props.getTemplate().getClasspath().getPathPrefix(),
							props.getThymeleaf().getPathPrefix(),
							props.getTemplate().getPathPrefix(),
							templateProperties.getPathPrefix())
				.pathSuffix(props.getThymeleaf().getClasspath().getPathSuffix(),
							props.getTemplate().getClasspath().getPathSuffix(),
							props.getThymeleaf().getPathSuffix(),
							props.getTemplate().getPathSuffix(),
							templateProperties.getPathSuffix())
				.and()
			.file()
				.pathPrefix(props.getThymeleaf().getFile().getPathPrefix(),
							props.getTemplate().getFile().getPathPrefix(),
							props.getThymeleaf().getPathPrefix(),
							props.getTemplate().getPathPrefix(),
							templateProperties.getPathPrefix())
				.pathSuffix(props.getThymeleaf().getFile().getPathSuffix(),
							props.getTemplate().getFile().getPathSuffix(),
							props.getThymeleaf().getPathSuffix(),
							props.getTemplate().getPathSuffix(),
							templateProperties.getPathSuffix());
		// @formatter:on
	}

	private void applySpringConfiguration(AbstractThymeleafBuilder<?, ?> builder) {
		// @formatter:off
		builder
			.classpath()
				.pathPrefix(springProperties.getPrefix())
				.pathSuffix(springProperties.getSuffix())
				.and()
			.file()
				.pathPrefix(springProperties.getPrefix())
				.pathSuffix(springProperties.getSuffix());
		// @formatter:on
	}

}
