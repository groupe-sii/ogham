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
					.pathPrefix("${ogham.sms.thymeleaf.classpath.prefix}", "${ogham.sms.template.classpath.prefix}", "${ogham.sms.thymeleaf.prefix}", "${ogham.sms.template.prefix}")
					.pathSuffix("${ogham.sms.thymeleaf.classpath.suffix}", "${ogham.sms.template.classpath.suffix}", "${ogham.sms.thymeleaf.suffix}", "${ogham.sms.template.suffix}")
					.and()
				.file()
					.pathPrefix("${ogham.sms.thymeleaf.file.prefix}", "${ogham.sms.template.file.prefix}", "${ogham.sms.thymeleaf.prefix}", "${ogham.sms.template.prefix}")
					.pathSuffix("${ogham.sms.thymeleaf.file.suffix}", "${ogham.sms.template.file.suffix}", "${ogham.sms.thymeleaf.suffix}", "${ogham.sms.template.suffix}");
			// @formatter:on
		}
	}

	private boolean canUseThymeleaf() {
		return ClasspathUtils.exists("org.thymeleaf.TemplateEngine");
	}

}
