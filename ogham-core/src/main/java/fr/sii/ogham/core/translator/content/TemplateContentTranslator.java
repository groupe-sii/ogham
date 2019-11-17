package fr.sii.ogham.core.translator.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.exception.handler.TemplateNotFoundException;
import fr.sii.ogham.core.exception.handler.TemplateParsingFailedException;
import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.message.content.TemplateVariantContent;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.common.adapter.VariantResolver;
import fr.sii.ogham.template.exception.TemplateVariantNotFoundException;
import fr.sii.ogham.template.exception.VariantResolutionException;

/**
 * <p>
 * Translator that handles {@link TemplateContent}. It parses the template and
 * provide an evaluated content. It also handles {@link TemplateVariantContent}
 * through {@link VariantResolver}.
 * </p>
 * <p>
 * The template parsing is delegated to a {@link TemplateParser}.
 * </p>
 * <p>
 * If the content is not a {@link TemplateContent}, then the content is returned
 * as-is
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class TemplateContentTranslator implements ContentTranslator {
	private static final Logger LOG = LoggerFactory.getLogger(TemplateContentTranslator.class);

	/**
	 * The parser to use for finding, loading and evaluating the template
	 */
	private final TemplateParser parser;

	/**
	 * The resolver that converts partial path with variant into real path
	 */
	private final VariantResolver variantResolver;

	public TemplateContentTranslator(TemplateParser parser) {
		this(parser, null);
	}

	public TemplateContentTranslator(TemplateParser parser, VariantResolver variantResolver) {
		super();
		this.parser = parser;
		this.variantResolver = variantResolver;
	}

	@Override
	public Content translate(Content content) throws ContentTranslatorException {
		if (!(content instanceof TemplateContent)) {
			LOG.trace("Not a TemplateContent => skip it");
			return content;
		}
		try {
			TemplateContent template = (TemplateContent) content;
			ResourcePath realPath = getRealPath(template);
			if (realPath == null) {
				LOG.debug("No template found for {}", template.getPath());
				throw new TemplateNotFoundException("Template not found for " + template.getPath().getOriginalPath());
			}
			Context ctx = template.getContext();
			LOG.info("Parse template {} using context {}", realPath, ctx);
			LOG.debug("Parse template content {} using {}", template, parser);
			return parser.parse(realPath, ctx);
		} catch (TemplateVariantNotFoundException e) {
			LOG.debug("No template found for {} after trying to load from {}", e.getTemplatePath(), e.getResolvedPaths());
			throw new TemplateNotFoundException("Template not found for " + e.getTemplatePath().getOriginalPath() + " after trying to load from " + e.getResolvedPaths(), e);
		} catch (ParseException e) {
			throw new TemplateParsingFailedException("failed to translate templated content", e);
		}
	}

	private ResourcePath getRealPath(TemplateContent template) throws VariantResolutionException {
		if (variantResolver == null) {
			return template.getPath();
		}
		return variantResolver.getRealPath(template);
	}

	@Override
	public String toString() {
		return "TemplateContentTranslator";
	}

}
