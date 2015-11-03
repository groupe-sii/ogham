package fr.sii.ogham.sms.builder;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;

public class RecipientNumberBuilder extends AbstractParent<PhoneNumbersBuilder> implements Builder<PhoneNumberTranslator> {
	private EnvironmentBuilder<?> environmentBuilder;
	private RecipientNumberFormatBuilder formatBuilder;
	private PhoneNumberTranslator customTranslator;
	
	public RecipientNumberBuilder() {
		this(null, new SimpleEnvironmentBuilder<>(null));
	}

	public RecipientNumberBuilder(PhoneNumbersBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
	}

	public RecipientNumberFormatBuilder format() {
		if(formatBuilder==null) {
			formatBuilder = new RecipientNumberFormatBuilder(this, environmentBuilder);
		}
		return formatBuilder;
	}
	
	public RecipientNumberBuilder convert(PhoneNumberTranslator handler) {
		this.customTranslator = handler;
		return this;
	}

	@Override
	public PhoneNumberTranslator build() throws BuildException {
		if(customTranslator!=null) {
			return customTranslator;
		}
		return formatBuilder.build();
	}
}
