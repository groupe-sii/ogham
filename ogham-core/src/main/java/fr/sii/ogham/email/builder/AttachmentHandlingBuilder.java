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
import fr.sii.ogham.core.translator.resource.AttachmentResourceTranslator;
import fr.sii.ogham.core.translator.resource.EveryResourceTranslator;
import fr.sii.ogham.core.translator.resource.LookupResourceTranslator;

public class AttachmentHandlingBuilder extends AbstractParent<EmailBuilder> implements ResourceResolutionBuilder<AttachmentHandlingBuilder>, Builder<AttachmentResourceTranslator> {
	private static final Logger LOG = LoggerFactory.getLogger(AttachmentHandlingBuilder.class);
	
	private ResourceResolutionBuilderHelper<AttachmentHandlingBuilder> resourceResolutionBuilderHelper;

	public AttachmentHandlingBuilder(EmailBuilder parent, EnvironmentBuilder<?> environmentBuider) {
		super(parent);
		resourceResolutionBuilderHelper = new ResourceResolutionBuilderHelper<>(this, environmentBuider);
	}

	@Override
	public ClassPathResolutionBuilder<AttachmentHandlingBuilder> classpath() {
		return resourceResolutionBuilderHelper.classpath();
	}

	@Override
	public FileResolutionBuilder<AttachmentHandlingBuilder> file() {
		return resourceResolutionBuilderHelper.file();
	}

	@Override
	public StringResolutionBuilder<AttachmentHandlingBuilder> string() {
		return resourceResolutionBuilderHelper.string();
	}

	@Override
	public AttachmentHandlingBuilder resolver(ResourceResolver resolver) {
		return resourceResolutionBuilderHelper.resolver(resolver);
	}

	@Override
	public AttachmentResourceTranslator build() throws BuildException {
		EveryResourceTranslator translator = new EveryResourceTranslator();
		LOG.info("Using translator that calls all registered translators");
		translator.addTranslator(new LookupResourceTranslator(buildResolver()));
		LOG.debug("Registered translators: {}", translator.getTranslators());
		return translator;
	}

	private ResourceResolver buildResolver() {
		List<ResourceResolver> resolvers = resourceResolutionBuilderHelper.buildResolvers();
		return new FirstSupportingResourceResolver(resolvers);
	}
}
