package fr.sii.notification.core.builder;

import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.core.template.parser.TemplateParser;
import fr.sii.notification.core.translator.ChainContentTranslator;
import fr.sii.notification.core.translator.ContentTranslator;
import fr.sii.notification.core.translator.MultiContentTranslator;
import fr.sii.notification.core.translator.TemplateContentTranslator;

public class ContentTranslatorBuilder implements Builder<ContentTranslator> {
	private ChainContentTranslator translator;
	
	public ContentTranslatorBuilder() {
		super();
		translator = new ChainContentTranslator();
	}
	
	@Override
	public ContentTranslator build() throws BuildException {
		return translator;
	}
	
	public ContentTranslatorBuilder withTemplate() {
		return withTemplate(new TemplateBuilder().useDefaults());
	}
	
	public ContentTranslatorBuilder withTemplate(TemplateParser parser) {
		translator.addTranslator(new TemplateContentTranslator(parser));
		return this;
	}
	
	public ContentTranslatorBuilder withTemplate(TemplateBuilder builder) {
		return withTemplate(builder.build());
	}
	
	public ContentTranslatorBuilder withMultiContentSupport() {
		translator.addTranslator(new MultiContentTranslator(translator));
		return this;
	}
	
	public ContentTranslatorBuilder useDefaults() {
		withTemplate();
		withMultiContentSupport();
		return this;
	}
}
