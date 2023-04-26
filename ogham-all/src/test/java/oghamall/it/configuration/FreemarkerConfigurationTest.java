package oghamall.it.configuration;

import static fr.sii.ogham.testing.assertion.OghamInternalAssertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.template.freemarker.builder.FreemarkerEmailBuilder;
import fr.sii.ogham.template.freemarker.builder.FreemarkerSmsBuilder;

public class FreemarkerConfigurationTest {
	@Test
	public void asDeveloperIDefineCustomPathPrefixUsingProperties() {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.template.path-prefix", "/custom-template-path/");
		MessagingService service = builder.build();
		assertThat(service)
			.freemarker()
				.all()
					.resourceResolver()
						.classpath()
							.pathPrefix(is("/custom-template-path/"))
							.pathSuffix(is(""))
							.lookup(contains("classpath:", ""))
							.and()
						.file()
							.pathPrefix(is("/custom-template-path/"))
							.pathSuffix(is(""))
							.lookup(contains("file:"));
	}
	
	@Test
	public void asDeveloperIDefineCustomPathPrefixInMyOwnCode() {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.template.path-prefix", "/custom-template-path-from-properties/");
		builder
			.email()
				.template(FreemarkerEmailBuilder.class)
					.classpath()
						.pathPrefix("/custom-template-path/")
						.and()
					.and()
				.and()
			.sms()
				.template(FreemarkerSmsBuilder.class)
				.classpath()
					.pathPrefix("/custom-template-path/");
		MessagingService service = builder.build();
		assertThat(service)
			.freemarker()
				.all()
					.resourceResolver()
					.classpath()
						.pathPrefix(is("/custom-template-path/"))
						.pathSuffix(is(""))
						.lookup(contains("classpath:", ""))
						.and()
					.file()
						.pathPrefix(is("/custom-template-path-from-properties/"))
						.pathSuffix(is(""))
						.lookup(contains("file:"));
	}
}
