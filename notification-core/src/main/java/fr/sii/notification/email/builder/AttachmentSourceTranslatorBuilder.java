package fr.sii.notification.email.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.core.builder.Builder;
import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.core.util.BuilderUtil;
import fr.sii.notification.email.attachment.resolver.ClassPathSourceResolver;
import fr.sii.notification.email.attachment.resolver.FileSourceResolver;
import fr.sii.notification.email.attachment.resolver.LookupSourceResolver;
import fr.sii.notification.email.attachment.resolver.SourceResolver;
import fr.sii.notification.email.attachment.translator.AttachmentSourceTranslator;
import fr.sii.notification.email.attachment.translator.EverySourceTranslator;
import fr.sii.notification.email.attachment.translator.LookupSourceTranslator;

/**
 * Builder for constructing a chained translator. Each translator is able to
 * handle a kind of source and to transform it into another source.
 * 
 * This builder simplifies the definition of the translators to use. Each
 * defined translator will be applied on each source.
 * 
 * @author Aurélien Baudet
 *
 */
public class AttachmentSourceTranslatorBuilder implements Builder<AttachmentSourceTranslator> {
	private static final Logger LOG = LoggerFactory.getLogger(AttachmentSourceTranslatorBuilder.class);

	/**
	 * A simple translator that delegates the translation to all of the provided
	 * implementations.
	 */
	private EverySourceTranslator translator;

	/**
	 * The map for managing lookup part in source path
	 */
	private Map<String, SourceResolver> sourceResolvers;

	public AttachmentSourceTranslatorBuilder() {
		super();
		translator = new EverySourceTranslator();
		sourceResolvers = new HashMap<>();
	}

	/**
	 * Generate a chain translator that delegates translation of source to all
	 * enabled translators.
	 * 
	 * @return the chain translator
	 * @throws BuildException
	 *             when the translator couldn't be generated
	 */
	@Override
	public AttachmentSourceTranslator build() throws BuildException {
		LOG.info("Using translator that calls all registered translators");
		LOG.debug("Registered translators: {}", translator.getTranslators());
		return translator;
	}

	/**
	 * Tells the builder to use all default behaviors and values.
	 * <p>
	 * It will register a {@link LookupSourceTranslator} for searching
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
	public AttachmentSourceTranslatorBuilder useDefaults() {
		useDefaultLookups();
		withTranslator(new LookupSourceTranslator(new LookupSourceResolver(sourceResolvers)));
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
	public AttachmentSourceTranslatorBuilder useDefaultLookups() {
		return useDefaultLookups(BuilderUtil.getDefaultProperties());
	}

	/**
	 * Tells the builder to use the default lookup mapping:
	 * <ul>
	 * <li>"classpath" for searching attachment into the classpath</li>
	 * <li>"file" for searching attachment on the file system</li>
	 * <li>"" when no lookup is defined. It searches in the classpath too</li>
	 * </ul>
	 * 
	 * @param properties
	 *            the properties to use
	 * @return this instance for fluent use
	 */
	public AttachmentSourceTranslatorBuilder useDefaultLookups(Properties properties) {
		withLookup("classpath", new ClassPathSourceResolver());
		withLookup("file", new FileSourceResolver());
		withLookup("", new ClassPathSourceResolver());
		return this;
	}

	/**
	 * Register a new translator. The translator is added at the end.
	 * 
	 * @param translator
	 *            the translator to register
	 * @return this instance for fluent use
	 */
	public AttachmentSourceTranslatorBuilder withTranslator(AttachmentSourceTranslator translator) {
		this.translator.addTranslator(translator);
		return this;
	}

	/**
	 * Register a lookup mapping. The key is the lookup prefix. The value is the
	 * source resolver.
	 * 
	 * @param lookup
	 *            the lookup prefix
	 * @param source
	 *            the attachment source resolver
	 * @return this instance for fluent use
	 */
	public AttachmentSourceTranslatorBuilder withLookup(String lookup, SourceResolver source) {
		sourceResolvers.put(lookup, source);
		return this;
	}
}
