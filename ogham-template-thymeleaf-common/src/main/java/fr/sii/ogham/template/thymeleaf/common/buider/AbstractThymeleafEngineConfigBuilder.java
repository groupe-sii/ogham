package fr.sii.ogham.template.thymeleaf.common.buider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;

public abstract class AbstractThymeleafEngineConfigBuilder<MYSELF extends AbstractThymeleafEngineConfigBuilder<MYSELF, P>, P> extends AbstractParent<P> implements Builder<TemplateEngine> {
	protected final MYSELF myself;
	protected Set<IDialect> dialects;
	protected Map<String, IDialect> dialectsByPrefix;
	protected Set<ITemplateResolver> templateResolvers;
	protected ICacheManager cacheManager;
	protected Set<IMessageResolver> messageResolvers;

	@SuppressWarnings("unchecked")
	public AbstractThymeleafEngineConfigBuilder(Class<?> selfType, P parent) {
		super(parent);
		myself = (MYSELF) selfType.cast(this);
	}
	

	/**
	 * <p>
	 * Sets a new unique dialect for this template engine.
	 * </p>
	 * <p>
	 * This operation is equivalent to removing all the currently configured
	 * dialects and then adding this one.
	 * </p>
	 * <p>
	 * This operation can only be executed before processing templates for the
	 * first time. Once a template is processed, the template engine is
	 * considered to be <i>initialized</i>, and from then on any attempt to
	 * change its configuration will result in an exception.
	 * </p>
	 * 
	 * @param dialect
	 *            the new unique {@link IDialect} to be used.
	 * @return this for fluent use
	 */
	public MYSELF setDialect(final IDialect dialect) {
		dialects().clear();
		dialects().add(dialect);
		return myself;
	}

	/**
	 * <p>
	 * Adds a new dialect for this template engine, using the specified prefix.
	 * </p>
	 * <p>
	 * This dialect will be added to the set of currently configured ones.
	 * </p>
	 * <p>
	 * This operation can only be executed before processing templates for the
	 * first time. Once a template is processed, the template engine is
	 * considered to be <i>initialized</i>, and from then on any attempt to
	 * change its configuration will result in an exception.
	 * </p>
	 * 
	 * @param prefix
	 *            the prefix that will be used for this dialect
	 * @param dialect
	 *            the new {@link IDialect} to be added to the existing ones.
	 * @return this for fluent use
	 */
	public MYSELF addDialect(final String prefix, final IDialect dialect) {
		this.dialectsByPrefix().put(prefix, dialect);
		return myself;
	}

	/**
	 * <p>
	 * Adds a new dialect for this template engine, using the dialect's
	 * specified default dialect.
	 * </p>
	 * <p>
	 * This dialect will be added to the set of currently configured ones.
	 * </p>
	 * <p>
	 * This operation can only be executed before processing templates for the
	 * first time. Once a template is processed, the template engine is
	 * considered to be <i>initialized</i>, and from then on any attempt to
	 * change its configuration will result in an exception.
	 * </p>
	 * 
	 * @param dialect
	 *            the new {@link IDialect} to be added to the existing ones.
	 * @return this for fluent use
	 */
	public MYSELF addDialect(final IDialect dialect) {
		dialects().add(dialect);
		return myself;
	}

	/**
	 * <p>
	 * Sets a new set of dialects for this template engine, referenced by the
	 * prefixes they will be using.
	 * </p>
	 * <p>
	 * This operation can only be executed before processing templates for the
	 * first time. Once a template is processed, the template engine is
	 * considered to be <i>initialized</i>, and from then on any attempt to
	 * change its configuration will result in an exception.
	 * </p>
	 * 
	 * @param dialects
	 *            the new map of {@link IDialect} objects to be used, referenced
	 *            by their prefixes.
	 * @return this for fluent use
	 */
	public MYSELF setDialectsByPrefix(final Map<String, IDialect> dialects) {
		dialectsByPrefix().clear();
		dialectsByPrefix().putAll(dialects);
		return myself;
	}

	/**
	 * <p>
	 * Sets a new set of dialects for this template engine, all of them using
	 * their default prefixes.
	 * </p>
	 * <p>
	 * This operation can only be executed before processing templates for the
	 * first time. Once a template is processed, the template engine is
	 * considered to be <i>initialized</i>, and from then on any attempt to
	 * change its configuration will result in an exception.
	 * </p>
	 * 
	 * @param dialects
	 *            the new set of {@link IDialect} objects to be used.
	 * @return this for fluent use
	 */
	public MYSELF setDialects(final Set<IDialect> dialects) {
		this.dialects().clear();
		this.dialects().addAll(dialects);
		return myself;
	}

	/**
	 * <p>
	 * Sets an additional set of dialects for this template engine, all of them
	 * using their default prefixes.
	 * </p>
	 * <p>
	 * This operation can only be executed before processing templates for the
	 * first time. Once a template is processed, the template engine is
	 * considered to be <i>initialized</i>, and from then on any attempt to
	 * change its configuration will result in an exception.
	 * </p>
	 * 
	 * @param additionalDialects
	 *            the new set of {@link IDialect} objects to be used.
	 * 
	 * @since 2.0.9
	 * @return this for fluent use
	 */
	public MYSELF setAdditionalDialects(final Set<IDialect> additionalDialects) {
		dialects().addAll(additionalDialects);
		return myself;
	}

	/**
	 * <p>
	 * Removes all the currently configured dialects.
	 * </p>
	 * <p>
	 * This operation can only be executed before processing templates for the
	 * first time. Once a template is processed, the template engine is
	 * considered to be <i>initialized</i>, and from then on any attempt to
	 * change its configuration will result in an exception.
	 * </p>
	 * 
	 * @return this for fluent use
	 */
	public MYSELF clearDialects() {
		dialects().clear();
		return myself;
	}

	/**
	 * <p>
	 * Sets the entire set of template resolvers.
	 * </p>
	 * 
	 * @param templateResolvers
	 *            the new template resolvers.
	 * @return this for fluent use
	 */
	public MYSELF setTemplateResolvers(final Set<? extends ITemplateResolver> templateResolvers) {
		this.templateResolvers().clear();
		this.templateResolvers().addAll(templateResolvers);
		return myself;
	}

	/**
	 * <p>
	 * Adds a new template resolver to the current set.
	 * </p>
	 * 
	 * @param templateResolver
	 *            the new template resolver.
	 * @return this for fluent use
	 */
	public MYSELF addTemplateResolver(final ITemplateResolver templateResolver) {
		templateResolvers().add(templateResolver);
		return myself;
	}

	/**
	 * <p>
	 * Sets a single template resolver for this template engine.
	 * </p>
	 * <p>
	 * Calling this method is equivalent to calling
	 * {@link #setTemplateResolvers(Set)} passing a Set with only one template
	 * resolver.
	 * </p>
	 * 
	 * @param templateResolver
	 *            the template resolver to be set.
	 * @return this for fluent use
	 */
	public MYSELF setTemplateResolver(final ITemplateResolver templateResolver) {
		templateResolvers().clear();
		templateResolvers().add(templateResolver);
		return myself;
	}

	/**
	 * <p>
	 * Sets the Cache Manager to be used. If set to null, no caches will be used
	 * throughout the engine.
	 * </p>
	 * <p>
	 * By default, an instance of
	 * {@link org.thymeleaf.cache.StandardCacheManager} is set.
	 * </p>
	 * <p>
	 * This operation can only be executed before processing templates for the
	 * first time. Once a template is processed, the template engine is
	 * considered to be <i>initialized</i>, and from then on any attempt to
	 * change its configuration will result in an exception.
	 * </p>
	 * 
	 * @param cacheManager
	 *            the cache manager to be set.
	 * @return this for fluent use
	 * 
	 */
	public MYSELF setCacheManager(final ICacheManager cacheManager) {
		this.cacheManager = cacheManager;
		return myself;
	}

	/**
	 * <p>
	 * Sets the message resolvers to be used by this template engine.
	 * </p>
	 * <p>
	 * This operation can only be executed before processing templates for the
	 * first time. Once a template is processed, the template engine is
	 * considered to be <i>initialized</i>, and from then on any attempt to
	 * change its configuration will result in an exception.
	 * </p>
	 * 
	 * @param messageResolvers
	 *            the Set of template resolvers.
	 * @return this for fluent use
	 */
	public MYSELF setMessageResolvers(final Set<? extends IMessageResolver> messageResolvers) {
		this.messageResolvers().clear();
		this.messageResolvers().addAll(messageResolvers);
		return myself;
	}

	/**
	 * <p>
	 * Adds a message resolver to the set of message resolvers to be used by the
	 * template engine.
	 * </p>
	 * <p>
	 * This operation can only be executed before processing templates for the
	 * first time. Once a template is processed, the template engine is
	 * considered to be <i>initialized</i>, and from then on any attempt to
	 * change its configuration will result in an exception.
	 * </p>
	 * 
	 * @param messageResolver
	 *            the new message resolver to be added.
	 * @return this for fluent use
	 */
	public MYSELF addMessageResolver(final IMessageResolver messageResolver) {
		messageResolvers().add(messageResolver);
		return myself;
	}

	/**
	 * <p>
	 * Sets a single message resolver for this template engine.
	 * </p>
	 * <p>
	 * Calling this method is equivalent to calling
	 * {@link #setMessageResolvers(Set)} passing a Set with only one message
	 * resolver.
	 * </p>
	 * <p>
	 * This operation can only be executed before processing templates for the
	 * first time. Once a template is processed, the template engine is
	 * considered to be <i>initialized</i>, and from then on any attempt to
	 * change its configuration will result in an exception.
	 * </p>
	 * 
	 * @param messageResolver
	 *            the message resolver to be set.
	 * @return this for fluent use
	 */
	public MYSELF setMessageResolver(final IMessageResolver messageResolver) {
		messageResolvers().clear();
		messageResolvers().add(messageResolver);
		return myself;
	}

	
	@Override
	public TemplateEngine build() {
		TemplateEngine engine = new TemplateEngine();
		configureDialects(engine);
		configureMessageResolvers(engine);
		configureTemplateResolvers(engine);
		configureCacheManager(engine);
		return engine;
	}

	protected void configureTemplateResolvers(TemplateEngine engine) {
		if (templateResolvers != null) {
			engine.setTemplateResolvers(templateResolvers);
		}
	}

	protected void configureMessageResolvers(TemplateEngine engine) {
		if (messageResolvers != null) {
			engine.setMessageResolvers(messageResolvers);
		}
	}

	protected void configureCacheManager(TemplateEngine engine) {
		if (cacheManager != null) {
			engine.setCacheManager(cacheManager);
		}
	}

	protected void configureDialects(TemplateEngine engine) {
		if (dialects != null) {
			engine.setDialects(dialects);
		}
		if (dialectsByPrefix != null) {
			engine.setDialectsByPrefix(dialectsByPrefix);
		}
	}

	protected Set<IDialect> dialects() {
		if (dialects == null) {
			dialects = new HashSet<>();
		}
		return dialects;
	}

	protected Map<String, IDialect> dialectsByPrefix() {
		if (dialectsByPrefix == null) {
			dialectsByPrefix = new HashMap<>();
		}
		return dialectsByPrefix;
	}

	protected Set<IMessageResolver> messageResolvers() {
		if (messageResolvers == null) {
			messageResolvers = new HashSet<>();
		}
		return messageResolvers;
	}

	protected Set<ITemplateResolver> templateResolvers() {
		if (templateResolvers == null) {
			templateResolvers = new HashSet<>();
		}
		return templateResolvers;
	}
}
