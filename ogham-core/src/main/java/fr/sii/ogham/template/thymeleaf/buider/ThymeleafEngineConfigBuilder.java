package fr.sii.ogham.template.thymeleaf.buider;

import org.thymeleaf.TemplateEngine;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.exception.builder.BuildException;

public class ThymeleafEngineConfigBuilder<P> extends AbstractParent<P> implements Builder<TemplateEngine> {

	public ThymeleafEngineConfigBuilder(P parent) {
		super(parent);
	}

	@Override
	public TemplateEngine build() throws BuildException {
		// TODO Auto-generated method stub
		return null;
	}

}
