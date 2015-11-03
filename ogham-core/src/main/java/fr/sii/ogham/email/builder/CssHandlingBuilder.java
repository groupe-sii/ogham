package fr.sii.ogham.email.builder;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.translator.content.ContentTranslator;

public class CssHandlingBuilder extends AbstractParent<EmailBuilder> implements Builder<ContentTranslator> {
	private CssInliningBuilder cssInliningBuilder;
	
	public CssHandlingBuilder(EmailBuilder parent) {
		super(parent);
	}

	public CssInliningBuilder inline() {
		if(cssInliningBuilder==null) {
			cssInliningBuilder = new CssInliningBuilder(this);
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
