package fr.sii.ogham.template.freemarker.configurer;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.DefaultMessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.template.freemarker.FreeMarkerTemplateDetector;
import fr.sii.ogham.template.freemarker.builder.FreemarkerSmsBuilder;
import freemarker.template.TemplateExceptionHandler;

@ConfigurerFor(targetedBuilder={"minimal", "standard"}, priority=60000)
public class DefaultFreemarkerSmsConfigurer implements MessagingConfigurer {
	private final MessagingConfigurerAdapter delegate;

	public DefaultFreemarkerSmsConfigurer() {
		this(new DefaultMessagingConfigurer());
	}

	public DefaultFreemarkerSmsConfigurer(MessagingConfigurerAdapter delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void configure(MessagingBuilder msgBuilder) {
		if(canUseFreemaker()) {
			FreemarkerSmsBuilder builder = msgBuilder.sms().template(FreemarkerSmsBuilder.class);
			// use same environment as parent builder
			builder.environment(msgBuilder.environment());
			// apply default resource resolution configuration
			if(delegate!=null) {
				delegate.configure(builder);
			}
			// @formatter:off
			builder
				.classpath()
					.pathPrefix("${ogham.sms.freemarker.classpath.path-prefix}", "${ogham.sms.template.classpath.path-prefix}", "${ogham.sms.freemarker.prefix}", "${ogham.sms.template.path-prefix}", "${ogham.template.path-prefix}")
					.pathSuffix("${ogham.sms.freemarker.classpath.path-suffix}", "${ogham.sms.template.classpath.path-suffix}", "${ogham.sms.freemarker.suffix}", "${ogham.sms.template.path-suffix}", "${ogham.template.path-suffix}")
					.and()
				.file()
					.pathPrefix("${ogham.sms.freemarker.file.path-prefix}", "${ogham.sms.template.file.path-prefix}", "${ogham.sms.freemarker.prefix}", "${ogham.sms.template.path-prefix}", "${ogham.template.path-prefix}")
					.pathSuffix("${ogham.sms.freemarker.file.path-suffix}", "${ogham.sms.template.file.path-suffix}", "${ogham.sms.freemarker.suffix}", "${ogham.sms.template.path-suffix}", "${ogham.template.path-suffix}")
					.and()
				.configuration()
					.defaultEncoding("${ogham.freemarker.default-encoding}", "UTF-8")
					.templateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)
					.and()
				.detector(new FreeMarkerTemplateDetector(".ftl"));
			// @formatter:on
		}
	}

	private boolean canUseFreemaker() {
		return ClasspathUtils.exists("freemarker.template.Configuration") && ClasspathUtils.exists("freemarker.template.Template");
	}

}
