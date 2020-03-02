package fr.sii.ogham.spring.template;

import static fr.sii.ogham.core.util.ConfigurationValueUtils.firstValue;
import static java.util.Optional.ofNullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.context.ApplicationContext;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;
import fr.sii.ogham.spring.common.OghamTemplateProperties;
import fr.sii.ogham.spring.common.SpringMessagingConfigurer;
import fr.sii.ogham.spring.email.OghamEmailProperties;
import fr.sii.ogham.spring.sms.OghamSmsProperties;
import fr.sii.ogham.spring.template.freemarker.SpringBeansTemplateHashModelEx;
import fr.sii.ogham.template.freemarker.FreeMarkerParser;
import fr.sii.ogham.template.freemarker.FreemarkerConstants;
import fr.sii.ogham.template.freemarker.builder.AbstractFreemarkerBuilder;
import fr.sii.ogham.template.freemarker.builder.FreemarkerEmailBuilder;
import fr.sii.ogham.template.freemarker.builder.FreemarkerSmsBuilder;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;

/**
 * Integrates with Spring templating system by using Freemarker
 * {@link Configuration} object provided by Spring and by using Spring
 * properties defined with prefix {@code spring.freemarker} (see
 * {@link FreeMarkerProperties}).
 * 
 * If both Spring property and Ogham property is defined, Ogham property is
 * used.
 * 
 * For example, if the file application.properties contains the following
 * configuration:
 * 
 * <pre>
 * spring.freemarker.prefix=/email/
 * ogham.email.freemarker.path-prefix=/foo/
 * </pre>
 * 
 * The {@link FreeMarkerParser} will use the templates in "/foo/".
 * 
 * <p>
 * This configurer is also useful to support property naming variants (see
 * <a href=
 * "https://github.com/spring-projects/spring-boot/wiki/relaxed-binding-2.0">Relaxed
 * Binding</a>).
 * 
 * <p>
 * If ogham.freemarker.enable-spring-beans is true (default value), then Spring
 * Beans are available from the template using syntax
 * {@code @beanName.method(args)}.
 * 
 * <p>
 * If {@code ogham.freemarker.static-method-access.enable} is true (default
 * value), then static methods can be called from templates using
 * 
 * <pre>
 * {@code statics['full.package.name.ClassName'].method(args)}
 * </pre>
 * 
 * If {@code ogham.freemarker.static-method-access.variable-name} value is
 * changed (default value is 'statics'), then static methods can be called from
 * templates using another variable name. For example, configuring
 * {@code ogham.freemarker.static-method-access.variable-name=global} gives
 * access to static methods using name global:
 * 
 * <pre>
 * {@code global['full.package.name.ClassName'].method(args)}
 * </pre>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class FreemarkerConfigurer extends MessagingConfigurerAdapter implements SpringMessagingConfigurer {
	private static final int SPRING_CONFIGURER_PRIORITY_OFFSET = 1000;

	private static final Logger LOG = LoggerFactory.getLogger(FreemarkerConfigurer.class);

	private final Configuration emailConfiguration;
	private final Configuration smsConfiguration;
	private final OghamCommonTemplateProperties templateProperties;
	private final OghamEmailProperties emailProperties;
	private final OghamSmsProperties smsProperties;
	private final FreeMarkerProperties springProperties;
	private final OghamFreemarkerProperties oghamFreemarkerProperties;
	private final ApplicationContext applicationContext;

	public FreemarkerConfigurer(Configuration emailConfiguration, Configuration smsConfiguration, OghamCommonTemplateProperties templateProperties, OghamEmailProperties emailProperties,
			OghamSmsProperties smsProperties, FreeMarkerProperties springProperties, OghamFreemarkerProperties oghamFreemarkerProperties, ApplicationContext applicationContext) {
		super();
		this.emailConfiguration = emailConfiguration;
		this.smsConfiguration = smsConfiguration;
		this.templateProperties = templateProperties;
		this.emailProperties = emailProperties;
		this.smsProperties = smsProperties;
		this.springProperties = springProperties;
		this.oghamFreemarkerProperties = oghamFreemarkerProperties;
		this.applicationContext = applicationContext;
	}

	@Override
	public void configure(MessagingBuilder builder) {
		LOG.debug("[{}] apply configuration", this);
		// use same environment as parent builder
		builder.email().template(FreemarkerEmailBuilder.class).environment(builder.environment());
		builder.sms().template(FreemarkerSmsBuilder.class).environment(builder.environment());
		super.configure(builder);
	}

	@Override
	public void configure(EmailBuilder emailBuilder) {
		AbstractFreemarkerBuilder<?, ?> builder = emailBuilder.template(FreemarkerEmailBuilder.class);
		builder.mergeConfiguration(emailConfiguration);
		// specific Ogham properties explicitly take precedence over Spring
		// properties
		if (springProperties != null) {
			applySpringConfiguration(builder);
		}
		if (emailProperties != null) {
			applyOghamConfiguration(builder, emailProperties);
		}
		if (oghamFreemarkerProperties.getSpringBeans().isEnable()) {
			registerSpringBeans(builder, emailConfiguration);
		}
		if (oghamFreemarkerProperties.getStaticMethodAccess().isEnable()) {
			registerStatics(builder, emailConfiguration);
		}
	}

	@Override
	public void configure(SmsBuilder smsBuilder) {
		AbstractFreemarkerBuilder<?, ?> builder = smsBuilder.template(FreemarkerSmsBuilder.class);
		builder.mergeConfiguration(smsConfiguration);
		// specific Ogham properties explicitly take precedence over Spring
		// properties
		if (springProperties != null) {
			applySpringConfiguration(builder);
		}
		if (smsProperties != null) {
			applyOghamConfiguration(builder, smsProperties);
		}
		if (oghamFreemarkerProperties.getSpringBeans().isEnable()) {
			registerSpringBeans(builder, smsConfiguration);
		}
		if (oghamFreemarkerProperties.getStaticMethodAccess().isEnable()) {
			registerStatics(builder, smsConfiguration);
		}
	}

	@Override
	public int getOrder() {
		return FreemarkerConstants.DEFAULT_FREEMARKER_EMAIL_CONFIGURER_PRIORITY + SPRING_CONFIGURER_PRIORITY_OFFSET;
	}

	private void applyOghamConfiguration(AbstractFreemarkerBuilder<?, ?> builder, OghamTemplateProperties props) {
		LOG.debug("[{}] apply ogham configuration properties to {}", this, builder);
		// @formatter:off
		builder
			.classpath()
				.pathPrefix()
					.value(ofNullable(firstValue(props.getFreemarker().getClasspath().getPathPrefix(),
												props.getTemplate().getClasspath().getPathPrefix(),
												props.getFreemarker().getPathPrefix(),
												props.getTemplate().getPathPrefix(),
												templateProperties.getPathPrefix())))
					.and()
				.pathSuffix()
					.value(ofNullable(firstValue(props.getFreemarker().getClasspath().getPathSuffix(),
												props.getTemplate().getClasspath().getPathSuffix(),
												props.getFreemarker().getPathSuffix(),
												props.getTemplate().getPathSuffix(),
												templateProperties.getPathSuffix())))
					.and()
				.and()
			.file()
				.pathPrefix()
					.value(ofNullable(firstValue(props.getFreemarker().getFile().getPathPrefix(),
												props.getTemplate().getFile().getPathPrefix(),
												props.getFreemarker().getPathPrefix(),
												props.getTemplate().getPathPrefix(),
												templateProperties.getPathPrefix())))
					.and()
				.pathSuffix()
					.value(ofNullable(firstValue(props.getFreemarker().getFile().getPathSuffix(),
											props.getTemplate().getFile().getPathSuffix(),
											props.getFreemarker().getPathSuffix(),
											props.getTemplate().getPathSuffix(),
											templateProperties.getPathSuffix())));
		builder
			.configuration()
				.defaultEncoding().value(ofNullable(oghamFreemarkerProperties.getDefaultEncoding()));
		// @formatter:on
	}

	private void applySpringConfiguration(AbstractFreemarkerBuilder<?, ?> builder) {
		LOG.debug("[{}] apply spring configuration properties to {}", this, builder);
		// @formatter:off
		builder
			.classpath()
				.pathPrefix().value(ofNullable(springProperties.getPrefix())).and()
				.pathSuffix().value(ofNullable(springProperties.getSuffix())).and()
				.and()
			.file()
				.pathPrefix().value(ofNullable(springProperties.getPrefix())).and()
				.pathSuffix().value(ofNullable(springProperties.getSuffix()));
		builder
			.configuration()
				.defaultEncoding().value(ofNullable(springProperties.getCharsetName()));
		// @formatter:on
	}

	private void registerSpringBeans(AbstractFreemarkerBuilder<?, ?> builder, Configuration configuration) {
		builder.configuration().addSharedVariables(new SpringBeansTemplateHashModelEx(applicationContext, getBeansWrapper(configuration)));
	}

	private static BeansWrapper getBeansWrapper(Configuration configuration) {
		ObjectWrapper objectWrapper = configuration.getObjectWrapper();
		if (objectWrapper instanceof BeansWrapper) {
			return (BeansWrapper) objectWrapper;
		}
		return new BeansWrapperBuilder(configuration.getIncompatibleImprovements()).build();
	}

	private void registerStatics(AbstractFreemarkerBuilder<?, ?> builder, Configuration configuration) {
		builder.configuration().addSharedVariable(oghamFreemarkerProperties.getStaticMethodAccess().getVariableName(), getBeansWrapper(configuration).getStaticModels());
	}
}
