package fr.sii.ogham.template.thymeleaf.common.buider;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
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
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.thymeleaf.common.SimpleThymeleafContextConverter;
import fr.sii.ogham.template.thymeleaf.common.TemplateResolverOptions;
import fr.sii.ogham.template.thymeleaf.common.ThymeleafContextConverter;
import fr.sii.ogham.template.thymeleaf.common.ThymeleafParser;
import fr.sii.ogham.template.thymeleaf.common.adapter.FirstSupportingResolverAdapter;
import fr.sii.ogham.template.thymeleaf.common.adapter.TemplateResolverAdapter;

@SuppressWarnings("squid:S00119")
public abstract class AbstractThymeleafBuilder<MYSELF extends AbstractThymeleafBuilder<MYSELF, P, E>, P, E extends AbstractThymeleafEngineConfigBuilder<E, MYSELF>> extends AbstractParent<P>
		implements DetectorBuilder<MYSELF>, ResourceResolutionBuilder<MYSELF>, Builder<TemplateParser> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractThymeleafBuilder.class);

	protected final MYSELF myself;
	protected final BuildContext buildContext;
	protected TemplateEngineDetector detector;
	protected ResourceResolutionBuilderHelper<MYSELF> resourceResolutionBuilderHelper;
	protected TemplateEngine engine;
	protected ThymeleafContextConverter contextConverter;
	protected E engineBuilder;
	protected final List<TemplateResolverAdapter> customAdapters;
	protected final ConfigurationValueBuilderHelper<MYSELF, Boolean> enableCacheValueBuilder;

	protected AbstractThymeleafBuilder(Class<?> selfType) {
		this(selfType, null, new DefaultBuildContext());
	}

	@SuppressWarnings("unchecked")
	protected AbstractThymeleafBuilder(Class<?> selfType, P parent, BuildContext buildContext) {
		super(parent);
		myself = (MYSELF) selfType.cast(this);
		this.buildContext = buildContext;
		customAdapters = new ArrayList<>();
		enableCacheValueBuilder = buildContext.newConfigurationValueBuilder(myself, Boolean.class);
	}

	protected AbstractThymeleafBuilder(P parent, BuildContext buildContext) {
		this(AbstractThymeleafBuilder.class, parent, buildContext);
	}

	/**
	 * Enable/disable cache for templates.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #cache()}.
	 * 
	 * <pre>
	 * .cache(false)
	 * .cache()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <pre>
	 * .cache(false)
	 * .cache()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * In both cases, {@code cache(false)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param enable
	 *            enable or disable cache
	 * @return this instance for fluent chaining
	 */
	public MYSELF cache(Boolean enable) {
		enableCacheValueBuilder.setValue(enable);
		return myself;
	}

	/**
	 * Enable/disable cache for templates.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .cache()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #cache(Boolean)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .cache(false)
	 * .cache()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * The value {@code false} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<MYSELF, Boolean> cache() {
		return enableCacheValueBuilder;
	}

	/**
	 * Fluent configurer for Thymeleaf engine configuration.
	 * 
	 * @return the fluent builder for Thymeleaf engine
	 */
	public E engine() {
		if (engineBuilder == null) {
			engineBuilder = getThymeleafEngineConfigBuilder();
		}
		return engineBuilder;
	}

	/**
	 * Sets a Thymeleaf engine.
	 * 
	 * This value preempts any other value defined by calling {@link #engine()}
	 * method.
	 * 
	 * If this method is called several times, only the last provider is used.
	 * 
	 * @param engine
	 *            the Thymeleaf engine
	 * @return this instance for fluent chaining
	 */
	public MYSELF engine(TemplateEngine engine) {
		this.engine = engine;
		return myself;
	}

	/**
	 * Ogham provides a generic context concept for template parsing. Thymeleaf
	 * uses also a context concept. A context converter
	 * ({@link ThymeleafContextConverter}) is the way to transform an Ogham
	 * context into a Thymeleaf context.
	 * 
	 * <p>
	 * Ogham provides and registers the default converter
	 * 
	 * <p>
	 * If this method is called several times, only the last provider is used.
	 * 
	 * @param converter
	 *            the context converter
	 * @return this instance for fluent chaining
	 */
	public MYSELF contextConverter(ThymeleafContextConverter converter) {
		this.contextConverter = converter;
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
	 * ({@link ResourceResolver}). Thymeleaf uses its own template resolution
	 * mechanism ({@link ITemplateResolver}). A resolver adapter
	 * ({@link TemplateResolverAdapter}) is the way to transform a
	 * {@link ResourceResolver} into a {@link ITemplateResolver}.
	 * 
	 * <p>
	 * Ogham provides and registers default resolver adapters but you may need
	 * to use a custom {@link ResourceResolver}. So you also need to provide the
	 * corresponding {@link TemplateResolverAdapter}.
	 * 
	 * @param adapter
	 *            the resolver adapter
	 * @return this instance for fluent chaining
	 */
	public MYSELF resolverAdapter(TemplateResolverAdapter adapter) {
		customAdapters.add(adapter);
		return myself;
	}

	@Override
	public TemplateParser build() {
		LOG.info("Thymeleaf parser is registered");
		return buildContext.register(new ThymeleafParser(buildEngine(), buildResolver(), buildContext()));
	}

	@Override
	public TemplateEngineDetector buildDetector() {
		return detector == null ? createTemplateDetector() : detector;
	}

	/**
	 * Builds the resolver used by Thymeleaf to resolve resources
	 * 
	 * @return the resource resolver
	 */
	public FirstSupportingResourceResolver buildResolver() {
		return buildContext.register(new FirstSupportingResourceResolver(buildResolvers()));
	}

	protected TemplateEngine buildEngine() {
		TemplateEngine builtEngine;
		if (engine != null) {
			LOG.debug("Using custom Thymeleaf engine");
			builtEngine = engine;
		} else if (engineBuilder != null) {
			LOG.debug("Using custom Thymeleaf engine built using engine()");
			builtEngine = engineBuilder.build();
		} else {
			LOG.debug("Using default Thymeleaf engine");
			builtEngine = buildContext.register(new TemplateEngine());
		}
		builtEngine.addTemplateResolver(buildTemplateResolver(builtEngine));
		return builtEngine;
	}

	protected abstract TemplateEngineDetector createTemplateDetector();

	protected abstract ITemplateResolver buildTemplateResolver(TemplateEngine builtEngine);

	protected abstract E getThymeleafEngineConfigBuilder();

	protected ThymeleafContextConverter buildContext() {
		return contextConverter == null ? buildContext.register(new SimpleThymeleafContextConverter()) : contextConverter;
	}

	private List<ResourceResolver> buildResolvers() {
		initResolutionBuilder();
		return resourceResolutionBuilderHelper.buildResolvers();
	}

	protected abstract FirstSupportingResolverAdapter buildAdapters();

	protected TemplateResolverOptions buildTemplateResolverOptions() {
		TemplateResolverOptions options = buildContext.register(new TemplateResolverOptions());
		options.setCacheable(enableCacheValueBuilder.getValue());
		// TODO: handle other options
		return options;
	}

	private void initResolutionBuilder() {
		if (resourceResolutionBuilderHelper == null) {
			resourceResolutionBuilderHelper = new ResourceResolutionBuilderHelper<>(myself, buildContext);
		}
	}
}
