package fr.sii.ogham.sms.builder;

import fr.sii.ogham.core.builder.AbstractAutofillDefaultValueBuilder;

public class AutofillDefaultPhoneNumberBuilder<V> extends AbstractAutofillDefaultValueBuilder<AutofillDefaultPhoneNumberBuilder<V>, AutofillSmsBuilder, V> {

	public AutofillDefaultPhoneNumberBuilder(AutofillSmsBuilder parent, Class<V> valueClass) {
		super(AutofillDefaultPhoneNumberBuilder.class, parent, valueClass);
	}

}
