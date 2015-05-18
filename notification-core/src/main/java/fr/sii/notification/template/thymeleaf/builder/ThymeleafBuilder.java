package fr.sii.notification.template.thymeleaf.builder;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.notification.core.builder.TemplateParserBuilder;
import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.core.template.parser.TemplateParser;
import fr.sii.notification.core.template.resolver.TemplateResolver;
import fr.sii.notification.template.exception.NoResolverAdapter;
import fr.sii.notification.template.thymeleaf.ThymeleafLookupMappingResolver;
import fr.sii.notification.template.thymeleaf.ThymeleafParser;
import fr.sii.notification.template.thymeleaf.adapter.ChainResolverAdapter;
import fr.sii.notification.template.thymeleaf.adapter.ClassPathResolverAdapter;
import fr.sii.notification.template.thymeleaf.adapter.FileResolverAdapter;
import fr.sii.notification.template.thymeleaf.adapter.ThymeleafResolverAdapter;

public class ThymeleafBuilder implements TemplateParserBuilder {

	private TemplateEngine engine;
	private ThymeleafLookupMappingResolver<ITemplateResolver> lookupResolver;
	private ChainResolverAdapter resolverAdapter;
	private String prefix;
	private String suffix;
	
	public ThymeleafBuilder() {
		super();
		engine = new TemplateEngine();
		lookupResolver = new ThymeleafLookupMappingResolver<ITemplateResolver>();
		resolverAdapter = new ChainResolverAdapter(new ClassPathResolverAdapter(), new FileResolverAdapter());
		prefix = "";
		suffix = "";
	}

	@Override
	public TemplateParser build() throws BuildException {
		for(ITemplateResolver resolver : lookupResolver.getResolvers()) {
			if(resolver instanceof org.thymeleaf.templateresolver.TemplateResolver) {
				org.thymeleaf.templateresolver.TemplateResolver templateResolver = (org.thymeleaf.templateresolver.TemplateResolver) resolver;
				templateResolver.setPrefix(prefix);
				templateResolver.setSuffix(suffix);
			}
		}
		return new ThymeleafParser(engine, lookupResolver);
	}
	
	public ThymeleafBuilder withLookupResolver(String lookup, ITemplateResolver resolver) {
		lookupResolver.addMapping(lookup, resolver);
		return this;
	}

	public ThymeleafBuilder withTemplateEngine(TemplateEngine engine) {
		this.engine = engine;
		return this;
	}
	
	public ThymeleafBuilder withPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}
	
	public ThymeleafBuilder withSuffix(String suffix) {
		this.suffix = suffix;
		return this;
	}
	
	public ThymeleafBuilder registerResolverAdapter(ThymeleafResolverAdapter adapter) {
		resolverAdapter.addAdapter(adapter);
		return this;
	}
	
	@Override
	public TemplateParserBuilder withLookupResolver(String lookup, TemplateResolver resolver) {
		try {
			return withLookupResolver(lookup, resolverAdapter.adapt(resolver));
		} catch (NoResolverAdapter e) {
			throw new IllegalArgumentException("Can't register resolver", e);
		}
	}
}
