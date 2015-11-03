package fr.sii.ogham.email.builder;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.translator.content.ContentTranslator;

public class ImageHandlingBuilder extends AbstractParent<EmailBuilder> implements Builder<ContentTranslator> {
	private ImageInliningBuilder imageInliningBuilder;
	private EnvironmentBuilder<?> environmentBuilder;
	
	public ImageHandlingBuilder(EmailBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
	}

	public ImageInliningBuilder inline() {
		if(imageInliningBuilder==null) {
			imageInliningBuilder = new ImageInliningBuilder(this, environmentBuilder);
		}
		return imageInliningBuilder;
	}

	@Override
	public ContentTranslator build() throws BuildException {
		if(imageInliningBuilder==null) {
			return null;
		}
		return imageInliningBuilder.build();
	}
}
