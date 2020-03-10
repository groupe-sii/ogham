package fr.sii.ogham.email.builder;

import fr.sii.ogham.core.builder.BuildContext;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.core.translator.content.ContentTranslator;

/**
 * CSS handling consists of defining how CSS are inlined in the email. Inlining
 * CSS means that CSS styles are loaded and applied on the matching HTML nodes
 * using the {@code style} HTML attribute.
 * 
 * @author Aurélien Baudet
 *
 */
public class CssHandlingBuilder extends AbstractParent<EmailBuilder> implements Builder<ContentTranslator> {
	private final BuildContext buildContext;
	private CssInliningBuilder cssInliningBuilder;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for property resolution and evaluation
	 */
	public CssHandlingBuilder(EmailBuilder parent, BuildContext buildContext) {
		super(parent);
		this.buildContext = buildContext;
	}

	/**
	 * Configures how CSS are applied on HTML emails.
	 * 
	 * Inlining CSS means that CSS styles are loaded and applied on the matching
	 * HTML nodes using the {@code style} HTML attribute.
	 * 
	 * @return the builder to configure how CSS styles are inlined
	 */
	public CssInliningBuilder inline() {
		if (cssInliningBuilder == null) {
			cssInliningBuilder = new CssInliningBuilder(this, buildContext);
		}
		return cssInliningBuilder;
	}

	@Override
	public ContentTranslator build() {
		if (cssInliningBuilder == null) {
			return null;
		}
		return cssInliningBuilder.build();
	}
}
