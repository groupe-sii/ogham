package fr.sii.ogham.email.builder;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.translator.content.ContentTranslator;

public class CssHandlingBuilder extends AbstractParent<EmailBuilder> implements Builder<ContentTranslator> {
	private CssInliningBuilder cssInliningBuilder;
	private EnvironmentBuilder<?> environmentBuilder;
	
	public CssHandlingBuilder(EmailBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
	}

	public CssInliningBuilder inline() {
		if(cssInliningBuilder==null) {
			cssInliningBuilder = new CssInliningBuilder(this, environmentBuilder);
		}
		return cssInliningBuilder;
	}

	@Override
	public ContentTranslator build() throws BuildException {
		if(cssInliningBuilder==null) {
			return null;
		}
		return cssInliningBuilder.build();
	}
}
