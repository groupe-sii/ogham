package fr.sii.ogham.template.freemarker.builder;

import fr.sii.ogham.core.builder.TemplateParserBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.resource.ResourcePath;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.freemarker.FreeMarkerFirstSupportingTemplateLoader;
import fr.sii.ogham.template.freemarker.FreeMarkerParser;
import fr.sii.ogham.template.freemarker.adapter.ClassPathResolverAdapter;
import fr.sii.ogham.template.freemarker.adapter.FileResolverAdapter;
import fr.sii.ogham.template.freemarker.adapter.FirstSupportingResolverAdapter;
import fr.sii.ogham.template.freemarker.adapter.StringResolverAdapter;
import fr.sii.ogham.template.freemarker.adapter.TemplateLoaderAdapter;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

/**
 * Specialized builder for FreeMarker template engine.
 * 
 * @author Cyril Dejonghe
 *
 */
public class FreeMarkerTemplateParserBuilder implements TemplateParserBuilder {

	/**
	 * FreeMarker configuration.
	 */
	private Configuration configuration;

	/**
	 * Find the first adapter that can handle the general {@link ResourceResolver} in order to convert it to FreeMarker specific resolver
	 */
	private FirstSupportingResolverAdapter resolverAdapter;

	/**
	 * Find the first resource resolver that can handle a given path.
	 */
	private FirstSupportingResourceResolver resourceResolver;

	public FreeMarkerTemplateParserBuilder() {
		super();
		this.configuration = new Configuration(Configuration.VERSION_2_3_23);
		configuration.setDefaultEncoding("UTF-8");
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		configuration.setLogTemplateExceptions(false);

		this.resolverAdapter = new FirstSupportingResolverAdapter(new ClassPathResolverAdapter(), new FileResolverAdapter(), new StringResolverAdapter());
	}

	@Override
	public TemplateParser build() throws BuildException {
		configuration.setTemplateLoader(new FreeMarkerFirstSupportingTemplateLoader(resourceResolver, resolverAdapter));
		return new FreeMarkerParser(configuration);
	}

	/**
	 * By default the builder uses the a {@link FreeMarkerFirstSupportingTemplateLoader} in a default configuration.
	 * 
	 * @param configuration
	 *            the new FreeMarker configuration
	 * @return this instance for fluent use
	 */
	public FreeMarkerTemplateParserBuilder withConfiguration(Configuration configuration) {
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
	public FreeMarkerTemplateParserBuilder withFirstResourceResolver(FirstSupportingResourceResolver firstSupportingResourceResolver) {
		this.resourceResolver = firstSupportingResourceResolver;
		return this;
	}

	/**
	 * Registers a new template resolver adapter. An adapter is used for transforming the general resolver into a FreeMarker specific equivalent.
	 * 
	 * @param adapter
	 *            the adapter to register
	 * @return this instance for fluent use
	 */
	public FreeMarkerTemplateParserBuilder registerResolverAdapter(TemplateLoaderAdapter adapter) {
		resolverAdapter.addAdapter(adapter);
		return this;
	}

	/**
	 * Give access to the FreeMarker configuration in order to be able to customize it.
	 * 
	 * @return FreeMarker configuration
	 */
	public Configuration getConfiguration() {
		return configuration;
	}
}
