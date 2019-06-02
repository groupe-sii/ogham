package fr.sii.ogham.template.thymeleaf.v2.buider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.templatemode.ITemplateModeHandler;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.template.thymeleaf.common.buider.ThymeleafEngineConfigBuilder;

/**
 * Fluent builder to configure Thymeleaf engine.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class ThymeleafV2EngineConfigBuilder<P> extends AbstractParent<P> implements ThymeleafEngineConfigBuilder<P> {
	private Set<IDialect> dialects;
	private Map<String, IDialect> dialectsByPrefix;
	private Set<ITemplateResolver> templateResolvers;
	private ICacheManager cacheManager;
	private Set<IMessageResolver> messageResolvers;
	private Set<IMessageResolver> defaultMessageResolvers;
	private Set<ITemplateModeHandler> templateModeHandlers;
	private Set<ITemplateModeHandler> defaultTemplateModeHandlers;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method.
	 * 
	 * @param parent
	 *            the parent builder
	 */
	public ThymeleafV2EngineConfigBuilder(P parent) {
		super(parent);
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
	public ThymeleafV2EngineConfigBuilder<P> setDialect(final IDialect dialect) {
		dialects().clear();
		dialects().add(dialect);
		return this;
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
	public ThymeleafV2EngineConfigBuilder<P> addDialect(final String prefix, final IDialect dialect) {
		this.dialectsByPrefix().put(prefix, dialect);
		return this;
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
	public ThymeleafV2EngineConfigBuilder<P> addDialect(final IDialect dialect) {
		dialects().add(dialect);
		return this;
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
	public ThymeleafV2EngineConfigBuilder<P> setDialectsByPrefix(final Map<String, IDialect> dialects) {
		dialectsByPrefix().clear();
		dialectsByPrefix().putAll(dialects);
		return this;
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
	public ThymeleafV2EngineConfigBuilder<P> setDialects(final Set<IDialect> dialects) {
		this.dialects().clear();
		this.dialects().addAll(dialects);
		return this;
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
	public ThymeleafV2EngineConfigBuilder<P> setAdditionalDialects(final Set<IDialect> additionalDialects) {
		dialects().addAll(additionalDialects);
		return this;
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
	public ThymeleafV2EngineConfigBuilder<P> clearDialects() {
		dialects().clear();
		return this;
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
	public ThymeleafV2EngineConfigBuilder<P> setTemplateResolvers(final Set<? extends ITemplateResolver> templateResolvers) {
		this.templateResolvers().clear();
		this.templateResolvers().addAll(templateResolvers);
		return this;
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
	public ThymeleafV2EngineConfigBuilder<P> addTemplateResolver(final ITemplateResolver templateResolver) {
		templateResolvers().add(templateResolver);
		return this;
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
	public ThymeleafV2EngineConfigBuilder<P> setTemplateResolver(final ITemplateResolver templateResolver) {
		templateResolvers().clear();
		templateResolvers().add(templateResolver);
		return this;
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
	public ThymeleafV2EngineConfigBuilder<P> setCacheManager(final ICacheManager cacheManager) {
		this.cacheManager = cacheManager;
		return this;
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
	public ThymeleafV2EngineConfigBuilder<P> setMessageResolvers(final Set<? extends IMessageResolver> messageResolvers) {
		this.messageResolvers().clear();
		this.messageResolvers().addAll(messageResolvers);
		return this;
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
	public ThymeleafV2EngineConfigBuilder<P> addMessageResolver(final IMessageResolver messageResolver) {
		messageResolvers().add(messageResolver);
		return this;
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
	public ThymeleafV2EngineConfigBuilder<P> setMessageResolver(final IMessageResolver messageResolver) {
		messageResolvers().clear();
		messageResolvers().add(messageResolver);
		return this;
	}

	/**
	 * <p>
	 * Sets the default message resolvers. These are used when no message
	 * resolvers are set via the {@link #setMessageResolver(IMessageResolver)},
	 * {@link #setMessageResolvers(Set)} or
	 * {@link #addMessageResolver(IMessageResolver)} methods.
	 * </p>
	 * <p>
	 * This method is useful for creating subclasses of TemplateEngine
	 * that establish default configurations for message resolvers.
	 * </p>
	 * <p>
	 * This operation can only be executed before processing templates for the
	 * first time. Once a template is processed, the template engine is
	 * considered to be <i>initialized</i>, and from then on any attempt to
	 * change its configuration will result in an exception.
	 * </p>
	 * 
	 * @param defaultMessageResolvers
	 *            the default message resolvers.
	 * @return this for fluent use
	 */
	public ThymeleafV2EngineConfigBuilder<P> setDefaultMessageResolvers(final Set<? extends IMessageResolver> defaultMessageResolvers) {
		this.defaultMessageResolvers().clear();
		this.defaultMessageResolvers().addAll(defaultMessageResolvers);
		return this;
	}

	/**
	 * <p>
	 * Sets the Template Mode Handlers to be used by this template engine. Every
	 * available template mode must have its corresponding handler.
	 * </p>
	 * <p>
	 * By default, template mode handlers set are
	 * {@link StandardTemplateModeHandlers#ALL_TEMPLATE_MODE_HANDLERS}
	 * </p>
	 * <p>
	 * This operation can only be executed before processing templates for the
	 * first time. Once a template is processed, the template engine is
	 * considered to be <i>initialized</i>, and from then on any attempt to
	 * change its configuration will result in an exception.
	 * </p>
	 * 
	 * @param templateModeHandlers
	 *            the Set of Template Mode Handlers.
	 * @return this for fluent use
	 */
	public ThymeleafV2EngineConfigBuilder<P> setTemplateModeHandlers(final Set<? extends ITemplateModeHandler> templateModeHandlers) {
		this.templateModeHandlers().clear();
		this.templateModeHandlers().addAll(templateModeHandlers);
		return this;
	}

	/**
	 * <p>
	 * Adds a Template Mode Handler to the set of Template Mode Handlers to be
	 * used by the template engine. Every available template mode must have its
	 * corresponding handler.
	 * </p>
	 * <p>
	 * By default, template mode handlers set are
	 * {@link StandardTemplateModeHandlers#ALL_TEMPLATE_MODE_HANDLERS}
	 * </p>
	 * <p>
	 * This operation can only be executed before processing templates for the
	 * first time. Once a template is processed, the template engine is
	 * considered to be <i>initialized</i>, and from then on any attempt to
	 * change its configuration will result in an exception.
	 * </p>
	 * 
	 * @param templateModeHandler
	 *            the new Template Mode Handler to be added.
	 * @return this for fluent use
	 */
	public ThymeleafV2EngineConfigBuilder<P> addTemplateModeHandler(final ITemplateModeHandler templateModeHandler) {
		templateModeHandlers().add(templateModeHandler);
		return this;
	}

	/**
	 * <p>
	 * Sets the default Template Mode Handlers. These are used when no Template
	 * Mode Handlers are set via the {@link #setTemplateModeHandlers(Set)} or
	 * {@link #addTemplateModeHandler(ITemplateModeHandler)} methods.
	 * </p>
	 * <p>
	 * This method is useful for creating subclasses of TemplateEngine
	 * that establish default configurations for Template Mode Handlers.
	 * </p>
	 * <p>
	 * By default, template mode handlers set are
	 * {@link StandardTemplateModeHandlers#ALL_TEMPLATE_MODE_HANDLERS}
	 * </p>
	 * <p>
	 * This operation can only be executed before processing templates for the
	 * first time. Once a template is processed, the template engine is
	 * considered to be <i>initialized</i>, and from then on any attempt to
	 * change its configuration will result in an exception.
	 * </p>
	 * 
	 * @param defaultTemplateModeHandlers
	 *            the default Template Mode Handlers.
	 * @return this for fluent use
	 */
	public ThymeleafV2EngineConfigBuilder<P> setDefaultTemplateModeHandlers(final Set<? extends ITemplateModeHandler> defaultTemplateModeHandlers) {
		defaultTemplateModeHandlers().clear();
		defaultTemplateModeHandlers().addAll(defaultTemplateModeHandlers);
		return this;
	}

	@Override
	public TemplateEngine build() {
		TemplateEngine engine = new TemplateEngine();
		configureDialects(engine);
		configureMessageResolvers(engine);
		configureTemplateResolvers(engine);
		configureTemplateModeHandlers(engine);
		configureCacheManager(engine);
		return engine;
	}

	private void configureCacheManager(TemplateEngine engine) {
		if (cacheManager != null) {
			engine.setCacheManager(cacheManager);
		}
	}

	private void configureTemplateModeHandlers(TemplateEngine engine) {
		if (defaultTemplateModeHandlers != null) {
			engine.setDefaultTemplateModeHandlers(defaultTemplateModeHandlers);
		}
		if (templateModeHandlers != null) {
			engine.setTemplateModeHandlers(templateModeHandlers);
		}
	}

	private void configureTemplateResolvers(TemplateEngine engine) {
		if (templateResolvers != null) {
			engine.setTemplateResolvers(templateResolvers);
		}
	}

	private void configureMessageResolvers(TemplateEngine engine) {
		if (defaultMessageResolvers != null) {
			engine.setDefaultMessageResolvers(defaultMessageResolvers);
		}
		if (messageResolvers != null) {
			engine.setMessageResolvers(messageResolvers);
		}
	}

	private void configureDialects(TemplateEngine engine) {
		if (dialects != null) {
			engine.setDialects(dialects);
		}
		if (dialectsByPrefix != null) {
			engine.setDialectsByPrefix(dialectsByPrefix);
		}
	}

	private Set<IDialect> dialects() {
		if (dialects == null) {
			dialects = new HashSet<>();
		}
		return dialects;
	}

	private Map<String, IDialect> dialectsByPrefix() {
		if (dialectsByPrefix == null) {
			dialectsByPrefix = new HashMap<>();
		}
		return dialectsByPrefix;
	}

	private Set<IMessageResolver> messageResolvers() {
		if (messageResolvers == null) {
			messageResolvers = new HashSet<>();
		}
		return messageResolvers;
	}

	private Set<ITemplateResolver> templateResolvers() {
		if (templateResolvers == null) {
			templateResolvers = new HashSet<>();
		}
		return templateResolvers;
	}

	private Set<IMessageResolver> defaultMessageResolvers() {
		if (defaultMessageResolvers == null) {
			defaultMessageResolvers = new HashSet<>();
		}
		return defaultMessageResolvers;
	}

	private Set<ITemplateModeHandler> templateModeHandlers() {
		if (templateModeHandlers == null) {
			templateModeHandlers = new HashSet<>();
		}
		return templateModeHandlers;
	}

	private Set<ITemplateModeHandler> defaultTemplateModeHandlers() {
		if (defaultTemplateModeHandlers == null) {
			defaultTemplateModeHandlers = new HashSet<>();
		}
		return defaultTemplateModeHandlers;
	}

}
