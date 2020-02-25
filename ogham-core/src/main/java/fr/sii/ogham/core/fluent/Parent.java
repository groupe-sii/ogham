package fr.sii.ogham.core.fluent;

/**
 * Defines a method to go back to the parent in order to chain calls.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent (when calling {@link #and()} method)
 */
public interface Parent<P> {
	/**
	 * Go back to the parent
	 * 
	 * @return the parent instance
	 */
	P and();
}
