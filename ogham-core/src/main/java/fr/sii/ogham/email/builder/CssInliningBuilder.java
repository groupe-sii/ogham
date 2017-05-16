package fr.sii.ogham.email.builder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.resolution.ClassPathResolutionBuilder;
import fr.sii.ogham.core.builder.resolution.FileResolutionBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilderHelper;
import fr.sii.ogham.core.builder.resolution.StringResolutionBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.translator.content.ContentTranslator;
import fr.sii.ogham.html.inliner.CssInliner;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupCssInliner;
import fr.sii.ogham.html.translator.InlineCssTranslator;

public class CssInliningBuilder extends AbstractParent<CssHandlingBuilder> implements ResourceResolutionBuilder<CssInliningBuilder>, Builder<ContentTranslator> {
	private static final Logger LOG = LoggerFactory.getLogger(CssInliningBuilder.class);
	
	private ResourceResolutionBuilderHelper<CssInliningBuilder> resourceResolutionBuilderHelper;
	private boolean useJsoup;

	public CssInliningBuilder(CssHandlingBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		resourceResolutionBuilderHelper = new ResourceResolutionBuilderHelper<>(this, environmentBuilder);
	}

	public CssInliningBuilder jsoup() {
		useJsoup = true;
		return this;
	}

	@Override
	public ClassPathResolutionBuilder<CssInliningBuilder> classpath() {
		return resourceResolutionBuilderHelper.classpath();
	}

	@Override
	public FileResolutionBuilder<CssInliningBuilder> file() {
		return resourceResolutionBuilderHelper.file();
	}

	@Override
	public StringResolutionBuilder<CssInliningBuilder> string() {
		return resourceResolutionBuilderHelper.string();
	}

	@Override
	public CssInliningBuilder resolver(ResourceResolver resolver) {
		return resourceResolutionBuilderHelper.resolver(resolver);
	}


	@Override
	public ContentTranslator build() throws BuildException {
		ResourceResolver resourceResolver = buildResolver();
		CssInliner cssInliner = buildInliner();
		if(cssInliner==null) {
			// TODO: log to indicate why no translator
			return null;
		}
		return new InlineCssTranslator(cssInliner, resourceResolver);
	}

	private CssInliner buildInliner() {
		if(useJsoup) {
			return new JsoupCssInliner();
		}
		LOG.debug("No CSS inliner implementation configured");
		return null;
	}

	private ResourceResolver buildResolver() {
		List<ResourceResolver> resolvers = resourceResolutionBuilderHelper.buildResolvers();
		return new FirstSupportingResourceResolver(resolvers);
	}

}
