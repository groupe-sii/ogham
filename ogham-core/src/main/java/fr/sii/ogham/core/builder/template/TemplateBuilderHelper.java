package fr.sii.ogham.core.builder.template;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.core.template.detector.FixedEngineDetector;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;
import fr.sii.ogham.core.template.parser.AutoDetectTemplateParser;
import fr.sii.ogham.core.template.parser.AutoDetectTemplateParser.TemplateImplementation;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.common.adapter.FailIfNotFoundVariantResolver;
import fr.sii.ogham.template.common.adapter.FailIfNotFoundWithTestedPathsVariantResolver;
import fr.sii.ogham.template.common.adapter.FirstExistingResourceVariantResolver;
import fr.sii.ogham.template.common.adapter.VariantResolver;

/**
 * Helps to configure a {@link TemplateParser} builder.
 * 
 * <p>
 * It registers and uses {@link Builder}s to instantiate and configure
 * {@link TemplateParser} specialized implementations.
 * </p>
 * 
 * <p>
 * It also configures how to handle missing variant (either fail or do nothing).
 * </p>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder used by custom
 *            {@link TemplateParser} {@link Builder}
 */
public class TemplateBuilderHelper<P> {
	private static final Logger LOG = LoggerFactory.getLogger(TemplateBuilderHelper.class);

	private final P parent;
	private final List<Builder<? extends TemplateParser>> templateBuilders;
	private final EnvironmentBuilder<?> environmentBuilder;
	private boolean missingVariantFail;
	private VariantResolver missingResolver;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling and() method of any registered {@link TemplateParser}
	 * {@link Builder}. The {@link EnvironmentBuilder} is used to evaluate
	 * properties at build time (used by {@link TemplateParser}
	 * {@link Builder}s).
	 * 
	 * @param parent
	 *            the parent builder
	 * @param environmentBuilder
	 *            the configuration for property resolution and evaluation
	 */
	public TemplateBuilderHelper(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super();
		this.parent = parent;
		this.environmentBuilder = environmentBuilder;
		templateBuilders = new ArrayList<>();
	}

	/**
	 * Indicates if some {@link TemplateParser} {@link Builder}s have been
	 * registered
	 * 
	 * @return true if at least one builder has been registered
	 */
	public boolean hasRegisteredTemplates() {
		return !templateBuilders.isEmpty();
	}

	/**
	 * If a variant is missing, then force to fail.
	 * 
	 * <p>
	 * This may be useful if you want for example to always provide a text
	 * fallback when using an html template. So if a client can't read the html
	 * version, the fallback version will still always be readable. So to avoid
	 * forgetting to write text template, set this to true.
	 * </p>
	 * 
	 * @param fail
	 *            if true, it fails if a variant is missing
	 */
	public void missingVariant(boolean fail) {
		this.missingVariantFail = fail;
	}

	/**
	 * Provide custom resolver that will handle a missing variant.
	 * 
	 * @param resolver
	 *            the custom resolver
	 */
	public void missingVariant(VariantResolver resolver) {
		this.missingResolver = resolver;
	}

	/**
	 * Registers and configures a {@link TemplateParser} through a dedicated
	 * builder.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * .register(ThymeleafEmailBuilder.class)
	 *     .detector(new ThymeleafEngineDetector());
	 * </pre>
	 * 
	 * <p>
	 * Your {@link Builder} may implement {@link VariantBuilder} to handle
	 * template {@link Variant}s (used for {@link MultiTemplateContent} that
	 * provide a single path to templates with different extensions for
	 * example).
	 * </p>
	 * 
	 * <p>
	 * Your {@link Builder} may also implement {@link DetectorBuilder} in order
	 * to indicate which kind of templates your {@link TemplateParser} is able
	 * to parse. If your template parse is able to parse any template file you
	 * are using, you may not need to implement {@link DetectorBuilder}.
	 * </p>
	 * 
	 * <p>
	 * In order to be able to keep chaining, you builder instance may provide a
	 * constructor with two arguments:
	 * <ul>
	 * <li>The type of the parent builder ({@code &lt;P&gt;})</li>
	 * <li>The {@link EnvironmentBuilder} instance</li>
	 * </ul>
	 * If you don't care about chaining, just provide a default constructor.
	 * 
	 * @param builderClass
	 *            the builder class to instantiate
	 * @param <T>
	 *            the type of the builder
	 * @return the builder to configure the implementation
	 */
	@SuppressWarnings("unchecked")
	public <T extends Builder<? extends TemplateParser>> T register(Class<T> builderClass) {
		// if already registered => provide same instance
		for (Builder<? extends TemplateParser> builder : templateBuilders) {
			if (builderClass.isAssignableFrom(builder.getClass())) {
				return (T) builder;
			}
		}
		// create the builder instance
		try {
			T builder;
			Constructor<T> constructor = builderClass.getConstructor(parent.getClass(), EnvironmentBuilder.class);
			if (constructor != null) {
				builder = constructor.newInstance(parent, environmentBuilder);
			} else {
				builder = builderClass.newInstance();
			}
			templateBuilders.add(builder);
			return builder;
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			throw new BuildException("Can't instantiate builder from class " + builderClass.getSimpleName(), e);
		}
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
	 * @return the template parser instance
	 * @throws BuildException
	 *             when template parser couldn't be initialized
	 */
	public TemplateParser buildTemplateParser() {
		// TODO: handle enable?
		List<TemplateImplementation> impls = buildTemplateParserImpls();
		if (impls.isEmpty()) {
			// if no template parser available => exception
			throw new BuildException("No parser available. Either disable template features or register a template engine");
		}
		if (impls.size() == 1) {
			// if no detector defined or only one available parser => do not use
			// auto detection
			TemplateParser parser = impls.get(0).getParser();
			LOG.info("Using single template engine: {}", parser);
			return parser;
		}
		LOG.info("Using auto detection mechanism");
		LOG.debug("Auto detection mechanisms: {}", impls);
		return new AutoDetectTemplateParser(impls);
	}

	/**
	 * Instantiates and configures the variant resolution. Variant resolution is
	 * a chain of resolvers. The first resolver that is able to resolve a
	 * variant is used. If no resolver is able to resolve a variant, it uses the
	 * default variant resolver (see {@link #missingVariant(boolean)} and
	 * {@link #missingVariant(VariantResolver)}).
	 * 
	 * @return the variant resolver
	 */
	public VariantResolver buildVariant() {
		FirstExistingResourceVariantResolver variantResolver = new FirstExistingResourceVariantResolver(buildDefaultVariantResolver());
		for (Builder<? extends TemplateParser> builder : templateBuilders) {
			if (builder instanceof VariantBuilder) {
				variantResolver.addVariantResolver(((VariantBuilder<?>) builder).buildVariant());
			}
		}
		return variantResolver;
	}

	private VariantResolver buildDefaultVariantResolver() {
		if (missingResolver != null) {
			return missingResolver;
		}
		if (missingVariantFail) {
			return new FailIfNotFoundVariantResolver();
		}
		FailIfNotFoundWithTestedPathsVariantResolver fail = new FailIfNotFoundWithTestedPathsVariantResolver();
		for (Builder<? extends TemplateParser> builder : templateBuilders) {
			if (builder instanceof VariantBuilder) {
				fail.addVariantResolver(((VariantBuilder<?>) builder).buildVariant());
			}
		}
		return fail;
	}

	private List<TemplateImplementation> buildTemplateParserImpls() {
		List<TemplateImplementation> impls = new ArrayList<>();
		for (Builder<? extends TemplateParser> builder : templateBuilders) {
			TemplateEngineDetector detector;
			if (builder instanceof DetectorBuilder) {
				detector = ((DetectorBuilder<?>) builder).buildDetector();
			} else {
				detector = new FixedEngineDetector(true);
			}
			impls.add(new TemplateImplementation(detector, builder.build()));
		}
		return impls;
	}
}
