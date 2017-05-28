package fr.sii.ogham.template.thymeleaf.configure;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.DefaultMessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.template.thymeleaf.buider.ThymeleafSmsBuilder;

@ConfigurerFor(targetedBuilder={"minimal", "standard"}, priority=70000)
public class DefaultThymeleafSmsConfigurer implements MessagingConfigurer {
	private final MessagingConfigurerAdapter delegate;

	public DefaultThymeleafSmsConfigurer() {
		this(new DefaultMessagingConfigurer());
	}

	public DefaultThymeleafSmsConfigurer(MessagingConfigurerAdapter delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void configure(MessagingBuilder msgBuilder) {
		if(canUseThymeleaf()) {
			ThymeleafSmsBuilder builder = msgBuilder.sms().template(ThymeleafSmsBuilder.class);
			// use same environment as parent builder
			builder.environment(msgBuilder.environment());
			// apply default resource resolution configuration
			if(delegate!=null) {
				delegate.configure(builder);
			}
			// @formatter:off
			builder
				.classpath()
					.pathPrefix("${ogham.sms.thymeleaf.classpath.path-prefix}", "${ogham.sms.template.classpath.path-prefix}", "${ogham.sms.thymeleaf.path-prefix}", "${ogham.sms.template.path-prefix}", "${ogham.template.path-prefix}")
					.pathSuffix("${ogham.sms.thymeleaf.classpath.path-suffix}", "${ogham.sms.template.classpath.path-suffix}", "${ogham.sms.thymeleaf.path-suffix}", "${ogham.sms.template.path-suffix}", "${ogham.template.path-suffix}")
					.and()
				.file()
					.pathPrefix("${ogham.sms.thymeleaf.file.path-prefix}", "${ogham.sms.template.file.path-prefix}", "${ogham.sms.thymeleaf.path-prefix}", "${ogham.sms.template.path-prefix}", "${ogham.template.path-prefix}")
					.pathSuffix("${ogham.sms.thymeleaf.file.path-suffix}", "${ogham.sms.template.file.path-suffix}", "${ogham.sms.thymeleaf.path-suffix}", "${ogham.sms.template.path-suffix}", "${ogham.template.path-suffix}");
			// @formatter:on
		}
	}

	private boolean canUseThymeleaf() {
		return ClasspathUtils.exists("org.thymeleaf.TemplateEngine");
	}

}
