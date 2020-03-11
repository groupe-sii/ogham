package fr.sii.ogham.sms.builder;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.context.DefaultBuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.addressing.AddressedPhoneNumber;
import fr.sii.ogham.sms.message.addressing.translator.CompositePhoneNumberTranslator;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberHandler;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;

/**
 * Configures the sender phone number conversion (from a {@link PhoneNumber} to
 * an {@link AddressedPhoneNumber}).
 * 
 * The {@link PhoneNumber} is used by the developer to provide a simple phone
 * number without knowing how phone number works (no need to handle formats,
 * addressing, countries...). The {@link AddressedPhoneNumber} is used by Ogham
 * implementations to have a phone number that is usable by a technical system.
 * 
 * @author Aurélien Baudet
 *
 */
public class SenderNumberBuilder extends AbstractParent<PhoneNumbersBuilder> implements Builder<PhoneNumberTranslator> {
	private final BuildContext buildContext;
	private SenderNumberFormatBuilder formatBuilder;
	private PhoneNumberTranslator customTranslator;

	/**
	 * Default constructor when using without all Ogham work.
	 * 
	 * <strong>WARNING: use is only if you know what you are doing !</strong>
	 */
	public SenderNumberBuilder() {
		this(null, new DefaultBuildContext());
	}

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for registering instances and property evaluation
	 */
	public SenderNumberBuilder(PhoneNumbersBuilder parent, BuildContext buildContext) {
		super(parent);
		this.buildContext = buildContext;
	}

	/**
	 * Defines which standard conversions may be applied on the phone number to
	 * convert it from a {@link PhoneNumber} to an {@link AddressedPhoneNumber}.
	 * 
	 * @return the builder to configure standard phone number conversions
	 */
	public SenderNumberFormatBuilder format() {
		if (formatBuilder == null) {
			formatBuilder = new SenderNumberFormatBuilder(this, buildContext);
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
	public SenderNumberBuilder convert(PhoneNumberTranslator handler) {
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
