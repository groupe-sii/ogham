package fr.sii.ogham.sms.builder;

import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.filler.AbstractAutofillDefaultValueBuilder;

public class AutofillDefaultPhoneNumberBuilder<V> extends AbstractAutofillDefaultValueBuilder<AutofillDefaultPhoneNumberBuilder<V>, AutofillSmsBuilder, V> {

	public AutofillDefaultPhoneNumberBuilder(AutofillSmsBuilder parent, Class<V> valueClass, BuildContext buildContext) {
		super(AutofillDefaultPhoneNumberBuilder.class, parent, valueClass, buildContext);
	}

}
