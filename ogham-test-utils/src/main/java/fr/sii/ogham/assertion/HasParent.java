package fr.sii.ogham.assertion;

public class HasParent<P> {
	private final P parent;

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