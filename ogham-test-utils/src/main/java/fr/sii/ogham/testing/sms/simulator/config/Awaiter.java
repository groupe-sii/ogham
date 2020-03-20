package fr.sii.ogham.testing.sms.simulator.config;

/**
 * Simple interface to simulate a delay or a running task.
 * 
 * @author Aurélien Baudet
 *
 */
public interface Awaiter {
	/**
	 * Simulate waiting for something (expiration of some time, wait for the end
	 * of a running task, ...).
	 */
	void await();
}
