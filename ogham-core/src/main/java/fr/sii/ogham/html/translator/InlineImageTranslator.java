package fr.sii.ogham.html.translator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.MayHaveStringContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.message.content.UpdatableStringContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.translator.content.ContentTranslator;
import fr.sii.ogham.core.util.HtmlUtils;
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
	private ImageInliner inliner;

	/**
	 * The resource resolver used to find images
	 */
	private ResourceResolver resourceResolver;

	/**
	 * The provider that detects the mimetype for each image
	 */
	private MimeTypeProvider mimetypeProvider;

	public InlineImageTranslator(ImageInliner inliner, ResourceResolver resourceResolver, MimeTypeProvider mimetypeProvider) {
		super();
		this.inliner = inliner;
		this.resourceResolver = resourceResolver;
		this.mimetypeProvider = mimetypeProvider;
	}

	@Override
	public Content translate(Content content) throws ContentTranslatorException {
		if (content instanceof MayHaveStringContent && ((MayHaveStringContent) content).canProvideString()) {
			String stringContent = ((MayHaveStringContent) content).asString();
			List<String> images = HtmlUtils.getDistinctImageUrls(stringContent);
			if (!images.isEmpty()) {
				// parepare list of images paths/urls with their content
				List<ImageResource> imageResources = load(images);
				// generate new HTML with inlined images
				ContentWithImages contentWithImages = inliner.inline(stringContent, imageResources);
				// update the HTML content
				Content inlinedContent = updateHtmlContent(content, contentWithImages);
				// if it was already a content with attachments then update it otherwise create a new one
				return generateFinalContent(content, contentWithImages, inlinedContent);
			}
		} else {
			LOG.debug("Neither content usable as string nor HTML. Skip image inlining for {}", content);
		}
		return content;
	}

	private List<ImageResource> load(List<String> images) throws ContentTranslatorException {
		List<ImageResource> imageResources = new ArrayList<>(images.size());
		for (String path : images) {
			load(imageResources, path);
		}
		return imageResources;
	}

	private void load(List<ImageResource> imageResources, String path) throws ContentTranslatorException {
		try {
			byte[] imgContent = IOUtils.toByteArray(resourceResolver.getResource(path).getInputStream());
			String mimetype = mimetypeProvider.detect(new ByteArrayInputStream(imgContent)).toString();
			String imgName = new File(path).getName().toString();
			imageResources.add(new ImageResource(imgName, path, imgContent, mimetype));
		} catch (IOException e) {
			throw new ContentTranslatorException("Failed to inline CSS file " + path + " because it can't be read", e);
		} catch (ResourceResolutionException e) {
			throw new ContentTranslatorException("Failed to inline CSS file " + path + " because it can't be resolved", e);
		} catch (MimeTypeDetectionException e) {
			throw new ContentTranslatorException("Failed to inline CSS file " + path + " because mimetype can't be detected", e);
		}
	}

	private Content updateHtmlContent(Content content, ContentWithImages contentWithImages) {
		Content inlinedContent = content;
		if(inlinedContent instanceof UpdatableStringContent) {
			LOG.debug("Content is updatable => update it with inlined images");
			((UpdatableStringContent) inlinedContent).setStringContent(contentWithImages.getContent());
		} else {
			LOG.info("Content is not updatable => create a new StringContent for image inlining result");
			inlinedContent = new StringContent(contentWithImages.getContent());
		}
		return inlinedContent;
	}

	private Content generateFinalContent(Content content, ContentWithImages contentWithImages, Content inlinedContent) {
		ContentWithAttachments finalContent;
		if(content instanceof ContentWithAttachments) {
			finalContent = ((ContentWithAttachments) content);
			finalContent.addAttachments(contentWithImages.getAttachments());
			finalContent.setContent(inlinedContent);
		} else {
			finalContent = new ContentWithAttachments(inlinedContent, contentWithImages.getAttachments());
		}
		return finalContent;
	}
}
