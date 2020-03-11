package fr.sii.ogham.template.freemarker.builder;

import static freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.context.DefaultBuildContext;
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
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

@SuppressWarnings("squid:S00119")
public abstract class AbstractFreemarkerBuilder<MYSELF extends AbstractFreemarkerBuilder<MYSELF, P>, P> extends AbstractParent<P>
		implements DetectorBuilder<MYSELF>, ResourceResolutionBuilder<MYSELF>, Builder<TemplateParser> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractFreemarkerBuilder.class);

	protected final MYSELF myself;
	protected final BuildContext buildContext;
	private TemplateEngineDetector detector;
	private ResourceResolutionBuilderHelper<MYSELF> resourceResolutionBuilderHelper;
	private Configuration configuration;
	private List<TemplateLoaderAdapter> customAdapters;
	private FreemarkerConfigurationBuilder<MYSELF> configurationBuilder;
	private ClassLoader classLoader;

	protected AbstractFreemarkerBuilder(Class<?> selfType) {
		this(selfType, null, new DefaultBuildContext());
	}

	@SuppressWarnings("unchecked")
	protected AbstractFreemarkerBuilder(Class<?> selfType, P parent, BuildContext buildContext) {
		super(parent);
		myself = (MYSELF) selfType.cast(this);
		this.buildContext = buildContext;
		customAdapters = new ArrayList<>();
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
			configurationBuilder = new FreemarkerConfigurationBuilder<>(myself, buildContext);
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
	 * Merge an existing Freemarker configuration with previously provided
	 * configuration.
	 * 
	 * <p>
	 * The provided configuration is used and any call to
	 * {@link #configuration()} builder methods are applied to the provided
	 * configuration.
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
		return buildContext.register(new FreeMarkerParser(buildConfiguration(), buildResolver()));
	}

	@Override
	public TemplateEngineDetector buildDetector() {
		return detector == null ? buildDefaultDetector() : detector;
	}

	private TemplateEngineDetector buildDefaultDetector() {
		FirstSupportingResourceResolver resolver = buildResolver();
		OrTemplateDetector or = buildContext.register(new OrTemplateDetector());
		or.addDetector(buildContext.register(new FreeMarkerTemplateDetector(resolver, ".ftl", ".ftlh")));
		or.addDetector(buildContext.register(new SimpleResourceEngineDetector(resolver, buildContext.register(new FixedEngineDetector(true)))));
		return or;
	}

	/**
	 * Builds the resolver used by Freemarker to resolve resources
	 * 
	 * @return the resource resolver
	 */
	public FirstSupportingResourceResolver buildResolver() {
		return buildContext.register(new FirstSupportingResourceResolver(buildResolvers()));
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
		builtConfiguration.setTemplateLoader(buildContext.register(new FreeMarkerFirstSupportingTemplateLoader(builtResolver, builtAdapter)));
		builtConfiguration.setTemplateLookupStrategy(buildContext.register(new SkipLocaleForStringContentTemplateLookupStrategy(builtConfiguration.getTemplateLookupStrategy(), builtResolver, builtAdapter)));
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
		adapter.addAdapter(buildContext.register(new ClassPathResolverAdapter(classLoader)));
		adapter.addAdapter(buildContext.register(new FileResolverAdapter()));
		adapter.addAdapter(buildContext.register(new StringResolverAdapter()));
		adapter.setOptions(buildContext.register(new TemplateLoaderOptions()));
		return adapter;
	}

	private void initResolutionBuilder() {
		if (resourceResolutionBuilderHelper == null) {
			resourceResolutionBuilderHelper = new ResourceResolutionBuilderHelper<>(myself, buildContext);
		}
	}
}
