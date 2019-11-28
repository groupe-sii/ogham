package fr.sii.ogham.core.message.content;

/**
 * Emails support to send several text body in several formats. An email can
 * have both a text body and an HTML body.
 * 
 * The Email client then can choose which body to display according to its
 * capabilities.
 * 
 * @author Aurélien Baudet
 *
 */
public enum EmailVariant implements Variant {
	/**
	 * The variant for text/plain body
	 */
	TEXT,
	/**
	 * The variant for text/html body
	 */
	HTML
}
