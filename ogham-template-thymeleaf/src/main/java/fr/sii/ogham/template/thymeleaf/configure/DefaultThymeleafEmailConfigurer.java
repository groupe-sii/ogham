package fr.sii.ogham.template.thymeleaf.configure;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.DefaultMessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.core.message.content.EmailVariant;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.template.thymeleaf.buider.ThymeleafEmailBuilder;

@ConfigurerFor(targetedBuilder={"minimal", "standard"}, priority=90000)
public class DefaultThymeleafEmailConfigurer implements MessagingConfigurer {
	private final MessagingConfigurerAdapter delegate;

	public DefaultThymeleafEmailConfigurer() {
		this(new DefaultMessagingConfigurer());
	}

	public DefaultThymeleafEmailConfigurer(MessagingConfigurerAdapter delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void configure(MessagingBuilder msgBuilder) {
		if(canUseThymeleaf()) {
			ThymeleafEmailBuilder builder = msgBuilder.email().template(ThymeleafEmailBuilder.class);
			// use same environment as parent builder
			builder.environment(msgBuilder.environment());
			// apply default resource resolution configuration
			if(delegate!=null) {
				delegate.configure(builder);
			}
			// @formatter:off
			builder
				.classpath()
					.pathPrefix("${ogham.email.thymeleaf.classpath.path-prefix}", "${ogham.email.template.classpath.path-prefix}", "${ogham.email.thymeleaf.path-prefix}", "${ogham.email.template.path-prefix}", "${ogham.template.path-prefix}")
					.pathSuffix("${ogham.email.thymeleaf.classpath.path-suffix}", "${ogham.email.template.classpath.path-suffix}", "${ogham.email.thymeleaf.path-suffix}", "${ogham.email.template.path-suffix}", "${ogham.template.path-suffix}")
					.and()
				.file()
					.pathPrefix("${ogham.email.thymeleaf.file.path-prefix}", "${ogham.email.template.file.path-prefix}", "${ogham.email.thymeleaf.path-prefix}", "${ogham.email.template.path-prefix}", "${ogham.template.path-prefix}")
					.pathSuffix("${ogham.email.thymeleaf.file.path-suffix}", "${ogham.email.template.file.path-suffix}", "${ogham.email.thymeleaf.path-suffix}", "${ogham.email.template.path-suffix}", "${ogham.template.path-suffix}")
					.and()
				.string()
					.and()
				.variant(EmailVariant.HTML, "html")
				.variant(EmailVariant.TEXT, "txt");			
			// @formatter:on
		}
	}

	private boolean canUseThymeleaf() {
		return ClasspathUtils.exists("org.thymeleaf.TemplateEngine");
	}

}
