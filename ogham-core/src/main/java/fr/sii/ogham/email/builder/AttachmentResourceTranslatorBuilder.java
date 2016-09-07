package fr.sii.ogham.email.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.FirstSupportingResourceResolverBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.translator.resource.AttachmentResourceTranslator;
import fr.sii.ogham.core.translator.resource.EveryResourceTranslator;
import fr.sii.ogham.core.translator.resource.LookupResourceTranslator;

/**
 * <p>
 * Builder for constructing a chained translator. Each translator is able to
 * handle a kind of resource and to transform it into another resource.
 * </p>
 * <p>
 * This builder simplifies the definition of the translators to use. Each
 * defined translator will be applied on each resource.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class AttachmentResourceTranslatorBuilder implements Builder<AttachmentResourceTranslator> {
	private static final Logger LOG = LoggerFactory.getLogger(AttachmentResourceTranslatorBuilder.class);

	/**
	 * A simple translator that delegates the translation to all of the provided
	 * implementations.
	 */
	private EveryResourceTranslator translator;

	/**
	 * The map for managing lookup part in resource path
	 */
	private FirstSupportingResourceResolverBuilder resolverBuilder;

	public AttachmentResourceTranslatorBuilder() {
		super();
		translator = new EveryResourceTranslator();
	}

	/**
	 * Generate a chain translator that delegates translation of resource to all
	 * enabled translators.
	 * 
	 * @return the chain translator
	 * @throws BuildException
	 *             when the translator couldn't be generated
	 */
	@Override
	public AttachmentResourceTranslator build() throws BuildException {
		LOG.info("Using translator that calls all registered translators");
		if (resolverBuilder != null) {
			translator.addTranslator(new LookupResourceTranslator(resolverBuilder.build()));
		}
		LOG.debug("Registered translators: {}", translator.getTranslators());
		return translator;
	}

	/**
	 * Tells the builder to use all default behaviors and values.
	 * <p>
	 * It will register a {@link LookupResourceTranslator} for searching
	 * attachments using a lookup.
	 * </p>
	 * It will enable default lookups (see {@link #useDefaultLookups()}):
	 * <ul>
	 * <li>"classpath" for searching attachment into the classpath</li>
	 * <li>"file" for searching attachment on the file system</li>
	 * <li>"" when no lookup is defined. It searches in the classpath too</li>
	 * </ul>
	 * 
	 * @return this builder instance for fluent use
	 */
	public AttachmentResourceTranslatorBuilder useDefaults() {
		useDefaultLookups();
		return this;
	}

	/**
	 * Tells the builder to use the default lookup mapping:
	 * <ul>
	 * <li>"classpath" for searching attachment into the classpath</li>
	 * <li>"file" for searching attachment on the file system</li>
	 * <li>"" when no lookup is defined. It searches in the classpath too</li>
	 * </ul>
	 * 
	 * @return this instance for fluent use
	 */
	public AttachmentResourceTranslatorBuilder useDefaultLookups() {
		resolverBuilder = new FirstSupportingResourceResolverBuilder().useDefaults();
		return this;
	}

	/**
	 * Register a new translator. The translator is added at the end.
	 * 
	 * @param translator
	 *            the translator to register
	 * @return this instance for fluent use
	 */
	public AttachmentResourceTranslatorBuilder withTranslator(AttachmentResourceTranslator translator) {
		this.translator.addTranslator(translator);
		return this;
	}

	/**
	 * Register a lookup mapping. The key is the lookup. The value is the
	 * resource resolver.
	 * 
	 * @param resource
	 *            the attachment resource resolver
	 * @return this instance for fluent use
	 */
	public AttachmentResourceTranslatorBuilder withResourceResolver(ResourceResolver resource) {
		if (resolverBuilder == null) {
			resolverBuilder = new FirstSupportingResourceResolverBuilder();
		}
		resolverBuilder.withResourceResolver(resource);
		return this;
	}

	/**
	 * <p>
	 * Get the builder used to handle resource resolution.
	 * </p>
	 * 
	 * Access this builder if you want to:
	 * <ul>
	 * <li>Customize how attached resources are resolved</li>
	 * <li>Register a custom lookup mapping resolver for attached resources</li>
	 * </ul>
	 * 
	 * @return the builder used to handle resource resolution
	 */
	public FirstSupportingResourceResolverBuilder getResolverBuilder() {
		return resolverBuilder;
	}
}
