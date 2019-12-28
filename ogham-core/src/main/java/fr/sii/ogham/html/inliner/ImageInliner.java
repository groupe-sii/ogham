package fr.sii.ogham.html.inliner;

import java.util.List;

/**
 * Interface for all image inliners. There may exist several kind of inliners.
 * For example:
 * <ul>
 * <li>Base64 inliner to convert images to base64 equivalent</li>
 * <li>Extract images and generate attachments to join to the email</li>
 * <li>Maybe anything else</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public interface ImageInliner {
	/**
	 * Transform the HTML content in order to inline images.
	 * 
	 * @param htmlContent
	 *            the HTML content that may contain images to inline
	 * @param images
	 *            the list of found images to inline
	 * @return the new HTML content with possible associated images to attach to
	 *         the mail
	 */
	ContentWithImages inline(String htmlContent, List<ImageResource> images);
}
