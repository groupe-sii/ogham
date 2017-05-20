package fr.sii.ogham.template.thymeleaf.configure;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.DefaultMessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.core.message.content.EmailVariant;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.template.thymeleaf.buider.ThymeleafEmailBuilder;

@ConfigurerFor(targetedBuilder={"minimal", "standard"}, priority=940)
public class ThymeleafEmailConfigurer implements MessagingConfigurer {
	private final MessagingConfigurerAdapter delegate;

	public ThymeleafEmailConfigurer() {
		this(new DefaultMessagingConfigurer());
	}

	public ThymeleafEmailConfigurer(MessagingConfigurerAdapter delegate) {
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
					.pathPrefix("${ogham.email.thymeleaf.classpath.prefix}", "${ogham.email.template.classpath.prefix}", "${ogham.email.thymeleaf.prefix}", "${ogham.email.template.prefix}")
					.pathSuffix("${ogham.email.thymeleaf.classpath.suffix}", "${ogham.email.template.classpath.suffix}", "${ogham.email.thymeleaf.suffix}", "${ogham.email.template.suffix}")
					.and()
				.file()
					.pathPrefix("${ogham.email.thymeleaf.file.prefix}", "${ogham.email.template.file.prefix}", "${ogham.email.thymeleaf.prefix}", "${ogham.email.template.prefix}")
					.pathSuffix("${ogham.email.thymeleaf.file.suffix}", "${ogham.email.template.file.suffix}", "${ogham.email.thymeleaf.suffix}", "${ogham.email.template.suffix}")
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
