package fr.sii.ogham.core.builder.template;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.template.parser.AutoDetectTemplateParser;
import fr.sii.ogham.core.template.parser.AutoDetectTemplateParser.TemplateImplementation;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.freemarker.builder.AbstractFreemarkerBuilder;
import fr.sii.ogham.template.thymeleaf.buider.AbstractThymeleafBuilder;

public abstract class AbstractTemplateBuilder<MYSELF extends AbstractTemplateBuilder<MYSELF, T, F, P>, T extends AbstractThymeleafBuilder<T, MYSELF>, F extends AbstractFreemarkerBuilder<F, MYSELF>, P> extends AbstractParent<P> implements Builder<TemplateParser> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractTemplateBuilder.class);
	
	protected MYSELF myself;
	protected final EnvironmentBuilder<?> environmentBuilder;
	private boolean enable;
	private T thymeleafBuilder;
	private F freemarkerBuilder;
	
	@SuppressWarnings("unchecked")
	protected AbstractTemplateBuilder(Class<?> selfType, P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		myself = (MYSELF) selfType.cast(this);
		this.environmentBuilder = environmentBuilder;
		enable = true;
	}

	public T thymeleaf() {
		if(thymeleafBuilder==null) {
			thymeleafBuilder = createThymeleafBuilder();
		}
		return thymeleafBuilder;
	}
	
	public F freemarker() {
		if(freemarkerBuilder==null) {
			freemarkerBuilder = createFreemarkerBuilder();
		}
		return freemarkerBuilder;
	}
	
//	<T> T custom(Class<T> builderClass);

	public MYSELF enable(boolean enable) {
		this.enable = enable;
		return myself;
	}

	@Override
	public TemplateParser build() throws BuildException {
		// TODO: handle enable
		List<TemplateImplementation> impls = buildImpls();
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

	private List<TemplateImplementation> buildImpls() {
		List<TemplateImplementation> impls = new ArrayList<>();
		if(thymeleafBuilder!=null) {
			impls.add(new TemplateImplementation(thymeleafBuilder.buildDetector(), thymeleafBuilder.build()));
		}
		if(freemarkerBuilder!=null) {
			impls.add(new TemplateImplementation(freemarkerBuilder.buildDetector(), freemarkerBuilder.build()));
		}
		return impls;
	}

	protected abstract T createThymeleafBuilder();

	protected abstract F createFreemarkerBuilder();
}
