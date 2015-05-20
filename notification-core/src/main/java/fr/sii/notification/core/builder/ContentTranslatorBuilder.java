package fr.sii.notification.core.builder;

import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.core.message.content.MultiContent;
import fr.sii.notification.core.template.parser.TemplateParser;
import fr.sii.notification.core.translator.EveryContentTranslator;
import fr.sii.notification.core.translator.ContentTranslator;
import fr.sii.notification.core.translator.MultiContentTranslator;
import fr.sii.notification.core.translator.TemplateContentTranslator;

/**
 * Builder for constructing a chained translator. Each translator is able to
 * handle a kind of content and to transform it into another content.
 * 
 * This builder simplifies the definition of the translators to use. Each
 * defined translator will be applied on each content.
 * 
 * @author Aurélien Baudet
 *
 */
public class ContentTranslatorBuilder implements Builder<ContentTranslator> {
	/**
	 * A simple translator that delegates the translation to all of the provided
	 * implementations.
	 */
	private EveryContentTranslator translator;

	public ContentTranslatorBuilder() {
		super();
		translator = new EveryContentTranslator();
	}

	/**
	 * Generate a chain translator that delegates translation of content to all
	 * enabled translators.
	 * 
	 * @return the chain translator
	 * @throws BuildException
	 *             when the translator couldn't be generated
	 */
	@Override
	public ContentTranslator build() throws BuildException {
		return translator;
	}

	/**
	 * Enable the management of templates using all default behaviors and
	 * values. It will use the default template engines. This method registers
	 * the translator that is able to convert a template into a String content.
	 * 
	 * This method is automatically called when calling {@link #useDefaults()}.
	 * 
	 * @return this builder instance for fluent use
	 * @see TemplateBuilder#useDefaults() More information about the default
	 *      behaviors.
	 * @see TemplateContentTranslator More information about the translator for
	 *      templates
	 */
	public ContentTranslatorBuilder withTemplate() {
		return withTemplate(new TemplateBuilder().useDefaults());
	}

	/**
	 * Enable the management of templates using the provided template engine. It
	 * adds a new translator for templates that will use the provided template
	 * engine. The previous added ones are still available.
	 * 
	 * This method is automatically called when calling {@link #useDefaults()}.
	 * 
	 * @return this builder instance for fluent use
	 * @see TemplateContentTranslator More information about the translator for
	 *      templates
	 * @see TemplateParser More information about the parser implementation to
	 *      use
	 */
	public ContentTranslatorBuilder withTemplate(TemplateParser parser) {
		translator.addTranslator(new TemplateContentTranslator(parser));
		return this;
	}

	/**
	 * Enable the management of templates by delegating template engine
	 * construction to the specialized builder. It adds a new translator for
	 * templates that will use the provided template engine. The previous added
	 * ones are still available.
	 * 
	 * This method is automatically called when calling {@link #useDefaults()}.
	 * 
	 * @return this builder instance for fluent use
	 * @see TemplateContentTranslator More information about the translator for
	 *      templates
	 * @see TemplateBuilder More information about the builder to use for
	 *      helping to construct the template parser
	 */
	public ContentTranslatorBuilder withTemplate(TemplateBuilder builder) {
		return withTemplate(builder.build());
	}

	/**
	 * Enable the management of multi-content messages (like email for example).
	 * It adds a new translator that will handle {@link MultiContent} contents.
	 * It will delegate each content management to the chain content translator
	 * built by this builder. It enables to apply the same translations to each
	 * content provided by the multi-content.
	 * 
	 * This method is automatically called when calling {@link #useDefaults()}.
	 * 
	 * @return this builder instance for fluent use
	 * @see MultiContentTranslator More information about multi content
	 *      management
	 */
	public ContentTranslatorBuilder withMultiContentSupport() {
		translator.addTranslator(new MultiContentTranslator(translator));
		return this;
	}

	/**
	 * Tells the builder to use all default behaviors and values. It will enable
	 * default template management and default multi-content support management.
	 * 
	 * @return this builder instance for fluent use
	 * @see #withTemplate() More information about default template management
	 * @see #withMultiContentSupport() More information about default
	 *      multi-content management
	 */
	public ContentTranslatorBuilder useDefaults() {
		withTemplate();
		withMultiContentSupport();
		return this;
	}
}
