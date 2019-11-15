package fr.sii.ogham.html.translator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.HasResourcePath;
import fr.sii.ogham.core.message.content.MayHaveStringContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.message.content.UpdatableStringContent;
import fr.sii.ogham.core.resource.path.RelativePathResolver;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.translator.content.ContentTranslator;
import fr.sii.ogham.core.util.HtmlUtils;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.html.inliner.CssInliner;
import fr.sii.ogham.html.inliner.ExternalCss;

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
	private static final Logger LOG = LoggerFactory.getLogger(InlineCssTranslator.class);
	
	/**
	 * The CSS inliner
	 */
	private final CssInliner cssInliner;

	/**
	 * The resource resolver to find the CSS files
	 */
	private final ResourceResolver resourceResolver;
	
	/**
	 * Provides an instance used to resolve relative path from source path and relative path
	 */
	private final RelativePathResolver relativePathProvider;

	public InlineCssTranslator(CssInliner cssInliner, ResourceResolver resourceResolver, RelativePathResolver relativePathProvider) {
		super();
		this.cssInliner = cssInliner;
		this.resourceResolver = resourceResolver;
		this.relativePathProvider = relativePathProvider;
	}

	@Override
	public Content translate(Content content) throws ContentTranslatorException {
		if (content instanceof MayHaveStringContent && ((MayHaveStringContent) content).canProvideString()) {
			String stringContent = ((MayHaveStringContent) content).asString();
			if (HtmlUtils.isHtml(stringContent)) {
				List<String> cssFiles = HtmlUtils.getDistinctCssUrls(stringContent);
				if (!cssFiles.isEmpty()) {
					// prepare list of css files/urls with their content
					List<ExternalCss> cssResources = load(getSourcePath(content), cssFiles);
					// generate the content with inlined css
					String inlinedContentStr = cssInliner.inline(stringContent, cssResources);
					// update the HTML content
					return updateHtmlContent(content, inlinedContentStr);
				}
			}
		} else {
			LOG.debug("Neither content as string nor HTML. Skip CSS inlining");
			LOG.trace("content: {}", content);
		}
		return content;
	}

	private ResourcePath getSourcePath(Content content) {
		if(content instanceof HasResourcePath) {
			return ((HasResourcePath) content).getPath();
		}
		return new UnresolvedPath("");
	}

	private List<ExternalCss> load(ResourcePath sourcePath, List<String> cssFiles) throws ContentTranslatorException {
		List<ExternalCss> cssResources = new ArrayList<>(cssFiles.size());
		for (String path : cssFiles) {
			load(cssResources, relativePathProvider.resolve(sourcePath, path));
		}
		return cssResources;
	}

	private void load(List<ExternalCss> cssResources, ResourcePath path) throws ContentTranslatorException {
		try {
			cssResources.add(new ExternalCss(path, IOUtils.toString(resourceResolver.getResource(path).getInputStream())));
		} catch (IOException e) {
			throw new ContentTranslatorException("Failed to inline CSS file " + path + " because it can't be read", e);
		} catch (ResourceResolutionException e) {
			throw new ContentTranslatorException("Failed to inline CSS file " + path + " because it can't be resolved", e);
		}
	}

	private Content updateHtmlContent(Content content, String inlinedContentStr) {
		Content inlinedContent = content;
		if(content instanceof UpdatableStringContent) {
			LOG.debug("Content is updatable => update it with inlined CSS");
			((UpdatableStringContent) inlinedContent).setStringContent(inlinedContentStr);
		} else {
			LOG.info("Content is not updatable => create a new StringContent for CSS inlining result");
			inlinedContent = new StringContent(inlinedContentStr);
		}
		return inlinedContent;
	}

	@Override
	public String toString() {
		return "InlineCssTranslator";
	}
	
}
