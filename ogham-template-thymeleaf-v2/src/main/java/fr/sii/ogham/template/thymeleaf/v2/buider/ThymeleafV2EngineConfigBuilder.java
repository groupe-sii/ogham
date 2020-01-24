package fr.sii.ogham.template.thymeleaf.v2.buider;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.templatemode.ITemplateModeHandler;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;

import fr.sii.ogham.template.thymeleaf.common.buider.AbstractThymeleafEngineConfigBuilder;

/**
 * Fluent builder to configure Thymeleaf engine.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class ThymeleafV2EngineConfigBuilder<P> extends AbstractThymeleafEngineConfigBuilder<ThymeleafV2EngineConfigBuilder<P>, P> {
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
		super(ThymeleafV2EngineConfigBuilder.class, parent);
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
	/**
	 * <p>
	 * Sets the default message resolvers. These are used when no message
	 * resolvers are set via the {@link #setMessageResolver(IMessageResolver)},
	 * {@link #setMessageResolvers(Set)} or
	 * {@link #addMessageResolver(IMessageResolver)} methods.
	 * </p>
	 * <p>
	 * This method is useful for creating subclasses of {@link TemplateEngine}
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

	@Override
	public TemplateEngine build() {
		TemplateEngine engine = super.build();
		configureTemplateModeHandlers(engine);
		return engine;
	}

	private void configureTemplateModeHandlers(TemplateEngine engine) {
		if (defaultTemplateModeHandlers != null) {
			engine.setDefaultTemplateModeHandlers(defaultTemplateModeHandlers);
		}
		if (templateModeHandlers != null) {
			engine.setTemplateModeHandlers(templateModeHandlers);
		}
	}

	@Override
	protected void configureMessageResolvers(TemplateEngine engine) {
		super.configureMessageResolvers(engine);
		if (defaultMessageResolvers != null) {
			engine.setDefaultMessageResolvers(defaultMessageResolvers);
		}
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
