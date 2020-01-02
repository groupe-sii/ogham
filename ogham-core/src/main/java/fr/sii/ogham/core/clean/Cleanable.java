package fr.sii.ogham.core.clean;

import fr.sii.ogham.core.exception.clean.CleanException;

/**
 * Mark an implementation that needs to clean some opened resources or memory
 * before destruction.
 * 
 * @author Aurélien Baudet
 *
 */
public interface Cleanable {
	/**
	 * Clean all the opened resources before destruction.
	 * 
	 * @throws CleanException
	 *             When cleanup has failed. This exception is likely to be
	 *             ignored because if cleanup has failed, there is nothing more
	 *             we can do. But this exception is important to track that
	 *             cleanup has failed and to indicate it to the developer.
	 */
	void clean() throws CleanException;
}
