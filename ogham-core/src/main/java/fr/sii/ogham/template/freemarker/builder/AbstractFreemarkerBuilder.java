package fr.sii.ogham.template.freemarker.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import fr.sii.ogham.template.freemarker.FreeMarkerFirstSupportingTemplateLoader;
import fr.sii.ogham.template.freemarker.FreeMarkerParser;
import fr.sii.ogham.template.freemarker.TemplateLoaderOptions;
import fr.sii.ogham.template.freemarker.adapter.ClassPathResolverAdapter;
import fr.sii.ogham.template.freemarker.adapter.FileResolverAdapter;
import fr.sii.ogham.template.freemarker.adapter.FirstSupportingResolverAdapter;
import fr.sii.ogham.template.freemarker.adapter.StringResolverAdapter;
import fr.sii.ogham.template.freemarker.adapter.TemplateLoaderAdapter;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class AbstractFreemarkerBuilder<MYSELF extends AbstractFreemarkerBuilder<MYSELF, P>, P> extends AbstractParent<P> implements PrefixSuffixBuilder<MYSELF>, DetectorBuilder<MYSELF>, ResourceResolutionBuilder<MYSELF>, Builder<TemplateParser> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractFreemarkerBuilder.class);
	
	protected MYSELF myself;
	protected final EnvironmentBuilder<?> environmentBuilder;
	private TemplateEngineDetector detector;
	private ResourceResolutionBuilderHelper<MYSELF> resourceResolutionBuilderHelper;
	private List<String> prefixes;
	private List<String> suffixes;
	private Configuration configuration;
	private List<TemplateLoaderAdapter> customAdapters;
	private FreemarkerConfigurationBuilder<MYSELF> configurationBuilder;

	@SuppressWarnings("unchecked")
	protected AbstractFreemarkerBuilder(Class<?> selfType, P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		myself = (MYSELF) selfType.cast(this);
		this.environmentBuilder = environmentBuilder;
		resourceResolutionBuilderHelper = new ResourceResolutionBuilderHelper<>(myself);
		customAdapters = new ArrayList<>();
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

	public MYSELF resolverAdapter(TemplateLoaderAdapter adapter) {
		customAdapters.add(adapter);
		return myself;
	}

	public FreemarkerConfigurationBuilder<MYSELF> configuration() {
		if(configurationBuilder==null) {
			configurationBuilder = new FreemarkerConfigurationBuilder<MYSELF>(myself, environmentBuilder);
		}
		return configurationBuilder;
	}

	public MYSELF configuration(Configuration configuration) {
		this.configuration = configuration;
		return myself;
	}

	@Override
	public TemplateParser build() throws BuildException {
		return new FreeMarkerParser(buildConfiguration());
	}

	@Override
	public TemplateEngineDetector buildDetector() {
		return detector;
	}

	private Configuration buildConfiguration() {
		Configuration configuration;
		if(this.configuration!=null) {
			configuration = this.configuration;
		} else if(configurationBuilder!=null) {
			configuration = configurationBuilder.build();
		} else {
			configuration = new Configuration();
			configuration.setDefaultEncoding("UTF-8");
			configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		}
		configuration.setTemplateLoader(new FreeMarkerFirstSupportingTemplateLoader(buildResolver(), buildAdapters()));
		return configuration;
	}

	protected FirstSupportingResourceResolver buildResolver() {
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
		return new FirstSupportingResourceResolver(builtResolvers);
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
		for(TemplateLoaderAdapter custom : customAdapters) {
			adapter.addAdapter(custom);
		}
		adapter.addAdapter(new ClassPathResolverAdapter());
		adapter.addAdapter(new FileResolverAdapter());
		adapter.addAdapter(new StringResolverAdapter());
		adapter.setOptions(new TemplateLoaderOptions());
		return adapter;
	}
}
