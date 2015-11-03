package fr.sii.ogham.email.builder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilderHelper;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.translator.resource.AttachmentResourceTranslator;
import fr.sii.ogham.core.translator.resource.EveryResourceTranslator;
import fr.sii.ogham.core.translator.resource.LookupResourceTranslator;

public class AttachmentHandlingBuilder extends AbstractParent<EmailBuilder> implements ResourceResolutionBuilder<AttachmentHandlingBuilder>, Builder<AttachmentResourceTranslator> {
	private static final Logger LOG = LoggerFactory.getLogger(AttachmentHandlingBuilder.class);
	
	private ResourceResolutionBuilderHelper<AttachmentHandlingBuilder> resourceResolutionBuilderHelper;

	public AttachmentHandlingBuilder(EmailBuilder parent) {
		super(parent);
		resourceResolutionBuilderHelper = new ResourceResolutionBuilderHelper<>(this);
	}

	@Override
	public AttachmentHandlingBuilder classpath(String... prefixes) {
		return resourceResolutionBuilderHelper.classpath(prefixes);
	}

	@Override
	public AttachmentHandlingBuilder file(String... prefixes) {
		return resourceResolutionBuilderHelper.file(prefixes);
	}

	@Override
	public AttachmentHandlingBuilder string(String... prefixes) {
		return resourceResolutionBuilderHelper.string(prefixes);
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
		// TODO: allow to use prefixes too
//		List<ResourceResolver> builtResolvers = new ArrayList<>();
//		if (!getValue(prefixes).isEmpty() || !getValue(suffixes).isEmpty()) {
//			LOG.debug("Using parentPath {} and extension {} for resource resolution", getValue(prefixes), getValue(suffixes));
//			for (ResourceResolver resolver : resolvers) {
//				if (resolver instanceof RelativisableResourceResolver) {
//					builtResolvers.add(new RelativeResolver((RelativisableResourceResolver) resolver, getValue(prefixes), getValue(suffixes)));
//				} else {
//					builtResolvers.add(resolver);
//				}
//			}
//		}
		return new FirstSupportingResourceResolver(resolvers);
	}
}
