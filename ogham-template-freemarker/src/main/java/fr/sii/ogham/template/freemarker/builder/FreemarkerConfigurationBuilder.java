package fr.sii.ogham.template.freemarker.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.util.BuilderUtils;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public class FreemarkerConfigurationBuilder<P> extends AbstractParent<P> implements Builder<Configuration> {
	private Version version;
	private List<String> defaultEncodings;
	private TemplateExceptionHandler templateExceptionHandler;
	private EnvironmentBuilder<?> environmentBuilder;
	// TODO: handle all other options

	public FreemarkerConfigurationBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		defaultEncodings = new ArrayList<>();
		this.environmentBuilder = environmentBuilder;
	}
	
	public FreemarkerConfigurationBuilder<P> version(Version version) {
		this.version = version;
		return this;
	}

	public FreemarkerConfigurationBuilder<P> defaultEncoding(String... encodings) {
		this.defaultEncodings.addAll(Arrays.asList(encodings));
		return this;
	}
	
	public FreemarkerConfigurationBuilder<P> templateExceptionHandler(TemplateExceptionHandler exceptionHandler) {
		this.templateExceptionHandler = exceptionHandler;
		return this;
	}

	@Override
	public Configuration build() throws BuildException {
		Configuration configuration = version==null ? new Configuration() : new Configuration(version);
		PropertyResolver propertyResolver = environmentBuilder.build();
		String defaultEncoding = BuilderUtils.evaluate(defaultEncodings, propertyResolver, String.class);
		if(defaultEncoding!=null) {
			configuration.setDefaultEncoding(defaultEncoding);
		}
		configuration.setTemplateExceptionHandler(templateExceptionHandler);
		return configuration;
	}
}
