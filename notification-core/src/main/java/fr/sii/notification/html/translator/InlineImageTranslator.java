package fr.sii.notification.html.translator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.sii.notification.core.exception.handler.ContentTranslatorException;
import fr.sii.notification.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.notification.core.exception.resource.ResourceResolutionException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.mimetype.MimeTypeProvider;
import fr.sii.notification.core.resource.resolver.ResourceResolver;
import fr.sii.notification.core.translator.content.ContentTranslator;
import fr.sii.notification.core.util.HtmlUtils;
import fr.sii.notification.core.util.IOUtils;
import fr.sii.notification.email.message.content.ContentWithAttachments;
import fr.sii.notification.html.inliner.ContentWithImages;
import fr.sii.notification.html.inliner.ImageInliner;
import fr.sii.notification.html.inliner.ImageResource;

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
