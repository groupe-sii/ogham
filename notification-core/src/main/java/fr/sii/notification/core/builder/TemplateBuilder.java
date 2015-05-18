package fr.sii.notification.core.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.core.template.detector.TemplateEngineDetector;
import fr.sii.notification.core.template.parser.AutoDetectTemplateParser;
import fr.sii.notification.core.template.parser.TemplateParser;
import fr.sii.notification.core.template.resolver.ClassPathTemplateResolver;
import fr.sii.notification.core.template.resolver.FileTemplateResolver;
import fr.sii.notification.core.template.resolver.LookupMappingResolver;
import fr.sii.notification.core.template.resolver.RelativeTemplateResolver;
import fr.sii.notification.core.template.resolver.TemplateResolver;
import fr.sii.notification.core.util.BuilderUtil;
import fr.sii.notification.template.thymeleaf.ThymeleafTemplateDetector;
import fr.sii.notification.template.thymeleaf.builder.ThymeleafBuilder;

public class TemplateBuilder implements Builder<TemplateParser> {
	public static final String PROPERTIES_PREFIX = "notification.template";
	public static final String PREFIX_PROPERTY = PROPERTIES_PREFIX+".prefix";
	public static final String SUFFIX_PROPERTY = PROPERTIES_PREFIX+".suffix";
	
	
	private Map<String, TemplateResolver> resolvers;
	private String prefix;
	private String suffix;
	private List<TemplateParserBuilder> builders;
	private Map<TemplateEngineDetector, TemplateParserBuilder> detectors;
	
	public TemplateBuilder() {
		super();
		resolvers = new HashMap<>();
		prefix = "";
		suffix = "";
		builders = new ArrayList<>();
		detectors = new HashMap<>();
	}
	
	public TemplateBuilder useDefaults() {
		return useDefaults(BuilderUtil.getDefaultProperties());
	}
	
	public TemplateBuilder useDefaults(Properties properties) {
		return registerTemplateParser(new ThymeleafBuilder(), new ThymeleafTemplateDetector())
				.useDefaultLookupResolvers()
				.withPrefix(properties.getProperty(PREFIX_PROPERTY, ""))
				.withSuffix(properties.getProperty(SUFFIX_PROPERTY, ""));
	}
	
	public TemplateBuilder registerTemplateParser(TemplateParserBuilder builder) {
		builders.add(builder);
		return this;
	}
	
	public TemplateBuilder registerTemplateParser(TemplateParserBuilder builder, TemplateEngineDetector detector) {
		registerTemplateParser(builder);
		detectors.put(detector, builder);
		return this;
	}
	
	public TemplateBuilder useDefaultLookupResolvers() {
		withLookupResolver("classpath", new ClassPathTemplateResolver());
		withLookupResolver("file", new FileTemplateResolver());
		return this;
	}
	
	public TemplateBuilder withPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}
	
	public TemplateBuilder withSuffix(String suffix) {
		this.suffix = suffix;
		return this;
	}
	
	public TemplateBuilder withLookupResolver(String lookup, TemplateResolver resolver) {
		resolvers.put(lookup, resolver);
		return this;
	}
	
	@Override
	public TemplateParser build() throws BuildException {
		for(TemplateParserBuilder builder : builders) {
			// set prefix and suffix for each implementation
			builder.withPrefix(prefix);
			builder.withSuffix(suffix);
			// set resolvers for each implementation
			for(Entry<String, TemplateResolver> entry : resolvers.entrySet()) {
				builder.withLookupResolver(entry.getKey(), new RelativeTemplateResolver(entry.getValue(), prefix, suffix));
			}
		}
		// if no detector defined or only one available parser => do not use auto detection
		// use auto detection if more than one parser available
		if(detectors.isEmpty() || builders.size()==1) {
			// TODO: if no template parser available => exception or default parser that does nothing ?
			return builders.get(0).build();
		} else {
			Map<TemplateEngineDetector, TemplateParser> map = new HashMap<>();
			for(Entry<TemplateEngineDetector, TemplateParserBuilder> entry : detectors.entrySet()) {
				map.put(entry.getKey(), entry.getValue().build());
			}
			return new AutoDetectTemplateParser(new LookupMappingResolver(resolvers), map);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <B extends TemplateParserBuilder> B getParserBuilder(Class<B> clazz) {
		for(TemplateParserBuilder builder : builders) {
			if(clazz.isAssignableFrom(builder.getClass())) {
				return (B) builder;
			}
		}
		throw new IllegalArgumentException("No implementation builder exists for "+clazz.getSimpleName());
	}
}
