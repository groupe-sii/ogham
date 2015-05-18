package fr.sii.notification.core.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.core.template.detector.FixedEngineDetector;
import fr.sii.notification.core.template.detector.TemplateEngineDetector;
import fr.sii.notification.core.template.parser.AutoDetectTemplateParser;
import fr.sii.notification.core.template.parser.TemplateParser;
import fr.sii.notification.core.template.resolver.ClassPathTemplateResolver;
import fr.sii.notification.core.template.resolver.FileTemplateResolver;
import fr.sii.notification.core.template.resolver.LookupMappingResolver;
import fr.sii.notification.core.template.resolver.RelativeTemplateResolver;
import fr.sii.notification.core.template.resolver.TemplateResolver;
import fr.sii.notification.core.util.BuilderUtil;
import fr.sii.notification.template.thymeleaf.ThymeleafParser;
import fr.sii.notification.template.thymeleaf.ThymeleafTemplateDetector;
import fr.sii.notification.template.thymeleaf.builder.ThymeleafBuilder;

/**
 * A specialized builder for template management. It helps construct the
 * template engines. By default, the builder construct the following engines:
 * <ul>
 * <li>{@link ThymeleafParser} (delegate construction to
 * {@link ThymeleafBuilder})</li>
 * </ul>
 * 
 * By default, the builder will use the following template resolvers:
 * <ul>
 * <li>Resolver that is able to handle classpath resolution (
 * {@link ClassPathTemplateResolver})</li>
 * <li>Resolver that is able to handle file resolution (
 * {@link FileTemplateResolver})</li>
 * </ul>
 * 
 * This builder is also able to register a prefix and a suffix for template
 * resolution. The aim is to be able to provide only the name of the template
 * without needing to provide the full path to it.
 * 
 * @author Aurélien Baudet
 *
 */
public class TemplateBuilder implements TemplateParserBuilder {
	/**
	 * The prefix for properties used by the template engines
	 */
	public static final String PROPERTIES_PREFIX = "notification.template";

	/**
	 * The property key for the prefix of the template resolution
	 */
	public static final String PREFIX_PROPERTY = PROPERTIES_PREFIX + ".prefix";

	/**
	 * The property key for the suffix of the template resolution
	 */
	public static final String SUFFIX_PROPERTY = PROPERTIES_PREFIX + ".suffix";

	/**
	 * The map that temporarily stores the template resolver implementations
	 * according to the lookup prefix
	 */
	private Map<String, TemplateResolver> resolvers;

	/**
	 * The prefix for template resolution
	 */
	private String prefix;

	/**
	 * The suffix for template resolution
	 */
	private String suffix;

	/**
	 * The list of delegate builders for each template engine. Each builder is
	 * specialized for constructing the real template parser.
	 */
	private List<TemplateParserBuilder> builders;

	/**
	 * A map that stores the engine detector and the associated engine. Each
	 * detector will indicate if the engine is able to parse the template.
	 */
	private Map<TemplateEngineDetector, TemplateParserBuilder> detectors;

	public TemplateBuilder() {
		super();
		resolvers = new HashMap<>();
		prefix = "";
		suffix = "";
		builders = new ArrayList<>();
		detectors = new HashMap<>();
	}

	/**
	 * Tells the builder to use all default behaviors and values. It will enable
	 * the following template engines:
	 * <ul>
	 * <li>{@link ThymeleafParser} with the associated detector (
	 * {@link ThymeleafTemplateDetector}) by calling {@link #withThymeleaf()}</li>
	 * </ul>
	 * 
	 * It will also use the default template resolvers:
	 * <ul>
	 * <li>Resolver that is able to handle classpath resolution (
	 * {@link ClassPathTemplateResolver})</li>
	 * <li>Resolver that is able to handle file resolution (
	 * {@link FileTemplateResolver})</li>
	 * </ul>
	 * 
	 * If the system properties provide values for prefix (
	 * notification.template.prefix) and suffix (notification.template.suffix),
	 * then the builder will use them. If no value defined for these properties,
	 * then empty strings will be used.
	 * 
	 * @return this builder instance for fluent use
	 */
	public TemplateBuilder useDefaults() {
		return useDefaults(BuilderUtil.getDefaultProperties());
	}

	/**
	 * Tells the builder to use all default behaviors and values. It will enable
	 * the following template engines:
	 * <ul>
	 * <li>{@link ThymeleafParser} with the associated detector (
	 * {@link ThymeleafTemplateDetector})</li>
	 * </ul>
	 * 
	 * It will also use the default template resolvers (by calling
	 * {@link #useDefaultLookupResolvers()}):
	 * <ul>
	 * <li>Resolver that is able to handle classpath resolution (
	 * {@link ClassPathTemplateResolver})</li>
	 * <li>Resolver that is able to handle file resolution (
	 * {@link FileTemplateResolver})</li>
	 * </ul>
	 * 
	 * If the provided properties provide values for prefix (
	 * notification.template.prefix) and suffix (notification.template.suffix),
	 * then the builder will use them. If no value defined for these properties,
	 * then empty strings will be used.
	 * 
	 * @param properties
	 *            indicate which properties to use instead of using the system
	 *            ones
	 * @return this builder instance for fluent use
	 */
	public TemplateBuilder useDefaults(Properties properties) {
		withThymeleaf();
		useDefaultLookupResolvers();
		withPrefix(properties.getProperty(PREFIX_PROPERTY, ""));
		withSuffix(properties.getProperty(SUFFIX_PROPERTY, ""));
		return this;
	}

	/**
	 * Register a specialized builder for a particular template engine. Using
	 * this method will associate a detector to this engine that will accept all
	 * templates.
	 * 
	 * @param builder
	 *            the builder to register
	 * @return this builder instance for fluent use
	 */
	public TemplateBuilder registerTemplateParser(TemplateParserBuilder builder) {
		return registerTemplateParser(builder, new FixedEngineDetector());
	}

	/**
	 * Register a specialized builder for a particular template engine. It also
	 * registers the associated detector to indicate if the engine can handle
	 * the templates at runtime.
	 * 
	 * @param builder
	 *            the builder to register
	 * @return this builder instance for fluent use
	 */
	public TemplateBuilder registerTemplateParser(TemplateParserBuilder builder, TemplateEngineDetector detector) {
		builders.add(builder);
		detectors.put(detector, builder);
		return this;
	}

	/**
	 * Tells the builder to use the default template resolvers:
	 * <ul>
	 * <li>Resolver that is able to handle classpath resolution (
	 * {@link ClassPathTemplateResolver})</li>
	 * <li>Resolver that is able to handle file resolution (
	 * {@link FileTemplateResolver})</li>
	 * </ul>
	 * 
	 * This method is automatically called by {@link #useDefaults()} or
	 * {@link #useDefaults(Properties)}.
	 * 
	 * @return this builder instance for fluent use
	 */
	public TemplateBuilder useDefaultLookupResolvers() {
		withLookupResolver("classpath", new ClassPathTemplateResolver());
		withLookupResolver("file", new FileTemplateResolver());
		return this;
	}

	@Override
	public TemplateBuilder withPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	@Override
	public TemplateBuilder withSuffix(String suffix) {
		this.suffix = suffix;
		return this;
	}

	@Override
	public TemplateBuilder withLookupResolver(String lookup, TemplateResolver resolver) {
		resolvers.put(lookup, resolver);
		return this;
	}

	/**
	 * Enable Thymeleaf template engine. This engine is used only if the
	 * associated detector ({@link ThymeleafTemplateDetector}) indicates that
	 * Thymeleaf is able to handle the provided template.
	 * 
	 * @return this builder instance for fluent use
	 */
	public TemplateBuilder withThymeleaf() {
		registerTemplateParser(new ThymeleafBuilder(), new ThymeleafTemplateDetector());
		return this;
	}

	/**
	 * Build the template parser according to options previously enabled. If
	 * only one template engine has been activated then the parser will be this
	 * template engine parser. If there are several activated engines, then the
	 * builder will generate an {@link AutoDetectTemplateParser}. This kind of
	 * parser is able to detect which parser to use according to the provided
	 * template at runtime. The auto-detection is delegated to each defined
	 * {@link TemplateEngineDetector} associated with each engine.
	 * 
	 * The builder will also construct a template resolver based on the
	 * previously defined mappings. This mapping is done using a
	 * {@link LookupMappingResolver}. It uses a map indexed by the lookup string
	 * (classpath, file, ...) and the resolver implementation as value.
	 * 
	 * The builder will also provide the previously defined prefix and suffix to
	 * the resolvers and to the delegated builders.
	 * 
	 * @return The parser implementation for templating system. If only one
	 *         template engine defined, then use it directly. Otherwise use the
	 *         auto-detection feature to automatically detect at runtime which
	 *         engine to use
	 * @throws BuildException
	 *             when the builder can't construct the parser
	 */
	@Override
	public TemplateParser build() throws BuildException {
		for (TemplateParserBuilder builder : builders) {
			// set prefix and suffix for each implementation
			builder.withPrefix(prefix);
			builder.withSuffix(suffix);
			// set resolvers for each implementation
			for (Entry<String, TemplateResolver> entry : resolvers.entrySet()) {
				builder.withLookupResolver(entry.getKey(), new RelativeTemplateResolver(entry.getValue(), prefix, suffix));
			}
		}
		// if no detector defined or only one available parser => do not use
		// auto detection
		// use auto detection if more than one parser available
		if (detectors.isEmpty() || builders.size() == 1) {
			// TODO: if no template parser available => exception or default
			// parser that does nothing ?
			return builders.get(0).build();
		} else {
			Map<TemplateEngineDetector, TemplateParser> map = new HashMap<>();
			for (Entry<TemplateEngineDetector, TemplateParserBuilder> entry : detectors.entrySet()) {
				map.put(entry.getKey(), entry.getValue().build());
			}
			return new AutoDetectTemplateParser(new LookupMappingResolver(resolvers), map);
		}
	}

	/**
	 * Get reference to the specialized builder. It may be useful to fine tune
	 * the template engine.
	 * 
	 * @param clazz
	 *            the class of the parser builder to get
	 * @return the template parser builder instance
	 */
	@SuppressWarnings("unchecked")
	public <B extends TemplateParserBuilder> B getParserBuilder(Class<B> clazz) {
		for (TemplateParserBuilder builder : builders) {
			if (clazz.isAssignableFrom(builder.getClass())) {
				return (B) builder;
			}
		}
		throw new IllegalArgumentException("No implementation builder exists for " + clazz.getSimpleName());
	}

	/**
	 * Get the reference to the specialized builder for Thymeleaf. It may be
	 * useful to fine tune Thymeleaf engine.
	 * 
	 * @return The Thymeleaf builder
	 */
	public ThymeleafBuilder getThymeleafParser() {
		return getParserBuilder(ThymeleafBuilder.class);
	}
}
