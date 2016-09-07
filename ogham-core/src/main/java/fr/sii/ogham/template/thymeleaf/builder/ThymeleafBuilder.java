package fr.sii.ogham.template.thymeleaf.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;

import fr.sii.ogham.core.builder.TemplateParserBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.resource.ResourcePath;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.thymeleaf.ThymeLeafFirstSupportingTemplateResolver;
import fr.sii.ogham.template.thymeleaf.ThymeleafParser;
import fr.sii.ogham.template.thymeleaf.adapter.ClassPathResolverAdapter;
import fr.sii.ogham.template.thymeleaf.adapter.FileResolverAdapter;
import fr.sii.ogham.template.thymeleaf.adapter.FirstSupportingResolverAdapter;
import fr.sii.ogham.template.thymeleaf.adapter.StringResolverAdapter;
import fr.sii.ogham.template.thymeleaf.adapter.ThymeleafResolverAdapter;
import fr.sii.ogham.template.thymeleaf.adapter.ThymeleafResolverOptions;

/**
 * Specialized builder for Thymeleaf template engine.
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeleafBuilder implements TemplateParserBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(ThymeleafBuilder.class);

	/**
	 * The Thymeleaf template engine
	 */
	private TemplateEngine engine;

	/**
	 * Find the first adapter that can handle the general
	 * {@link ResourceResolver} in order to convert it to Thymeleaf specific
	 * resolver
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

	public ThymeleafBuilder() {
		super();
		this.engine = new TemplateEngine();
		this.resourceResolver = null;
		this.resolverAdapter = new FirstSupportingResolverAdapter(new ClassPathResolverAdapter(), new FileResolverAdapter(), new StringResolverAdapter());
		parentPath = "";
		extension = "";
	}

	@Override
	public TemplateParser build() throws BuildException {
		LOG.debug("Using parent path {} and extension {} for thymeleaf template resolvers", parentPath, extension);
		resolverAdapter.setOptions(new ThymeleafResolverOptions(parentPath, extension));
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
	public ThymeleafBuilder withTemplateEngine(TemplateEngine engine) {
		this.engine = engine;
		return this;
	}

	/**
	 * To link our {@link ThymeleafResolverAdapter}s with our
	 * {@link ResourceResolver}, we need a
	 * {@link FirstSupportingResourceResolver}.
	 * 
	 * @param firstSupportingResourceResolver
	 *            composite resolver to link template path with our
	 *            {@link ResourcePath}
	 * 
	 * @return this instance for fluent use
	 */
	public ThymeleafBuilder withFirstResourceResolver(FirstSupportingResourceResolver firstSupportingResourceResolver) {
		this.resourceResolver = firstSupportingResourceResolver;
		return this;
	}

	@Override
	public ThymeleafBuilder withParentPath(String parentPath) {
		this.parentPath = parentPath;
		return this;
	}

	@Override
	public ThymeleafBuilder withExtension(String extension) {
		this.extension = extension;
		return this;
	}

	/**
	 * Registers a new template resolver adapter. An adapter is used for
	 * transforming the general resolver into a Thymeleaf specific equivalent.
	 * 
	 * @param adapter
	 *            the adapter to register
	 * @return this instance for fluent use
	 */
	public ThymeleafBuilder registerResolverAdapter(ThymeleafResolverAdapter adapter) {
		resolverAdapter.addAdapter(adapter);
		return this;
	}

	/**
	 * Give access to the Thymeleaf template engine in order to be able to
	 * customize it.
	 * 
	 * @return Thymeleaf template engine
	 */
	public TemplateEngine getEngine() {
		return engine;
	}
}
