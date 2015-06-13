package fr.sii.notification.html.translator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.sii.notification.core.exception.handler.ContentTranslatorException;
import fr.sii.notification.core.exception.resource.ResourceResolutionException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.resource.resolver.ResourceResolver;
import fr.sii.notification.core.translator.content.ContentTranslator;
import fr.sii.notification.core.util.HtmlUtils;
import fr.sii.notification.core.util.IOUtils;
import fr.sii.notification.html.inliner.CssInliner;
import fr.sii.notification.html.inliner.ExternalCss;

/**
 * Translator that transforms HTML content. If not HTML, the translator has no
 * effect. The HTML is analyzed in order to find external css files. For each
 * found image, it uses the resource resolver in order to find the css file.
 * Once all css files are found, the HTML is transformed in order to inline the
 * styles.
 * 
 * @author Aurélien Baudet
 *
 */
public class InlineCssTranslator implements ContentTranslator {
	/**
	 * The CSS inliner
	 */
	private CssInliner cssInliner;

	/**
	 * The resource resolver to find the CSS files
	 */
	private ResourceResolver resourceResolver;

	public InlineCssTranslator(CssInliner cssInliner, ResourceResolver resourceResolver) {
		super();
		this.cssInliner = cssInliner;
		this.resourceResolver = resourceResolver;
	}

	@Override
	public Content translate(Content content) throws ContentTranslatorException {
		String stringContent = content.toString();
		if (HtmlUtils.isHtml(stringContent)) {
			List<String> cssFiles = HtmlUtils.getCssFiles(stringContent);
			List<ExternalCss> cssResources = new ArrayList<>(cssFiles.size());
			for (String path : cssFiles) {
				try {
					cssResources.add(new ExternalCss(path, IOUtils.toString(resourceResolver.getResource(path).getInputStream())));
				} catch (IOException e) {
					throw new ContentTranslatorException("Failed to inline CSS file " + path + " because it can't be read", e);
				} catch (ResourceResolutionException e) {
					throw new ContentTranslatorException("Failed to inline CSS file " + path + " because it can't be resolved", e);
				}
			}
			return new StringContent(cssInliner.inline(stringContent, cssResources));
		} else {
			return content;
		}
	}
}
