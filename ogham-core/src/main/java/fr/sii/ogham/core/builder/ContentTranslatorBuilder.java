package fr.sii.ogham.core.builder;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.id.generator.SequentialIdGenerator;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.mimetype.TikaProvider;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.core.translator.content.ContentTranslator;
import fr.sii.ogham.core.translator.content.EveryContentTranslator;
import fr.sii.ogham.core.translator.content.MultiContentTranslator;
import fr.sii.ogham.core.translator.content.TemplateContentTranslator;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.html.inliner.EveryImageInliner;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupAttachImageInliner;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupBase64ImageInliner;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupCssInliner;
import fr.sii.ogham.html.translator.InlineCssTranslator;
import fr.sii.ogham.html.translator.InlineImageTranslator;

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
	private static final Logger LOG = LoggerFactory.getLogger(ContentTranslatorBuilder.class);

	/**
	 * The builder for parsing templates. If set, then a
	 * {@link TemplateContentTranslator} is created with this template builder.
	 */
	private TemplateBuilder templateBuilder;

	/**
	 * If true, a {@link MultiContentTranslator} is added to handle
	 * {@link MultiContent}
	 */
	private boolean enableMultiContent;

	/**
	 * If true, a {@link InlineCssTranslator} and a
	 * {@link InlineImageTranslator} are added
	 */
	private boolean enableInlining;

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
		LOG.info("Using translator that calls all registered translators");
		EveryContentTranslator translator = new EveryContentTranslator();
		if (templateBuilder != null) {
			TemplateParser templateParser = templateBuilder.build();
			LOG.debug("Registering content translator that parses templates using {}", templateParser);
			translator.addTranslator(new TemplateContentTranslator(templateParser));
		}
		if (enableMultiContent) {
			LOG.debug("Multi-content transformation is enabled");
			translator.addTranslator(new MultiContentTranslator(translator));
		}
		if (enableInlining) {
			// TODO: extract inliners init to their own builders
			LOG.debug("CSS inlining is enabled");
			FirstSupportingResourceResolver resolver = new FirstSupportingResourceResolverBuilder().useDefaults().build();
			translator.addTranslator(new InlineCssTranslator(new JsoupCssInliner(), resolver));
			LOG.debug("Image inlining is enabled");
			MimeTypeProvider mimetypeProvider = new TikaProvider();
			ImageInliner imageInliner = new EveryImageInliner(new JsoupAttachImageInliner(new SequentialIdGenerator()), new JsoupBase64ImageInliner());
			translator.addTranslator(new InlineImageTranslator(imageInliner, resolver, mimetypeProvider));
		}
		return translator;
	}

	/**
	 * Enable the management of templates using all default behaviors and
	 * values. It will use the default template engines. This method registers
	 * the translator that is able to convert a template into a String content.
	 * <p>
	 * This method is automatically called when calling {@link #useDefaults()}.
	 * </p>
	 * 
	 * @return this builder instance for fluent use
	 * @see TemplateBuilder#useDefaults() More information about the default
	 *      behaviors.
	 * @see TemplateContentTranslator More information about the translator for
	 *      templates
	 */
	public ContentTranslatorBuilder withTemplate() {
		return withTemplate(BuilderUtils.getDefaultProperties());
	}

	/**
	 * Enable the management of templates using all default behaviors and
	 * values. It will use the default template engines. This method registers
	 * the translator that is able to convert a template into a String content.
	 * <p>
	 * This method is automatically called when calling {@link #useDefaults()}.
	 * </p>
	 * 
	 * @param properties
	 *            the properties to use
	 * @return this builder instance for fluent use
	 * @see TemplateBuilder#useDefaults() More information about the default
	 *      behaviors.
	 * @see TemplateContentTranslator More information about the translator for
	 *      templates
	 */
	public ContentTranslatorBuilder withTemplate(Properties properties) {
		return withTemplate(new TemplateBuilder().useDefaults(properties));
	}

	/**
	 * Enable the management of templates by delegating template engine
	 * construction to the specialized builder. It adds a new translator for
	 * templates that will use the provided template engine. The previous added
	 * ones are still available.
	 * <p>
	 * This method is automatically called when calling {@link #useDefaults()}.
	 * </p>
	 * 
	 * @param builder
	 *            the builder that helps to construct the parser to add
	 * @return this builder instance for fluent use
	 * @see TemplateContentTranslator More information about the translator for
	 *      templates
	 * @see TemplateBuilder More information about the builder to use for
	 *      helping to construct the template parser
	 */
	public ContentTranslatorBuilder withTemplate(TemplateBuilder builder) {
		this.templateBuilder = builder;
		return this;
	}

	/**
	 * Enable the management of multi-content messages (like email for example).
	 * It adds a new translator that will handle {@link MultiContent} contents.
	 * It will delegate each content management to the chain content translator
	 * built by this builder. It enables to apply the same translations to each
	 * content provided by the multi-content.
	 * <p>
	 * This method is automatically called when calling {@link #useDefaults()}.
	 * </p>
	 * 
	 * @return this builder instance for fluent use
	 * @see MultiContentTranslator More information about multi content
	 *      management
	 */
	public ContentTranslatorBuilder withMultiContentSupport() {
		enableMultiContent = true;
		return this;
	}

	/**
	 * Enable the management of resource inlining:
	 * <ul>
	 * <li>Inline CSS files into the HTML content</li>
	 * <li>Inline image files into the HTML content</li>
	 * </ul>
	 * <p>
	 * This method is automatically called when calling {@link #useDefaults()}.
	 * </p>
	 * 
	 * @return this builder instance for fluent use
	 * @see MultiContentTranslator More information about multi content
	 *      management
	 */
	public ContentTranslatorBuilder withInlining() {
		enableInlining = true;
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
	 * @see #withInlining() More information about default inlining management
	 */
	public ContentTranslatorBuilder useDefaults() {
		useDefaults(BuilderUtils.getDefaultProperties());
		return this;
	}

	/**
	 * Tells the builder to use all default behaviors and values. It will enable
	 * default template management and default multi-content support management.
	 * 
	 * @param properties
	 *            the properties to use
	 * @return this builder instance for fluent use
	 * @see #withTemplate() More information about default template management
	 * @see #withMultiContentSupport() More information about default
	 *      multi-content management
	 * @see #withInlining() More information about default inlining management
	 */
	public ContentTranslatorBuilder useDefaults(Properties properties) {
		withTemplate(properties);
		withMultiContentSupport();
		withInlining();
		return this;
	}

	/**
	 * <p>
	 * Get the builder used to handle template support.
	 * </p>
	 * 
	 * Access this builder if you want to:
	 * <ul>
	 * <li>Customize how template resources are resolved</li>
	 * <li>Register a custom lookup mapping resolver for template resources</li>
	 * <li>Use your own template engine</li>
	 * <li>Customize the template engine configuration</li>
	 * <li>Set the parent path and extension for template resolution</li>
	 * <li>Set the property key for parent path and extension resolution</li>
	 * </ul>
	 * 
	 * @return the template builder
	 */
	public TemplateBuilder getTemplateBuilder() {
		return templateBuilder;
	}
}
