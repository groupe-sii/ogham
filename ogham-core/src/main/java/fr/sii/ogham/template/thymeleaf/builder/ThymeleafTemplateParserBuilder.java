package fr.sii.ogham.template.thymeleaf.builder;

import org.thymeleaf.TemplateEngine;

import fr.sii.ogham.core.builder.TemplateParserBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.message.content.EmailVariant;
import fr.sii.ogham.core.resource.ResourcePath;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.common.adapter.ExtensionMappingVariantResolver;
import fr.sii.ogham.template.thymeleaf.TemplateResolverOptions;
import fr.sii.ogham.template.thymeleaf.ThymeLeafFirstSupportingTemplateResolver;
import fr.sii.ogham.template.thymeleaf.ThymeleafParser;
import fr.sii.ogham.template.thymeleaf.adapter.ClassPathResolverAdapter;
import fr.sii.ogham.template.thymeleaf.adapter.FileResolverAdapter;
import fr.sii.ogham.template.thymeleaf.adapter.FirstSupportingResolverAdapter;
import fr.sii.ogham.template.thymeleaf.adapter.StringResolverAdapter;
import fr.sii.ogham.template.thymeleaf.adapter.TemplateResolverAdapter;

/**
 * Specialized builder for Thymeleaf template engine.
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeleafTemplateParserBuilder implements TemplateParserBuilder {

	/**
	 * The Thymeleaf template engine
	 */
	private TemplateEngine engine;

	/**
	 * Find the first adapter that can handle the general {@link ResourceResolver} in order to convert it to Thymeleaf specific resolver
	 */
	private FirstSupportingResolverAdapter resolverAdapter;

	/**
	 * Find the first resource resolver that can handle a given path.
	 */
	private FirstSupportingResourceResolver resourceResolver;

	public ThymeleafTemplateParserBuilder() {
		super();
		this.engine = new TemplateEngine();
		this.resourceResolver = null;
		this.resolverAdapter = new FirstSupportingResolverAdapter(new ClassPathResolverAdapter(), new FileResolverAdapter(), new StringResolverAdapter());
	}

	@Override
	public TemplateParser build() throws BuildException {
		resolverAdapter.setOptions(new TemplateResolverOptions());
		engine.addTemplateResolver(new ThymeLeafFirstSupportingTemplateResolver(resourceResolver, resolverAdapter));
		return new ThymeleafParser(engine);
	}

	/**
	 * By default the builder uses the default Thymeleaf template engine as-is.
	 * 
	 * @param engine
	 *            the new Thymeleaf template engine
	 * @return this instance for fluent use
	 */
	public ThymeleafTemplateParserBuilder withTemplateEngine(TemplateEngine engine) {
		this.engine = engine;
		return this;
	}

	/**
	 * To link our {@link TemplateResolverAdapter}s with our {@link ResourceResolver}, we need a {@link FirstSupportingResourceResolver}.
	 * 
	 * @param firstSupportingResourceResolver
	 *            composite resolver to link template path with our {@link ResourcePath}
	 * 
	 * @return this instance for fluent use
	 */
	public ThymeleafTemplateParserBuilder withFirstResourceResolver(FirstSupportingResourceResolver firstSupportingResourceResolver) {
		this.resourceResolver = firstSupportingResourceResolver;
		return this;
	}

	/**
	 * Registers a new template resolver adapter. An adapter is used for transforming the general resolver into a Thymeleaf specific equivalent.
	 * 
	 * @param adapter
	 *            the adapter to register
	 * @return this instance for fluent use
	 */
	public ThymeleafTemplateParserBuilder registerResolverAdapter(TemplateResolverAdapter adapter) {
		resolverAdapter.addAdapter(adapter);
		return this;
	}

	/**
	 * Give access to the Thymeleaf template engine in order to be able to customize it.
	 * 
	 * @return Thymeleaf template engine
	 */
	public TemplateEngine getEngine() {
		return engine;
	}
}
