package fr.sii.ogham.core.builder;

public class AbstractParent<P> implements Parent<P> {
	protected final P parent;
	
	public AbstractParent(P parent) {
		super();
		this.parent = parent;
	}

	@Override
	public P and() {
		return parent;
	}

}
