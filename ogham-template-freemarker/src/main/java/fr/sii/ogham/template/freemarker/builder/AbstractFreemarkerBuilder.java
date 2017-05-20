package fr.sii.ogham.template.freemarker.builder;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.AbstractParent;
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
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;
import fr.sii.ogham.core.template.parser.TemplateParser;
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

public class AbstractFreemarkerBuilder<MYSELF extends AbstractFreemarkerBuilder<MYSELF, P>, P> extends AbstractParent<P> implements DetectorBuilder<MYSELF>, ResourceResolutionBuilder<MYSELF>, Builder<TemplateParser> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractFreemarkerBuilder.class);
	
	protected MYSELF myself;
	protected EnvironmentBuilder<MYSELF> environmentBuilder;
	private TemplateEngineDetector detector;
	private ResourceResolutionBuilderHelper<MYSELF> resourceResolutionBuilderHelper;
	private Configuration configuration;
	private List<TemplateLoaderAdapter> customAdapters;
	private FreemarkerConfigurationBuilder<MYSELF> configurationBuilder;

	protected AbstractFreemarkerBuilder(Class<?> selfType) {
		this(selfType, null, null);
	}

	@SuppressWarnings("unchecked")
	protected AbstractFreemarkerBuilder(Class<?> selfType, P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		myself = (MYSELF) selfType.cast(this);
		if(environmentBuilder!=null) {
			environment(environmentBuilder);
		}
		customAdapters = new ArrayList<>();
	}

	public EnvironmentBuilder<MYSELF> environment() {
		if(environmentBuilder==null) {
			environmentBuilder = new SimpleEnvironmentBuilder<>(myself);
		}
		return environmentBuilder;
	}
	
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

	public MYSELF resolverAdapter(TemplateLoaderAdapter adapter) {
		customAdapters.add(adapter);
		return myself;
	}

	public FreemarkerConfigurationBuilder<MYSELF> configuration() {
		if(configurationBuilder==null) {
			configurationBuilder = new FreemarkerConfigurationBuilder<>(myself, environmentBuilder);
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

	public FirstSupportingResourceResolver buildResolver() {
		return new FirstSupportingResourceResolver(buildResolvers());
	}

	protected List<ResourceResolver> buildResolvers() {
		return resourceResolutionBuilderHelper.buildResolvers();
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

	private void initResolutionBuilder() {
		if(resourceResolutionBuilderHelper==null) {
			resourceResolutionBuilderHelper = new ResourceResolutionBuilderHelper<>(myself, environmentBuilder);
		}
	}
}
