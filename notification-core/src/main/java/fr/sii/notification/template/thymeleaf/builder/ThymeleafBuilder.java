package fr.sii.notification.template.thymeleaf.builder;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import fr.sii.notification.core.builder.TemplateParserBuilder;
import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.core.template.parser.TemplateParser;
import fr.sii.notification.template.thymeleaf.ThymeleafLookupMappingResolver;
import fr.sii.notification.template.thymeleaf.ThymeleafParser;

public class ThymeleafBuilder implements TemplateParserBuilder {

	private TemplateEngine engine;
	private ThymeleafLookupMappingResolver<ITemplateResolver> lookupResolver;
	private String prefix;
	private String suffix;
	
	public ThymeleafBuilder() {
		super();
		engine = new TemplateEngine();
		lookupResolver = new ThymeleafLookupMappingResolver<ITemplateResolver>();
		prefix = "";
		suffix = "";
	}

	@Override
	public TemplateParser build() throws BuildException {
		return new ThymeleafParser(engine, lookupResolver);
	}
	
	public ThymeleafBuilder addLookupMapping(String lookup, ITemplateResolver resolver) {
		lookupResolver.addMapping(lookup, resolver);
		return this;
	}

	public ThymeleafBuilder withDefaultLookupMappings() {
		TemplateResolver resolver = new ClassLoaderTemplateResolver();
		resolver.setPrefix(prefix);
		resolver.setSuffix(suffix);
		addLookupMapping("classpath", resolver);
		resolver = new FileTemplateResolver();
		resolver.setPrefix(prefix);
		resolver.setSuffix(suffix);
		addLookupMapping("file", new FileTemplateResolver());
//		addLookupMapping("classpath", new ServletContextTemplateResolver());
//		addLookupMapping("classpath", new UrlTemplateResolver());
		return this;
	}
	
	public ThymeleafBuilder withTemplateEngine(TemplateEngine engine) {
		this.engine = engine;
		return this;
	}
	
	public ThymeleafBuilder withPrefix(String prefix) {
		this.prefix = prefix;
		for(ITemplateResolver resolver : lookupResolver.getResolvers()) {
			if(resolver instanceof TemplateResolver) {
				((TemplateResolver) resolver).setPrefix(prefix);
			}
		}
		return this;
	}
	
	public ThymeleafBuilder withSuffix(String suffix) {
		this.suffix = suffix;
		for(ITemplateResolver resolver : lookupResolver.getResolvers()) {
			if(resolver instanceof TemplateResolver) {
				((TemplateResolver) resolver).setSuffix(suffix);
			}
		}
		return this;
	}
	
	public ThymeleafBuilder withDefaults() {
		return withDefaultLookupMappings().withPrefix("").withSuffix("");
	}
}
