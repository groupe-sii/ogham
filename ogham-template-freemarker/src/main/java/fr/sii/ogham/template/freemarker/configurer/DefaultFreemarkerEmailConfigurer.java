package fr.sii.ogham.template.freemarker.configurer;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.DefaultMessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.core.message.content.EmailVariant;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.template.freemarker.FreeMarkerTemplateDetector;
import fr.sii.ogham.template.freemarker.builder.FreemarkerEmailBuilder;
import freemarker.template.TemplateExceptionHandler;

@ConfigurerFor(targetedBuilder={"minimal", "standard"}, priority=80000)
public class DefaultFreemarkerEmailConfigurer implements MessagingConfigurer {
	private final MessagingConfigurerAdapter delegate;

	public DefaultFreemarkerEmailConfigurer() {
		this(new DefaultMessagingConfigurer());
	}

	public DefaultFreemarkerEmailConfigurer(MessagingConfigurerAdapter delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void configure(MessagingBuilder msgBuilder) {
		if(canUseFreemaker()) {
			FreemarkerEmailBuilder builder = msgBuilder.email().template(FreemarkerEmailBuilder.class);
			// use same environment as parent builder
			builder.environment(msgBuilder.environment());
			// apply default resource resolution configuration
			if(delegate!=null) {
				delegate.configure(builder);
			}
			// @formatter:off
			builder
				.classpath()
					.pathPrefix("${ogham.email.freemarker.classpath.path-prefix}", "${ogham.email.template.classpath.path-prefix}", "${ogham.email.freemarker.path-prefix}", "${ogham.email.template.path-prefix}", "${ogham.template.path-prefix}")
					.pathSuffix("${ogham.email.freemarker.classpath.path-suffix}", "${ogham.email.template.classpath.path-suffix}", "${ogham.email.freemarker.path-suffix}", "${ogham.email.template.path-suffix}", "${ogham.template.path-suffix}")
					.and()
				.file()
					.pathPrefix("${ogham.email.freemarker.file.path-prefix}", "${ogham.email.template.file.path-prefix}", "${ogham.email.freemarker.path-prefix}", "${ogham.email.template.path-prefix}", "${ogham.template.path-prefix}")
					.pathSuffix("${ogham.email.freemarker.file.path-suffix}", "${ogham.email.template.file.path-suffix}", "${ogham.email.freemarker.path-suffix}", "${ogham.email.template.path-suffix}", "${ogham.template.path-suffix}")
					.and()
				.string()
					.and()
				.variant(EmailVariant.HTML, "html.ftl")
				.variant(EmailVariant.TEXT, "txt.ftl")
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
