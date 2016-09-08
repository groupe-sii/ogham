package fr.sii.ogham.template.freemarker.builder;

import fr.sii.ogham.core.builder.TemplateParserBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.resource.ResourcePath;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.freemarker.FreemarkerFirstSupportingTemplateLoader;
import fr.sii.ogham.template.freemarker.FreemarkerParser;
import fr.sii.ogham.template.freemarker.adapter.ClassPathResolverAdapter;
import fr.sii.ogham.template.freemarker.adapter.FileResolverAdapter;
import fr.sii.ogham.template.freemarker.adapter.FirstSupportingResolverAdapter;
import fr.sii.ogham.template.freemarker.adapter.StringResolverAdapter;
import fr.sii.ogham.template.freemarker.adapter.TemplateLoaderAdapter;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

/**
 * Specialized builder for Freemarker template engine.
 * 
 * @author Cyril Dejonghe
 *
 */
public class FreemarkerTemplateParserBuilder implements TemplateParserBuilder {

	/**
	 * Freemarker configuration.
	 */
	private Configuration configuration;

	/**
	 * Find the first adapter that can handle the general {@link ResourceResolver} in order to convert it to Freemarker specific resolver
	 */
	private FirstSupportingResolverAdapter resolverAdapter;

	/**
	 * Find the first resource resolver that can handle a given path.
	 */
	private FirstSupportingResourceResolver resourceResolver;

	/**
	 * The parent path for searching template.
	 */
	private String parentPath;

	/**
	 * The extension for searching template.
	 */
	private String extension;

	public FreemarkerTemplateParserBuilder() {
		super();
		this.configuration = new Configuration(Configuration.VERSION_2_3_23);
		configuration.setDefaultEncoding("UTF-8");
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		configuration.setLogTemplateExceptions(false);

		this.resolverAdapter = new FirstSupportingResolverAdapter(new ClassPathResolverAdapter(), new FileResolverAdapter(), new StringResolverAdapter());
		parentPath = "";
		extension = "";
	}

	@Override
	public TemplateParser build() throws BuildException {
		configuration.setTemplateLoader(new FreemarkerFirstSupportingTemplateLoader(resourceResolver, resolverAdapter));
		return new FreemarkerParser(configuration);
	}

	/**
	 * By default the builder uses the a {@link FreemarkerFirstSupportingTemplateLoader} in a default configuration.
	 * 
	 * @param configuration
	 *            the new Freemarker configuration
	 * @return this instance for fluent use
	 */
	public FreemarkerTemplateParserBuilder withConfiguration(Configuration configuration) {
		this.configuration = configuration;
		return this;
	}

	/**
	 * To link our {@link TemplateLoaderAdapter}s with our {@link ResourceResolver}, we need a {@link FirstSupportingResourceResolver}.
	 * 
	 * @param firstSupportingResourceResolver
	 *            composite resolver to link template path with our {@link ResourcePath}
	 * 
	 * @return this instance for fluent use
	 */
	public FreemarkerTemplateParserBuilder withFirstResourceResolver(FirstSupportingResourceResolver firstSupportingResourceResolver) {
		this.resourceResolver = firstSupportingResourceResolver;
		return this;
	}

	@Override
	public FreemarkerTemplateParserBuilder withParentPath(String parentPath) {
		this.parentPath = parentPath;
		return this;
	}

	@Override
	public FreemarkerTemplateParserBuilder withExtension(String extension) {
		this.extension = extension;
		return this;
	}

	/**
	 * Registers a new template resolver adapter. An adapter is used for transforming the general resolver into a Freemarker specific equivalent.
	 * 
	 * @param adapter
	 *            the adapter to register
	 * @return this instance for fluent use
	 */
	public FreemarkerTemplateParserBuilder registerResolverAdapter(TemplateLoaderAdapter adapter) {
		resolverAdapter.addAdapter(adapter);
		return this;
	}

	/**
	 * Give access to the Freemarker configuration in order to be able to customize it.
	 * 
	 * @return Freemarker configuration
	 */
	public Configuration getConfiguration() {
		return configuration;
	}
}
