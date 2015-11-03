package fr.sii.ogham.sms.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.filler.EveryFillerDecorator;
import fr.sii.ogham.core.filler.MessageFiller;
import fr.sii.ogham.sms.filler.SmsFiller;

public class AutofillSmsBuilder extends AbstractParent<SmsBuilder> implements Builder<MessageFiller> {
	private EnvironmentBuilder<?> environmentBuilder;
	private AutofillDefaultPhoneNumberBuilder senderNumberBuilder;
	private AutofillDefaultPhoneNumberBuilder recipientNumberBuilder;

	public AutofillSmsBuilder(SmsBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
	}

	public AutofillDefaultPhoneNumberBuilder from() {
		if(senderNumberBuilder==null) {
			senderNumberBuilder = new AutofillDefaultPhoneNumberBuilder(this);
		}
		return senderNumberBuilder;
	}
	
	public AutofillDefaultPhoneNumberBuilder to() {
		if(recipientNumberBuilder==null) {
			recipientNumberBuilder = new AutofillDefaultPhoneNumberBuilder(this);
		}
		return recipientNumberBuilder;
	}

	@Override
	public MessageFiller build() throws BuildException {
		EveryFillerDecorator filler = new EveryFillerDecorator();
		Map<String, List<String>> props = new HashMap<>();
		props.put("from", senderNumberBuilder.getDefaultValueProperties());
		props.put("to", recipientNumberBuilder.getDefaultValueProperties());
		PropertyResolver propertyResolver = environmentBuilder.build();
		filler.addFiller(new SmsFiller(propertyResolver, props));
		return filler;
	}
}
