package fr.sii.ogham.template.thymeleaf.buider;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;

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

public class AbstractThymeleafBuilder<MYSELF extends AbstractThymeleafBuilder<MYSELF, P>, P> extends AbstractParent<P> implements DetectorBuilder<MYSELF>, ResourceResolutionBuilder<MYSELF>, Builder<TemplateParser> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractThymeleafBuilder.class);
	
	protected MYSELF myself;
	protected EnvironmentBuilder<MYSELF> environmentBuilder;
	private TemplateEngineDetector detector;
	private ResourceResolutionBuilderHelper<MYSELF> resourceResolutionBuilderHelper;
	private TemplateEngine engine;
	private ThymeleafContextConverter contextConverter;
	private ThymeleafEngineConfigBuilder<MYSELF> engineBuilder;
	private List<TemplateResolverAdapter> customAdapters;

	protected AbstractThymeleafBuilder(Class<?> selfType) {
		this(selfType, null, null);
	}
	
	@SuppressWarnings("unchecked")
	protected AbstractThymeleafBuilder(Class<?> selfType, P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		myself = (MYSELF) selfType.cast(this);
		if(environmentBuilder!=null) {
			environment(environmentBuilder);
		}
		customAdapters = new ArrayList<>();
	}

	public AbstractThymeleafBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		this(AbstractThymeleafBuilder.class, parent, environmentBuilder);
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
		return new FirstSupportingResourceResolver(buildResolvers());
	}

	private List<ResourceResolver> buildResolvers() {
		return resourceResolutionBuilderHelper.buildResolvers();
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


	private void initResolutionBuilder() {
		if(resourceResolutionBuilderHelper==null) {
			resourceResolutionBuilderHelper = new ResourceResolutionBuilderHelper<>(myself, environmentBuilder);
		}
	}
}
