package fr.sii.notification.email.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.core.builder.Builder;
import fr.sii.notification.core.builder.LookupMappingResourceResolverBuilder;
import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.core.resource.resolver.ResourceResolver;
import fr.sii.notification.core.translator.resource.AttachmentResourceTranslator;
import fr.sii.notification.core.translator.resource.EveryResourceTranslator;
import fr.sii.notification.core.translator.resource.LookupResourceTranslator;

/**
 * Builder for constructing a chained translator. Each translator is able to
 * handle a kind of resource and to transform it into another resource.
 * 
 * This builder simplifies the definition of the translators to use. Each
 * defined translator will be applied on each resource.
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
	private LookupMappingResourceResolverBuilder resolverBuilder;

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
		if(resolverBuilder!=null) {
			translator.addTranslator(new LookupResourceTranslator(resolverBuilder.build()));
		}
		LOG.debug("Registered translators: {}", translator.getTranslators());
		return translator;
	}

	/**
	 * Tells the builder to use all default behaviors and values.
	 * <p>
	 * It will register a {@link LookupResourceTranslator} for searching
	 * attachments using a prefix lookup.
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
		resolverBuilder = new LookupMappingResourceResolverBuilder().useDefaults();
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
	 * Register a lookup mapping. The key is the lookup prefix. The value is the
	 * resource resolver.
	 * 
	 * @param lookup
	 *            the lookup prefix
	 * @param resource
	 *            the attachment resource resolver
	 * @return this instance for fluent use
	 */
	public AttachmentResourceTranslatorBuilder withLookupResolver(String lookup, ResourceResolver resource) {
		if(resolverBuilder==null) {
			resolverBuilder = new LookupMappingResourceResolverBuilder();
		}
		resolverBuilder.withLookupResolver(lookup, resource);
		return this;
	}
}
