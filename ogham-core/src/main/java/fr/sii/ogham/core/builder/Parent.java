package fr.sii.ogham.core.builder;

/**
 * Defines a method to go back to the parent builder in order to chain calls.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public interface Parent<P> {
	/**
	 * Go back to the parent builder
	 * 
	 * @return the parent builder
	 */
	P and();
}
