package mock.builder;

import fr.sii.ogham.core.builder.context.BuildContext;

public class FluentChainingBuilderWithEnv<P, T> extends FluentChainingBuilder<P, T> {
	public FluentChainingBuilderWithEnv(P parent, BuildContext buildContext) {
		super(parent);
	}
}
