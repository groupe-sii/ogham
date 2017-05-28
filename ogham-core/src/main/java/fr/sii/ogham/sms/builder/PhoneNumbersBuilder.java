package fr.sii.ogham.sms.builder;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.sms.builder.PhoneNumbersBuilder.PhoneNumberTranslatorPair;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;

public class PhoneNumbersBuilder extends AbstractParent<SmsBuilder> implements Builder<PhoneNumberTranslatorPair> {
	private EnvironmentBuilder<?> environmentBuilder;
	private SenderNumberBuilder senderNumberBuilder;
	private RecipientNumberBuilder recipientNumberBuilder;

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
	public PhoneNumbersBuilder(SmsBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
	}

	public SenderNumberBuilder from() {
		if(senderNumberBuilder==null) {
			senderNumberBuilder = new SenderNumberBuilder(this, environmentBuilder);
		}
		return senderNumberBuilder;
	}

	public RecipientNumberBuilder to() {
		if(recipientNumberBuilder==null) {
			recipientNumberBuilder = new RecipientNumberBuilder(this, environmentBuilder);
		}
		return recipientNumberBuilder;
	}

	@Override
	public PhoneNumberTranslatorPair build() throws BuildException {
		return new PhoneNumberTranslatorPair(senderNumberBuilder.build(), recipientNumberBuilder.build());
	}
	
	public static class PhoneNumberTranslatorPair {
		private final PhoneNumberTranslator sender;
		private final PhoneNumberTranslator recipient;
		public PhoneNumberTranslatorPair(PhoneNumberTranslator sender, PhoneNumberTranslator recipient) {
			super();
			this.sender = sender;
			this.recipient = recipient;
		}
		public PhoneNumberTranslator getSender() {
			return sender;
		}
		public PhoneNumberTranslator getRecipient() {
			return recipient;
		}
	}
}
