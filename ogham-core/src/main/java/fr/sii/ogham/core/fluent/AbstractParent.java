package fr.sii.ogham.core.fluent;

/**
 * Base implementation that handles the parent and the {@link #and()} method.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent (when calling {@link #and()} method)
 */
public abstract class AbstractParent<P> implements Parent<P> {
	/**
	 * The parent instance
	 */
	protected final P parent;

	/**
	 * Initialize the parent instance
	 * 
	 * @param parent
	 *            the parent
	 */
	public AbstractParent(P parent) {
		super();
		this.parent = parent;
	}

	@Override
	public P and() {
		return parent;
	}

}
