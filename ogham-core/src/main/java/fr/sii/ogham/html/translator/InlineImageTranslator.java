package fr.sii.ogham.html.translator;

import static fr.sii.ogham.core.util.HtmlUtils.getDistinctCssImageUrls;
import static fr.sii.ogham.core.util.HtmlUtils.getDistinctImageUrls;
import static fr.sii.ogham.core.util.HtmlUtils.skipExternalUrls;
import static fr.sii.ogham.html.inliner.impl.jsoup.ImageInlineUtils.removeOghamAttributes;
import static fr.sii.ogham.html.inliner.impl.regexp.CssImageInlineUtils.removeOghamProperties;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.exception.handler.ImageInliningException;
import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.HasResourcePath;
import fr.sii.ogham.core.message.content.MayHaveStringContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.message.content.UpdatableStringContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.resource.path.RelativePath;
import fr.sii.ogham.core.resource.path.RelativePathResolver;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.translator.content.ContentTranslator;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.email.message.content.ContentWithAttachments;
import fr.sii.ogham.html.inliner.ContentWithImages;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.inliner.ImageResource;

/**
 * Translator that transforms HTML content. If not HTML, the translator has no
 * effect. The HTML is analyzed in order to find images. For each found image,
 * it uses the resource resolver in order to find the image file. Once all
 * images are found, the HTML is transformed in order to inline the images. The
 * images can be inlined using several methods:
 * <ul>
 * <li>Base64 inliner to convert images to base64 equivalent</li>
 * <li>Extract images and generate attachments to join to the email</li>
 * <li>Maybe anything else</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 * 
 */
public class InlineImageTranslator implements ContentTranslator {
	private static final Logger LOG = LoggerFactory.getLogger(InlineImageTranslator.class);
	
	/**
	 * The image inliner
	 */
	private final ImageInliner inliner;

	/**
	 * The resource resolver used to find images
	 */
	private final ResourceResolver resourceResolver;

	/**
	 * The provider that detects the mimetype for each image
	 */
	private final MimeTypeProvider mimetypeProvider;
	
	/**
	 * Provides an instance used to resolve relative path from source path and relative path
	 */
	private final RelativePathResolver relativePathProvider;

	public InlineImageTranslator(ImageInliner inliner, ResourceResolver resourceResolver, MimeTypeProvider mimetypeProvider, RelativePathResolver relativePathProvider) {
		super();
		this.inliner = inliner;
		this.resourceResolver = resourceResolver;
		this.mimetypeProvider = mimetypeProvider;
		this.relativePathProvider = relativePathProvider;
	}

	@Override
	public Content translate(Content content) throws ContentTranslatorException {
		if (content instanceof MayHaveStringContent && ((MayHaveStringContent) content).canProvideString()) {
			String stringContent = ((MayHaveStringContent) content).asString();
			List<String> images = skipExternalUrls(merge(getDistinctImageUrls(stringContent), getDistinctCssImageUrls(stringContent)));
			if (!images.isEmpty()) {
				LOG.debug("inlining {} images", images.size());
				// prepare list of images paths/urls with their content
				List<ImageResource> imageResources = load(getSourcePath(content), images);
				// generate new HTML with inlined images
				ContentWithImages contentWithImages = inliner.inline(stringContent, imageResources);
				// remove ogham attributes
				ContentWithImages cleaned = clean(contentWithImages);
				// update the HTML content
				Content inlinedContent = updateHtmlContent(content, cleaned);
				LOG.debug("{} images inlined", contentWithImages.getAttachments().size());
				// if it was already a content with attachments then update it otherwise create a new one
				return generateFinalContent(content, cleaned, inlinedContent);
			}
		} else {
			LOG.debug("Neither content usable as string nor HTML. Skip image inlining");
			LOG.trace("content: {}", content);
		}
		return content;
	}

	private static List<String> merge(List<String> distinctImageUrls, List<String> distinctCssImageUrls) {
		List<String> merged = new ArrayList<>(distinctImageUrls);
		merged.addAll(distinctCssImageUrls);
		return merged;
	}

	private static ContentWithImages clean(ContentWithImages contentWithImages) {
		String html = contentWithImages.getContent();
		html = removeOghamAttributes(html);
		html = removeOghamProperties(html);
		contentWithImages.setContent(html);
		return contentWithImages;
	}

	private static ResourcePath getSourcePath(Content content) {
		if(content instanceof HasResourcePath) {
			return ((HasResourcePath) content).getPath();
		}
		return new UnresolvedPath("");
	}
	
	private List<ImageResource> load(ResourcePath sourcePath, List<String> images) throws ContentTranslatorException {
		List<ImageResource> imageResources = new ArrayList<>(images.size());
		for (String path : images) {
			load(imageResources, relativePathProvider.resolve(sourcePath, path));
		}
		return imageResources;
	}

	@SuppressWarnings("squid:S1192")
	private void load(List<ImageResource> imageResources, RelativePath path) throws ContentTranslatorException {
		try {
			byte[] imgContent = IOUtils.toByteArray(resourceResolver.getResource(path).getInputStream());
			String mimetype = mimetypeProvider.detect(new ByteArrayInputStream(imgContent)).toString();
			String imgName = new File(path.getOriginalPath()).getName();
			imageResources.add(new ImageResource(imgName, path.getRelativePath().getOriginalPath(), path, imgContent, mimetype));
		} catch (IOException e) {
			throw new ImageInliningException("Failed to inline image file " + path + " because it can't be read", e);
		} catch (ResourceResolutionException e) {
			throw new ImageInliningException("Failed to inline image file " + path + " because it can't be resolved", e);
		} catch (MimeTypeDetectionException e) {
			throw new ImageInliningException("Failed to inline image file " + path + " because mimetype can't be detected", e);
		}
	}

	private static Content updateHtmlContent(Content content, ContentWithImages contentWithImages) {
		if(content instanceof UpdatableStringContent) {
			LOG.debug("Content is updatable => update it with inlined images");
			((UpdatableStringContent) content).setStringContent(contentWithImages.getContent());
			return content;
		}
		LOG.info("Content is not updatable => create a new StringContent for image inlining result");
		return new StringContent(contentWithImages.getContent());
	}

	private static Content generateFinalContent(Content content, ContentWithImages contentWithImages, Content inlinedContent) {
		if(content instanceof ContentWithAttachments) {
			ContentWithAttachments finalContent = (ContentWithAttachments) content;
			finalContent.addAttachments(contentWithImages.getAttachments());
			finalContent.setContent(inlinedContent);
			return finalContent;
		}
		return new ContentWithAttachments(inlinedContent, contentWithImages.getAttachments());
	}

	@Override
	public String toString() {
		return "InlineImageTranslator";
	}
	
}
