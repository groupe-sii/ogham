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
import fr.sii.ogham.core.template.detector.FixedEngineDetector;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;
import fr.sii.ogham.core.template.parser.AutoDetectTemplateParser;
import fr.sii.ogham.core.template.parser.AutoDetectTemplateParser.TemplateImplementation;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.common.adapter.FailIfNotFoundVariantResolver;
import fr.sii.ogham.template.common.adapter.FirstExistingResourceVariantResolver;
import fr.sii.ogham.template.common.adapter.NullVariantResolver;
import fr.sii.ogham.template.common.adapter.VariantResolver;

public class TemplateBuilderHelper<P> {
	private static final Logger LOG = LoggerFactory.getLogger(TemplateBuilderHelper.class);
	
	private final P parent;
	private final List<Builder<? extends TemplateParser>> templateBuilders;
	private final EnvironmentBuilder<?> environmentBuilder;
	private boolean missingVariantFail;
	private VariantResolver missingResolver;
	
	public TemplateBuilderHelper(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super();
		this.parent = parent;
		this.environmentBuilder = environmentBuilder;
		templateBuilders = new ArrayList<>();
	}
	
	public boolean hasRegisteredTemplates() {
		return !templateBuilders.isEmpty();
	}
	
	public void missingVariant(boolean fail) {
		this.missingVariantFail = fail;
	}

	public void missingVariant(VariantResolver resolver) {
		this.missingResolver = resolver;
	}

	public <T extends Builder<? extends TemplateParser>> T register(Class<T> builderClass) {
		// if already registered => provide same instance
		for(Builder<? extends TemplateParser> builder : templateBuilders) {
			if(builderClass.isAssignableFrom(builder.getClass())) {
				return (T) builder;
			}
		}
		// create the builder instance
		try {
			T builder;
			Constructor<T> constructor = builderClass.getConstructor(parent.getClass(), EnvironmentBuilder.class);
			if(constructor!=null) {
				builder = constructor.newInstance(parent, environmentBuilder);
			} else {
				builder = builderClass.newInstance();
			}
			templateBuilders.add(builder);
			return builder;
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			throw new BuildException("Can't instantiate builder from class "+builderClass.getSimpleName(), e);
		}
	}

	public TemplateParser buildTemplateParser() throws BuildException {
		// TODO: handle enable
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


	public VariantResolver buildVariant() {
		FirstExistingResourceVariantResolver variantResolver = new FirstExistingResourceVariantResolver(buildDefaultVariantResolver());
		for(Builder<? extends TemplateParser> builder : templateBuilders) {
			if(builder instanceof VariantBuilder) {
				variantResolver.addVariantResolver(((VariantBuilder<?>) builder).buildVariant());
			}
		}
		return variantResolver;
	}

	private VariantResolver buildDefaultVariantResolver() {
		if(missingVariantFail) {
			return new FailIfNotFoundVariantResolver();
		}
		return missingResolver==null ? new NullVariantResolver() : missingResolver;
	}

	private List<TemplateImplementation> buildTemplateParserImpls() {
		List<TemplateImplementation> impls = new ArrayList<>();
		for(Builder<? extends TemplateParser> builder : templateBuilders) {
			TemplateEngineDetector detector;
			if(builder instanceof DetectorBuilder) {
				detector = ((DetectorBuilder<?>) builder).buildDetector();
			} else {
				detector = new FixedEngineDetector(true);
			}
			impls.add(new TemplateImplementation(detector, builder.build()));
		}
		return impls;
	}
}
