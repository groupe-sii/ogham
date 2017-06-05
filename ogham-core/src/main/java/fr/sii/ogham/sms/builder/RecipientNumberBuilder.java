package fr.sii.ogham.sms.builder;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.addressing.AddressedPhoneNumber;
import fr.sii.ogham.sms.message.addressing.translator.CompositePhoneNumberTranslator;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberHandler;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;

/**
 * Configures the recipient phone number conversion (from a {@link PhoneNumber}
 * to an {@link AddressedPhoneNumber}).
 * 
 * The {@link PhoneNumber} is used by the developer to provide a simple phone
 * number without knowing how phone number works (no need to handle formats,
 * addressing, countries...). The {@link AddressedPhoneNumber} is used by Ogham
 * implementations to have a phone number that is usable by a technical system.
 * 
 * @author Aurélien Baudet
 *
 */
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

	/**
	 * Defines which standard conversions may be applied on the phone number to
	 * convert it from a {@link PhoneNumber} to an {@link AddressedPhoneNumber}.
	 * 
	 * @return the builder to configure standard phone number conversions
	 */
	public RecipientNumberFormatBuilder format() {
		if (formatBuilder == null) {
			formatBuilder = new RecipientNumberFormatBuilder(this, environmentBuilder);
		}
		return formatBuilder;
	}

	/**
	 * Overrides the standard phone number conversions by the provided handler.
	 * 
	 * <p>
	 * If you call this method several times, only the last custom handler is
	 * used. If you need to apply several conversions, you can use
	 * {@link CompositePhoneNumberTranslator} implementation that delegates to
	 * {@link PhoneNumberHandler}s.
	 * 
	 * @param handler
	 *            the handler to use
	 * @return this instance for fluent chaining
	 */
	public RecipientNumberBuilder convert(PhoneNumberTranslator handler) {
		this.customTranslator = handler;
		return this;
	}

	@Override
	public PhoneNumberTranslator build() {
		if (customTranslator != null) {
			return customTranslator;
		}
		return formatBuilder.build();
	}
}
