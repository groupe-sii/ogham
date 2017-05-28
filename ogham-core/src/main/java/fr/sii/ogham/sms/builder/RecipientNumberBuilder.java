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
	
	/**
	 * Default constructor used without all Ogham work.
	 * 
	 * <strong>WARNING: use is only if you know what you are doing !</strong>
	 */
	public RecipientNumberBuilder() {
		this(null, new SimpleEnvironmentBuilder<>(null));
	}

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param environmentBuilder
	 *            the configuration for property resolution and evaluation
	 */
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
