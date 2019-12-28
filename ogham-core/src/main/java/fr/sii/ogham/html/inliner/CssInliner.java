package fr.sii.ogham.html.inliner;

import java.util.List;

/**
 * Inline CSS styles directly in the HTML content.
 * 
 * @author Aurélien Baudet
 *
 */
public interface CssInliner {
	/**
	 * Apply external CSS styles (cssContents parameter) on the HTML using
	 * {@code style=""} attributes.
	 * 
	 * @param htmlContent
	 *            the HTML content to update with CSS styles
	 * @param cssContents
	 *            the external CSS declarations
	 * @return the updated HTML content
	 */
	String inline(String htmlContent, List<ExternalCss> cssContents);
}
