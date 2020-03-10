package fr.sii.ogham.sms.builder;

import fr.sii.ogham.core.builder.BuildContext;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.addressing.AddressedPhoneNumber;

/**
 * Configures the phone number conversions (from a {@link PhoneNumber} to an
 * {@link AddressedPhoneNumber}).
 * 
 * The {@link PhoneNumber} is used by the developer to provide a simple phone
 * number without knowing how phone number works (no need to handle formats,
 * addressing, countries...). The {@link AddressedPhoneNumber} is used by Ogham
 * implementations to have a phone number that is usable by a technical system.
 * 
 * @author Aurélien Baudet
 *
 */
public class PhoneNumbersBuilder extends AbstractParent<SmsBuilder> implements Builder<PhoneNumberTranslatorPair> {
	private final BuildContext buildContext;
	private SenderNumberBuilder senderNumberBuilder;
	private RecipientNumberBuilder recipientNumberBuilder;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for property resolution and evaluation
	 */
	public PhoneNumbersBuilder(SmsBuilder parent, BuildContext buildContext) {
		super(parent);
		this.buildContext = buildContext;
	}

	/**
	 * Configures the sender phone number conversion (from a {@link PhoneNumber}
	 * to an {@link AddressedPhoneNumber}).
	 * 
	 * The {@link PhoneNumber} is used by the developer to provide a simple
	 * phone number without knowing how phone number works (no need to handle
	 * formats, addressing, countries...). The {@link AddressedPhoneNumber} is
	 * used by Ogham implementations to have a phone number that is usable by a
	 * technical system.
	 * 
	 * @return the builder to configure the sender phone number
	 */
	public SenderNumberBuilder from() {
		if (senderNumberBuilder == null) {
			senderNumberBuilder = new SenderNumberBuilder(this, buildContext);
		}
		return senderNumberBuilder;
	}

	/**
	 * Configures the recipient phone number conversion (from a
	 * {@link PhoneNumber} to an {@link AddressedPhoneNumber}).
	 * 
	 * The {@link PhoneNumber} is used by the developer to provide a simple
	 * phone number without knowing how phone number works (no need to handle
	 * formats, addressing, countries...). The {@link AddressedPhoneNumber} is
	 * used by Ogham implementations to have a phone number that is usable by a
	 * technical system.
	 * 
	 * @return the builder to configure the recipient phone number
	 */
	public RecipientNumberBuilder to() {
		if (recipientNumberBuilder == null) {
			recipientNumberBuilder = new RecipientNumberBuilder(this, buildContext);
		}
		return recipientNumberBuilder;
	}

	@Override
	public PhoneNumberTranslatorPair build() {
		return new PhoneNumberTranslatorPair(senderNumberBuilder.build(), recipientNumberBuilder.build());
	}
}
