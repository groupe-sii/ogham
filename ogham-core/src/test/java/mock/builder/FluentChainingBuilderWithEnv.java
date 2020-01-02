package mock.builder;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;

public class FluentChainingBuilderWithEnv<P, T> extends FluentChainingBuilder<P, T> {
	public FluentChainingBuilderWithEnv(P parent, EnvironmentBuilder<?> env) {
		super(parent);
	}
}
