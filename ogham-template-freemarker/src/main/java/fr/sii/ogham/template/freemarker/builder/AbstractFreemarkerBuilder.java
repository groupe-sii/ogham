package fr.sii.ogham.template.freemarker.builder;

import static freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilderDelegate;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.core.builder.resolution.ClassPathResolutionBuilder;
import fr.sii.ogham.core.builder.resolution.FileResolutionBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilderHelper;
import fr.sii.ogham.core.builder.resolution.StringResolutionBuilder;
import fr.sii.ogham.core.builder.template.DetectorBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.template.detector.FixedEngineDetector;
import fr.sii.ogham.core.template.detector.OrTemplateDetector;
import fr.sii.ogham.core.template.detector.SimpleResourceEngineDetector;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.freemarker.FreeMarkerFirstSupportingTemplateLoader;
import fr.sii.ogham.template.freemarker.FreeMarkerParser;
import fr.sii.ogham.template.freemarker.FreeMarkerTemplateDetector;
import fr.sii.ogham.template.freemarker.SkipLocaleForStringContentTemplateLookupStrategy;
import fr.sii.ogham.template.freemarker.TemplateLoaderOptions;
import fr.sii.ogham.template.freemarker.adapter.ClassPathResolverAdapter;
import fr.sii.ogham.template.freemarker.adapter.FileResolverAdapter;
import fr.sii.ogham.template.freemarker.adapter.FirstSupportingResolverAdapter;
import fr.sii.ogham.template.freemarker.adapter.StringResolverAdapter;
import fr.sii.ogham.template.freemarker.adapter.TemplateLoaderAdapter;
import fr.sii.ogham.template.freemarker.configurer.DefaultFreemarkerEmailConfigurer;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

@SuppressWarnings("squid:S00119")
public abstract class AbstractFreemarkerBuilder<MYSELF extends AbstractFreemarkerBuilder<MYSELF, P>, P> extends AbstractParent<P>
		implements DetectorBuilder<MYSELF>, ResourceResolutionBuilder<MYSELF>, Builder<TemplateParser> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractFreemarkerBuilder.class);

	protected MYSELF myself;
	protected EnvironmentBuilder<MYSELF> environmentBuilder;
	private TemplateEngineDetector detector;
	private ResourceResolutionBuilderHelper<MYSELF> resourceResolutionBuilderHelper;
	private Configuration configuration;
	private List<TemplateLoaderAdapter> customAdapters;
	private FreemarkerConfigurationBuilder<MYSELF> configurationBuilder;
	private ClassLoader classLoader;

	protected AbstractFreemarkerBuilder(Class<?> selfType) {
		this(selfType, null, null);
	}

	@SuppressWarnings("unchecked")
	protected AbstractFreemarkerBuilder(Class<?> selfType, P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		myself = (MYSELF) selfType.cast(this);
		if (environmentBuilder != null) {
			environment(environmentBuilder);
		}
		customAdapters = new ArrayList<>();
	}

	/**
	 * Configures environment for the builder (and sub-builders). Environment
	 * consists of configuration properties/values that are used to configure
	 * the system (see {@link EnvironmentBuilder} for more information).
	 * 
	 * You can use system properties:
	 * 
	 * <pre>
	 * .environment()
	 *    .systemProperties();
	 * </pre>
	 * 
	 * Or, you can load properties from a file:
	 * 
	 * <pre>
	 * .environment()
	 *    .properties("/path/to/file.properties")
	 * </pre>
	 * 
	 * Or using directly a {@link Properties} object:
	 * 
	 * <pre>
	 * Properties myprops = new Properties();
	 * myprops.setProperty("foo", "bar");
	 * .environment()
	 *    .properties(myprops)
	 * </pre>
	 * 
	 * Or defining directly properties:
	 * 
	 * <pre>
	 * .environment()
	 *    .properties()
	 *       .set("foo", "bar")
	 * </pre>
	 * 
	 * 
	 * <p>
	 * If no environment was previously used, it creates a new one. Then each
	 * time you call {@link #environment()}, the same instance is used.
	 * </p>
	 * 
	 * @return the builder to configure properties handling
	 */
	public EnvironmentBuilder<MYSELF> environment() {
		if (environmentBuilder == null) {
			environmentBuilder = new SimpleEnvironmentBuilder<>(myself);
		}
		return environmentBuilder;
	}

	/**
	 * NOTE: this is mostly for advance usage (when creating a custom module).
	 * 
	 * Inherits environment configuration from another builder. This is useful
	 * for configuring independently different parts of Ogham but keeping a
	 * whole coherence (see {@link DefaultFreemarkerEmailConfigurer} for an
	 * example of use).
	 * 
	 * The same instance is shared meaning that all changes done here will also
	 * impact the other builder.
	 * 
	 * <p>
	 * If a previous builder was defined (by calling {@link #environment()} for
	 * example), the new builder will override it.
	 * 
	 * @param builder
	 *            the builder to inherit
	 * @return this instance for fluent chaining
	 */
	public MYSELF environment(EnvironmentBuilder<?> builder) {
		environmentBuilder = new EnvironmentBuilderDelegate<>(myself, builder);
		return myself;
	}

	@Override
	public MYSELF detector(TemplateEngineDetector detector) {
		this.detector = detector;
		return myself;
	}

	@Override
	public ClassPathResolutionBuilder<MYSELF> classpath() {
		initResolutionBuilder();
		return resourceResolutionBuilderHelper.classpath();
	}

	@Override
	public FileResolutionBuilder<MYSELF> file() {
		initResolutionBuilder();
		return resourceResolutionBuilderHelper.file();
	}

	@Override
	public StringResolutionBuilder<MYSELF> string() {
		initResolutionBuilder();
		return resourceResolutionBuilderHelper.string();
	}

	@Override
	public MYSELF resolver(ResourceResolver resolver) {
		initResolutionBuilder();
		return resourceResolutionBuilderHelper.resolver(resolver);
	}

	/**
	 * Ogham provides a generic resource resolution mechanism
	 * ({@link ResourceResolver}). Freemarker uses its own template resolution
	 * mechanism ({@link TemplateLoader}). A resolver adapter
	 * ({@link TemplateLoaderAdapter}) is the way to transform a
	 * {@link ResourceResolver} into a {@link TemplateLoader}.
	 * 
	 * <p>
	 * Ogham provides and registers default resolver adapters but you may need
	 * to use a custom {@link ResourceResolver}. So you also need to provide the
	 * corresponding {@link TemplateLoaderAdapter}.
	 * 
	 * @param adapter
	 *            the resolver adapter
	 * @return this instance for fluent chaining
	 */
	public MYSELF resolverAdapter(TemplateLoaderAdapter adapter) {
		customAdapters.add(adapter);
		return myself;
	}

	/**
	 * Fluent configurer for Freemarker configuration.
	 * 
	 * @return the fluent builder for Freemarker configuration object
	 */
	public FreemarkerConfigurationBuilder<MYSELF> configuration() {
		if (configurationBuilder == null) {
			configurationBuilder = new FreemarkerConfigurationBuilder<>(myself, environmentBuilder);
		}
		return configurationBuilder;
	}

	/**
	 * Sets a Freemarker configuration.
	 * 
	 * This value preempts any other value defined by calling
	 * {@link #configuration()} method. It means that the provided configuration
	 * is used as-is and any call to {@link #configuration()} builder methods
	 * has no effect on the provided configuration. You have to manually
	 * configure it.
	 * 
	 * If this method is called several times, only the last provided
	 * configuration is used.
	 * 
	 * @param configuration
	 *            the Freemarker configuration
	 * @return this instance for fluent chaining
	 */
	public MYSELF configuration(Configuration configuration) {
		this.configuration = configuration;
		return myself;
	}

	/**
	 * Merge an existing Freemarker configuration with previously provided configuration.
	 * 
	 * <p>
	 * The provided configuration is used and
	 * any call to {@link #configuration()} builder methods are applied to the
	 * provided configuration.
	 * 
	 * 
	 * @param configuration
	 *            The Freemarker configuration to apply
	 * @return this instance for fluent chaining
	 */
	public MYSELF mergeConfiguration(Configuration configuration) {
		configuration().base(configuration);
		return myself;
	}

	/**
	 * Set the {@link ClassLoader} to use for loading classpath resources.
	 * 
	 * <p>
	 * Loading resources from classpath requires a {@link ClassLoader}. Several
	 * class loaders may be defined in an application to isolate parts of the
	 * application. FreeMarker requires you to provide a {@link ClassLoader} for
	 * finding resources in the classpath. This is done for security reasons.
	 * 
	 * <p>
	 * By default, Ogham uses the current thread class loader.
	 * 
	 * @param classLoader
	 *            the class loader to use
	 * @return this instance for fluent chaining
	 */
	public MYSELF classLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
		return myself;
	}

	@Override
	public TemplateParser build() {
		LOG.info("Freemarker parser is registered");
		return new FreeMarkerParser(buildConfiguration(), buildResolver());
	}

	@Override
	public TemplateEngineDetector buildDetector() {
		return detector == null ? buildDefaultDetector() : detector;
	}

	private TemplateEngineDetector buildDefaultDetector() {
		FirstSupportingResourceResolver resolver = buildResolver();
		OrTemplateDetector or = new OrTemplateDetector();
		or.addDetector(new FreeMarkerTemplateDetector(resolver, ".ftl"));
		or.addDetector(new SimpleResourceEngineDetector(resolver, new FixedEngineDetector(true)));
		return or;
	}

	/**
	 * Builds the resolver used by Freemarker to resolve resources
	 * 
	 * @return the resource resolver
	 */
	public FirstSupportingResourceResolver buildResolver() {
		return new FirstSupportingResourceResolver(buildResolvers());
	}

	private Configuration buildConfiguration() {
		Configuration builtConfiguration;
		if (this.configuration != null) {
			builtConfiguration = this.configuration;
		} else if (configurationBuilder != null) {
			builtConfiguration = configurationBuilder.build();
		} else {
			builtConfiguration = new Configuration(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
			builtConfiguration.setDefaultEncoding("UTF-8");
			builtConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		}
		FirstSupportingResourceResolver builtResolver = buildResolver();
		FirstSupportingResolverAdapter builtAdapter = buildAdapter();
		builtConfiguration.setTemplateLoader(new FreeMarkerFirstSupportingTemplateLoader(builtResolver, builtAdapter));
		builtConfiguration.setTemplateLookupStrategy(new SkipLocaleForStringContentTemplateLookupStrategy(builtConfiguration.getTemplateLookupStrategy(), builtResolver, builtAdapter));
		return builtConfiguration;
	}

	protected List<ResourceResolver> buildResolvers() {
		initResolutionBuilder();
		return resourceResolutionBuilderHelper.buildResolvers();
	}

	protected FirstSupportingResolverAdapter buildAdapter() {
		FirstSupportingResolverAdapter adapter = new FirstSupportingResolverAdapter();
		for (TemplateLoaderAdapter custom : customAdapters) {
			adapter.addAdapter(custom);
		}
		adapter.addAdapter(new ClassPathResolverAdapter(classLoader));
		adapter.addAdapter(new FileResolverAdapter());
		adapter.addAdapter(new StringResolverAdapter());
		adapter.setOptions(new TemplateLoaderOptions());
		return adapter;
	}

	private void initResolutionBuilder() {
		if (resourceResolutionBuilderHelper == null) {
			resourceResolutionBuilderHelper = new ResourceResolutionBuilderHelper<>(myself, environmentBuilder);
		}
	}
}
