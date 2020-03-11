package fr.sii.ogham.core.builder.registry;

/**
 * Simple interface that represents a write-only registry (only provides a
 * registration method).
 * 
 * @author Aurélien Baudet
 * @param <T>
 *            the type of the registered instances
 *
 */
public interface Registry<T> {
	/**
	 * Registers an object into the registry.
	 * 
	 * @param obj
	 *            the instance to register
	 */
	void register(T obj);
}
