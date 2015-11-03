package fr.sii.ogham.sms.builder;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;

public class SenderNumberBuilder extends AbstractParent<PhoneNumbersBuilder> implements Builder<PhoneNumberTranslator> {
	private EnvironmentBuilder<?> environmentBuilder;
	private SenderNumberFormatBuilder formatBuilder;
	private PhoneNumberTranslator customTranslator;
	
	public SenderNumberBuilder() {
		this(null, new SimpleEnvironmentBuilder<>(null));
	}

	public SenderNumberBuilder(PhoneNumbersBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
	}

	public SenderNumberFormatBuilder format() {
		if(formatBuilder==null) {
			formatBuilder = new SenderNumberFormatBuilder(this, environmentBuilder);
		}
		return formatBuilder;
	}
	
	public SenderNumberBuilder convert(PhoneNumberTranslator handler) {
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
