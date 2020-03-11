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
	 * <p>
	 * Generally, this method is called only once per instance. But there may
	 * exist some cases where the method is called several times.
	 * 
	 * <p>
	 * The implementation should ensure that if the method is called several
	 * times, the first time cleans the resources and the other calls doesn't do
	 * anything.
	 * 
	 * @throws CleanException
	 *             When cleanup has failed. This exception is likely to be
	 *             ignored because if cleanup has failed, there is nothing more
	 *             we can do. But this exception is important to track that
	 *             cleanup has failed and to indicate it to the developer.
	 */
	void clean() throws CleanException;
}
