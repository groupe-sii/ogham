package fr.sii.ogham.spring.template;

import static fr.sii.ogham.core.util.ConfigurationValueUtils.firstValue;
import static java.util.Optional.ofNullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;

import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;
import fr.sii.ogham.spring.common.OghamTemplateProperties;
import fr.sii.ogham.spring.common.SpringMessagingConfigurer;
import fr.sii.ogham.spring.email.OghamEmailProperties;
import fr.sii.ogham.spring.sms.OghamSmsProperties;
import fr.sii.ogham.spring.template.thymeleaf.TemplateEngineSupplier;
import fr.sii.ogham.template.thymeleaf.common.ThymeleafConstants;
import fr.sii.ogham.template.thymeleaf.common.ThymeleafContextConverter;
import fr.sii.ogham.template.thymeleaf.common.ThymeleafParser;
import fr.sii.ogham.template.thymeleaf.common.buider.AbstractThymeleafBuilder;

/**
 * Integrates with Spring templating system by using SpringTemplateEngine object
 * provided by Spring and by using Spring properties defined with prefix
 * {@code spring.thymeleaf} (see {@link ThymeleafProperties}).
 * 
 * If both Spring property and Ogham property is defined, Ogham property is
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
 * The {@link ThymeleafParser} will use the templates in "/foo/".
 * 
 * <p>
 * This configurer is also useful to support property naming variants (see
 * <a href=
 * "https://github.com/spring-projects/spring-boot/wiki/relaxed-binding-2.0">Relaxed
 * Binding</a>).
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeLeafConfigurer extends MessagingConfigurerAdapter implements SpringMessagingConfigurer {
	private static final Logger LOG = LoggerFactory.getLogger(ThymeLeafConfigurer.class);

	private final TemplateEngineSupplier springTemplateEngineSupplier;
	private final ThymeleafContextConverter contextConverter;
	private final OghamCommonTemplateProperties templateProperties;
	private final OghamEmailProperties emailProperties;
	private final OghamSmsProperties smsProperties;
	private final ThymeleafProperties springProperties;
	private final Class<? extends AbstractThymeleafBuilder<?, ?, ?>> emailBuilderClass;
	private final Class<? extends AbstractThymeleafBuilder<?, ?, ?>> smsBuilderClass;

	public ThymeLeafConfigurer(TemplateEngineSupplier springTemplateEngineSupplier, ThymeleafContextConverter contextConverter, OghamCommonTemplateProperties templateProperties,
			OghamEmailProperties emailProperties, OghamSmsProperties smsProperties, ThymeleafProperties springProperties, Class<? extends AbstractThymeleafBuilder<?, ?, ?>> emailBuilderClass,
			Class<? extends AbstractThymeleafBuilder<?, ?, ?>> smsBuilderClass) {
		super();
		this.springTemplateEngineSupplier = springTemplateEngineSupplier;
		this.contextConverter = contextConverter;
		this.templateProperties = templateProperties;
		this.emailProperties = emailProperties;
		this.smsProperties = smsProperties;
		this.springProperties = springProperties;
		this.emailBuilderClass = emailBuilderClass;
		this.smsBuilderClass = smsBuilderClass;
	}

	@Override
	public void configure(EmailBuilder emailBuilder) {
		AbstractThymeleafBuilder<?, ?, ?> builder = emailBuilder.template(emailBuilderClass);
		configureSpringEngine(builder);
		// specific Ogham properties explicitly take precedence over Spring
		// properties
		if (springProperties != null) {
			applySpringConfiguration(builder);
		}
		if (emailProperties != null) {
			applyOghamConfiguration(builder, emailProperties);
		}
	}

	@Override
	public void configure(SmsBuilder smsBuilder) {
		AbstractThymeleafBuilder<?, ?, ?> builder = smsBuilder.template(smsBuilderClass);
		configureSpringEngine(builder);
		// specific Ogham properties explicitly take precedence over Spring
		// properties
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

	private void configureSpringEngine(AbstractThymeleafBuilder<?, ?, ?> builder) {
		if (springTemplateEngineSupplier != null) {
			builder.engine(springTemplateEngineSupplier.get());
		}
		if (contextConverter != null) {
			builder.contextConverter(contextConverter);
		}
	}

	private void applyOghamConfiguration(AbstractThymeleafBuilder<?, ?, ?> builder, OghamTemplateProperties props) {
		LOG.debug("[{}] apply ogham configuration properties to {}", this, builder);
		// @formatter:off
		builder
			.classpath()
				.pathPrefix()
					.value(ofNullable(firstValue(props.getThymeleaf().getClasspath().getPathPrefix(),
												props.getTemplate().getClasspath().getPathPrefix(),
												props.getThymeleaf().getPathPrefix(),
												props.getTemplate().getPathPrefix(),
												templateProperties.getPathPrefix())))
					.and()
				.pathSuffix()
					.value(ofNullable(firstValue(props.getThymeleaf().getClasspath().getPathSuffix(),
												props.getTemplate().getClasspath().getPathSuffix(),
												props.getThymeleaf().getPathSuffix(),
												props.getTemplate().getPathSuffix(),
												templateProperties.getPathSuffix())))
					.and()
				.and()
			.file()
				.pathPrefix()
					.value(ofNullable(firstValue(props.getThymeleaf().getFile().getPathPrefix(),
												props.getTemplate().getFile().getPathPrefix(),
												props.getThymeleaf().getPathPrefix(),
												props.getTemplate().getPathPrefix(),
												templateProperties.getPathPrefix())))
					.and()
				.pathSuffix()
					.value(ofNullable(firstValue(props.getThymeleaf().getFile().getPathSuffix(),
												props.getTemplate().getFile().getPathSuffix(),
												props.getThymeleaf().getPathSuffix(),
												props.getTemplate().getPathSuffix(),
												templateProperties.getPathSuffix())))
					.and()
				.and()
			.cache().value(ofNullable(firstValue(props.getThymeleaf().getCache(),
												props.getTemplate().getCache(),
												templateProperties.getCache())));
		// @formatter:on
	}

	private void applySpringConfiguration(AbstractThymeleafBuilder<?, ?, ?> builder) {
		LOG.debug("[{}] apply spring configuration properties to {}", this, builder);
		// @formatter:off
		builder
			.classpath()
				.pathPrefix().value(ofNullable(springProperties.getPrefix())).and()
				.pathSuffix().value(ofNullable(springProperties.getSuffix())).and()
				.and()
			.file()
				.pathPrefix().value(ofNullable(springProperties.getPrefix())).and()
				.pathSuffix().value(ofNullable(springProperties.getSuffix()))
				.and()
			.and()
			.cache().value(ofNullable(springProperties.isCache()));
		// @formatter:on
	}

}
