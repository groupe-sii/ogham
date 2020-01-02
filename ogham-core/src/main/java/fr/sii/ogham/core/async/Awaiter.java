package fr.sii.ogham.core.async;

import java.time.Instant;

import fr.sii.ogham.core.exception.async.WaitException;

/**
 * Simple interface that abstracts the implementation used to wait for some
 * time.
 * 
 * @author Aurélien Baudet
 *
 */
public interface Awaiter {
	/**
	 * Wait until date is reached.
	 * 
	 * @param date
	 *            the point in time to wait for
	 * @throws WaitException
	 *             when waiting has failed
	 */
	void waitUntil(Instant date) throws WaitException;
}
