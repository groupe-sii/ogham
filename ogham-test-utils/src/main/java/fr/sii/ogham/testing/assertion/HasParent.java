package fr.sii.ogham.testing.assertion;

/**
 * Represents a assertion helper that has parent assertions.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent
 */
public class HasParent<P> {
	private final P parent;

	/**
	 * Initializes with parent assertions
	 * 
	 * @param parent
	 *            the parent instance that provides assertions
	 */
	public HasParent(P parent) {
		this.parent = parent;
	}

	/**
	 * Go back to parent in the fluent API
	 * 
	 * @return the parent instance
	 */
	public P and() {
		return parent;
	}
}