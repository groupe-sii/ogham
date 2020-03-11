package fr.sii.ogham.sms.builder;

import java.util.HashMap;
import java.util.Map;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.filler.EveryFillerDecorator;
import fr.sii.ogham.core.filler.MessageFiller;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.sms.filler.SmsFiller;
import fr.sii.ogham.sms.message.Sms;

/**
 * Configures how Ogham will add default values to the {@link Sms} if some
 * information is missing.
 * 
 * If sender phone number is missing, a default one can be defined in
 * configuration properties.
 * 
 * If recipient phone number is missing, a default one can be defined in
 * configuration properties.
 * 
 * @author Aurélien Baudet
 * @see SmsFiller
 *
 */
public class AutofillSmsBuilder extends AbstractParent<SmsBuilder> implements Builder<MessageFiller> {
	private final BuildContext buildContext;
	private AutofillDefaultPhoneNumberBuilder<String> senderNumberBuilder;
	private AutofillDefaultPhoneNumberBuilder<String[]> recipientNumberBuilder;

	/**
	 * Initializes with the parent builder and the {@link EnvironmentBuilder}.
	 * The parent builder is used when calling the {@link #and()} method. The
	 * {@link EnvironmentBuilder} is used by {@link #build()} method to evaluate
	 * property values.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for property resolution
	 */
	public AutofillSmsBuilder(SmsBuilder parent, BuildContext buildContext) {
		super(parent);
		this.buildContext = buildContext;
	}

	/**
	 * Configures how to handle missing sender phone number: if no sender phone
	 * number is explicitly defined on the message, Ogham will use this phone
	 * number as default sender number.
	 * 
	 * @return the builder to configure default sender number
	 */
	public AutofillDefaultPhoneNumberBuilder<String> from() {
		if (senderNumberBuilder == null) {
			senderNumberBuilder = new AutofillDefaultPhoneNumberBuilder<>(this, String.class, buildContext);
		}
		return senderNumberBuilder;
	}

	/**
	 * Configures how to handle missing recipient phone number: if no recipient
	 * phone number is explicitly defined on the message, Ogham will use this
	 * phone number as default recipient number.
	 * 
	 * @return the builder to configure default recipient number
	 */
	public AutofillDefaultPhoneNumberBuilder<String[]> to() {
		if (recipientNumberBuilder == null) {
			recipientNumberBuilder = new AutofillDefaultPhoneNumberBuilder<>(this, String[].class, buildContext);
		}
		return recipientNumberBuilder;
	}

	@Override
	public MessageFiller build() {
		EveryFillerDecorator filler = buildContext.register(new EveryFillerDecorator());
		Map<String, ConfigurationValueBuilderHelper<?, ?>> props = new HashMap<>();
		props.put("from", (ConfigurationValueBuilderHelper<?, String>) senderNumberBuilder.defaultValue());
		props.put("to", (ConfigurationValueBuilderHelper<?, String[]>) recipientNumberBuilder.defaultValue());
		filler.addFiller(buildContext.register(new SmsFiller(props)));
		return filler;
	}
}
