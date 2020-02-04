package fr.sii.ogham.testing.assertion.util;

import java.io.IOException;

/**
 * Interface for registering functions that contains assertions.
 * 
 * The registered functions may throw exception when called.
 * 
 * Assertions can either be registered and evalutated later or immediately
 * evaluated after registration (depending on the strategy).
 * 
 * @author Aurélien Baudet
 *
 */
public interface AssertionRegistry {
	/**
	 * Register a function that contains assertions.
	 * 
	 * The function may throw exceptions (for example when the function tries to
	 * access a value that may fail, typically an {@link IOException}).
	 * 
	 * @param <E>
	 *            the type of the thrown exception if any
	 * @param executable
	 *            the function to register
	 * @throws E
	 *             the thrown exception
	 */
	<E extends Exception> void register(Executable<E> executable) throws E;

	/**
	 * Execute all registered functions
	 */
	void execute();
}
