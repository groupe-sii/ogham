package fr.sii.ogham.email.builder;

import fr.sii.ogham.core.builder.AbstractAutofillDefaultValueBuilder;

public class AutofillDefaultEmailAddressBuilder<V> extends AbstractAutofillDefaultValueBuilder<AutofillDefaultEmailAddressBuilder<V>, AutofillEmailBuilder, V> {

	public AutofillDefaultEmailAddressBuilder(AutofillEmailBuilder parent, Class<V> valueClass) {
		super(AutofillDefaultEmailAddressBuilder.class, parent, valueClass);
	}

}
