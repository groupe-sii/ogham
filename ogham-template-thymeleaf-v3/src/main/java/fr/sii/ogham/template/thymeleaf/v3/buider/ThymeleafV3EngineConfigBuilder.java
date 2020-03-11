package fr.sii.ogham.template.thymeleaf.v3.buider;

import fr.sii.ogham.core.builder.context.BuildContext;
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
public class ThymeleafV3EngineConfigBuilder<P> extends AbstractThymeleafEngineConfigBuilder<ThymeleafV3EngineConfigBuilder<P>, P> {
	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for registering instances and property evaluation
	 */
	public ThymeleafV3EngineConfigBuilder(P parent, BuildContext buildContext) {
		super(ThymeleafV3EngineConfigBuilder.class, parent, buildContext);
	}
}
