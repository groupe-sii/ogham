package fr.sii.notification.template.thymeleaf.builder;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.notification.core.builder.TemplateParserBuilder;
import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.core.resource.resolver.LookupMappingResolver;
import fr.sii.notification.core.resource.resolver.ResourceResolver;
import fr.sii.notification.core.template.parser.TemplateParser;
import fr.sii.notification.template.exception.NoResolverAdapterException;
import fr.sii.notification.template.thymeleaf.ThymeleafLookupMappingResolver;
import fr.sii.notification.template.thymeleaf.ThymeleafParser;
import fr.sii.notification.template.thymeleaf.adapter.ClassPathResolverAdapter;
import fr.sii.notification.template.thymeleaf.adapter.FileResolverAdapter;
import fr.sii.notification.template.thymeleaf.adapter.FirstSupportingResolverAdapter;
import fr.sii.notification.template.thymeleaf.adapter.ThymeleafResolverAdapter;

/**
 * Specialized builder for Thymeleaf template engine.
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeleafBuilder implements TemplateParserBuilder {
	/**
	 * The Thymeleaf template engine
	 */
	private TemplateEngine engine;

	/**
	 * The resolver based on lookup prefix. It wraps the general
	 * {@link LookupMappingResolver} to be usable by Thymeleaf
	 */
	private ThymeleafLookupMappingResolver lookupResolver;

	/**
	 * Find the first adapter that can handle the general
	 * {@link ResourceResolver} in order to convert it to Thymeleaf specific
	 * resolver
	 */
	private FirstSupportingResolverAdapter resolverAdapter;

	/**
	 * The prefix for searching template
	 */
	private String prefix;

	/**
	 * The suffix for searching template
	 */
	private String suffix;

	public ThymeleafBuilder() {
		super();
		engine = new TemplateEngine();
		lookupResolver = new ThymeleafLookupMappingResolver();
		resolverAdapter = new FirstSupportingResolverAdapter(new ClassPathResolverAdapter(), new FileResolverAdapter());
		prefix = "";
		suffix = "";
	}

	@Override
	public TemplateParser build() throws BuildException {
		for (ITemplateResolver resolver : lookupResolver.getResolvers()) {
			if (resolver instanceof org.thymeleaf.templateresolver.TemplateResolver) {
				org.thymeleaf.templateresolver.TemplateResolver templateResolver = (org.thymeleaf.templateresolver.TemplateResolver) resolver;
				templateResolver.setPrefix(prefix);
				templateResolver.setSuffix(suffix);
			}
		}
		return new ThymeleafParser(engine, lookupResolver);
	}

	/**
	 * <p>
	 * Registers a new custom Thymeleaf resolver for the lookup. If a resolver
	 * was already registered for the same lookup, the provided resolver will
	 * replace it.
	 * </p>
	 * <p>
	 * It doesn't use the general abstraction mechanism. So be sure to use it
	 * correctly.
	 * </p>
	 * 
	 * @param lookup
	 *            the lookup prefix (without the ':' character)
	 * @param resolver
	 *            the Thymealeaf specific resolver
	 * @return this instance for fluent use
	 */
	public ThymeleafBuilder withLookupResolver(String lookup, ITemplateResolver resolver) {
		lookupResolver.addMapping(lookup, resolver);
		return this;
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

	@Override
	public ThymeleafBuilder withPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	@Override
	public ThymeleafBuilder withSuffix(String suffix) {
		this.suffix = suffix;
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

	@Override
	public TemplateParserBuilder withLookupResolver(String lookup, ResourceResolver resolver) {
		try {
			return withLookupResolver(lookup, resolverAdapter.adapt(resolver));
		} catch (NoResolverAdapterException e) {
			throw new IllegalArgumentException("Can't register resolver", e);
		}
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
