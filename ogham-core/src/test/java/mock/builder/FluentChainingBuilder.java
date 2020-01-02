package mock.builder;

import fr.sii.ogham.core.builder.Parent;

public class FluentChainingBuilder<P, T> extends MockBuilder<T> implements Parent<P> {
	private final P parent;
	
	public FluentChainingBuilder(P parent) {
		super();
		this.parent = parent;
	}

	@Override
	public P and() {
		return parent;
	}
}
