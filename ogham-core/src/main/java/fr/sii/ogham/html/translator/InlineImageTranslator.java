package fr.sii.ogham.html.translator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
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
		if (content instanceof StringContent) {
			String stringContent = content.toString();
			if (HtmlUtils.isHtml(stringContent)) {
				List<String> images = HtmlUtils.getImages(stringContent);
				if (!images.isEmpty()) {
					List<ImageResource> imageResources = new ArrayList<>(images.size());
					for (String path : images) {
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
					ContentWithImages contentWithImages = inliner.inline(stringContent, imageResources);
					return new ContentWithAttachments(new StringContent(contentWithImages.getContent()), contentWithImages.getAttachments());
				}
			}
		}
		return content;
	}

}
