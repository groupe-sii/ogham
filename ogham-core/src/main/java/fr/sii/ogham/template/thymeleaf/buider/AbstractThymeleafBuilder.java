package fr.sii.ogham.template.thymeleaf.buider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilderHelper;
import fr.sii.ogham.core.builder.template.DetectorBuilder;
import fr.sii.ogham.core.builder.template.PrefixSuffixBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.RelativeResolver;
import fr.sii.ogham.core.resource.resolver.RelativisableResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.template.thymeleaf.SimpleThymeleafContextConverter;
import fr.sii.ogham.template.thymeleaf.TemplateResolverOptions;
import fr.sii.ogham.template.thymeleaf.ThymeLeafFirstSupportingTemplateResolver;
import fr.sii.ogham.template.thymeleaf.ThymeleafContextConverter;
import fr.sii.ogham.template.thymeleaf.ThymeleafParser;
import fr.sii.ogham.template.thymeleaf.ThymeleafTemplateDetector;
import fr.sii.ogham.template.thymeleaf.adapter.ClassPathResolverAdapter;
import fr.sii.ogham.template.thymeleaf.adapter.FileResolverAdapter;
import fr.sii.ogham.template.thymeleaf.adapter.FirstSupportingResolverAdapter;
import fr.sii.ogham.template.thymeleaf.adapter.StringResolverAdapter;
import fr.sii.ogham.template.thymeleaf.adapter.TemplateResolverAdapter;

public class AbstractThymeleafBuilder<MYSELF extends AbstractThymeleafBuilder<MYSELF, P>, P> extends AbstractParent<P> implements PrefixSuffixBuilder<MYSELF>, DetectorBuilder<MYSELF>, ResourceResolutionBuilder<MYSELF>, Builder<TemplateParser> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractThymeleafBuilder.class);
	
	protected MYSELF myself;
	protected final EnvironmentBuilder<?> environmentBuilder;
	private TemplateEngineDetector detector;
	private List<String> prefixes;
	private List<String> suffixes;
	private ResourceResolutionBuilderHelper<MYSELF> resourceResolutionBuilderHelper;
	private TemplateEngine engine;
	private ThymeleafContextConverter contextConverter;
	private ThymeleafEngineConfigBuilder<MYSELF> engineBuilder;
	private List<TemplateResolverAdapter> customAdapters;

	@SuppressWarnings("unchecked")
	protected AbstractThymeleafBuilder(Class<?> selfType, P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		myself = (MYSELF) selfType.cast(this);
		this.environmentBuilder = environmentBuilder;
		resourceResolutionBuilderHelper = new ResourceResolutionBuilderHelper<>(myself);
		customAdapters = new ArrayList<>();
	}

	public AbstractThymeleafBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		this(AbstractThymeleafBuilder.class, parent, environmentBuilder);
	}
	
	public ThymeleafEngineConfigBuilder<MYSELF> engine() {
		if(engineBuilder==null) {
			engineBuilder = new ThymeleafEngineConfigBuilder<>(myself);
		}
		return engineBuilder;
	}
	
	public MYSELF engine(TemplateEngine engine) {
		this.engine = engine;
		return myself;
	}
	
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
	public MYSELF pathPrefix(String... prefixes) {
		this.prefixes = new ArrayList<>(Arrays.asList(prefixes));
		return myself;
	}

	@Override
	public MYSELF pathSuffix(String... suffixes) {
		this.suffixes = new ArrayList<>(Arrays.asList(suffixes));
		return myself;
	}

	@Override
	public MYSELF classpath(String... prefixes) {
		return resourceResolutionBuilderHelper.classpath(prefixes);
	}

	@Override
	public MYSELF file(String... prefixes) {
		return resourceResolutionBuilderHelper.file(prefixes);
	}

	@Override
	public MYSELF string(String... prefixes) {
		return resourceResolutionBuilderHelper.string(prefixes);
	}

	@Override
	public MYSELF resolver(ResourceResolver resolver) {
		return resourceResolutionBuilderHelper.resolver(resolver);
	}

	public MYSELF resolverAdapter(TemplateResolverAdapter adapter) {
		customAdapters.add(adapter);
		return myself;
	}

	@Override
	public TemplateParser build() throws BuildException {
		return new ThymeleafParser(buildEngine(), buildContext());
	}
	
	@Override
	public TemplateEngineDetector buildDetector() {
		return detector==null ? new ThymeleafTemplateDetector(buildResolver()) : detector;
	}
	
	
	protected TemplateEngine buildEngine() {
		TemplateEngine engine;
		if(this.engine!=null) {
			engine = this.engine;
		} else if(engineBuilder!=null) {
			engine = engineBuilder.build();
		} else {
			engine = new TemplateEngine();
		}
		engine.addTemplateResolver(new ThymeLeafFirstSupportingTemplateResolver(buildResolver(), buildAdapters()));
		return engine;
	}

	protected ThymeleafContextConverter buildContext() {
		return contextConverter==null ? new SimpleThymeleafContextConverter() : contextConverter;
	}

	public FirstSupportingResourceResolver buildResolver() {
		List<ResourceResolver> builtResolvers = buildResolvers();
		return new FirstSupportingResourceResolver(builtResolvers);
	}

	private List<ResourceResolver> buildResolvers() {
		List<ResourceResolver> resolvers = resourceResolutionBuilderHelper.buildResolvers();
		List<ResourceResolver> builtResolvers = new ArrayList<>();
		if (getValue(prefixes).isEmpty() && getValue(suffixes).isEmpty()) {
			builtResolvers.addAll(resolvers);
		} else {
			LOG.debug("Using parentPath {} and extension {} for resource resolution", getValue(prefixes), getValue(suffixes));
			for (ResourceResolver resolver : resolvers) {
				if (resolver instanceof RelativisableResourceResolver) {
					builtResolvers.add(new RelativeResolver((RelativisableResourceResolver) resolver, getValue(prefixes), getValue(suffixes)));
				} else {
					builtResolvers.add(resolver);
				}
			}
		}
		return builtResolvers;
	}

	protected String getValue(List<String> props) {
		if(props==null) {
			return "";
		}
		PropertyResolver propertyResolver = environmentBuilder.build();
		String value = BuilderUtils.evaluate(props, propertyResolver, String.class);
		return value==null ? "" : value;
	}

	protected FirstSupportingResolverAdapter buildAdapters() {
		FirstSupportingResolverAdapter adapter = new FirstSupportingResolverAdapter();
		for(TemplateResolverAdapter custom : customAdapters) {
			adapter.addAdapter(custom);
		}
		adapter.addAdapter(new ClassPathResolverAdapter());
		adapter.addAdapter(new FileResolverAdapter());
		adapter.addAdapter(new StringResolverAdapter());
		adapter.setOptions(new TemplateResolverOptions());
		return adapter;
	}
}
