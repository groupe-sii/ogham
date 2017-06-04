package fr.sii.ogham.core.mimetype.replace;

/**
 * Replaces the original mimetype by something else.
 * 
 * If no replacement should occur, the original mimetype is returned.
 * 
 * @author Aurélien Baudet
 *
 */
public interface MimetypeReplacer {
	/**
	 * Replaces the original mimetype by something else.
	 * 
	 * If no replacement should occur, the original mimetype is returned.
	 * 
	 * @param mimetype
	 *            the original mimetype that may be replaced
	 * @return the new mimetype if replacement is needed or the original
	 *         mimetype
	 */
	String replace(String mimetype);
}
