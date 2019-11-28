package fr.sii.ogham.sms.builder.cloudhopper;

import static fr.sii.ogham.core.util.BuilderUtils.evaluate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.sms.builder.cloudhopper.UserDataBuilder.UserDataPropValues;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.MessagePreparator;

/**
 * "User Data" represents the text message or data to be transmitted the
 * end-user.
 * 
 * <p>
 * The length of the short message text (or user data) is defined in the
 * {@code sm_length} field of the {@code submit_sm}, {@code submit_multi},
 * {@code deliver_sm} and {@code replace_sm} SMPP PDUs. The maximum message
 * length which can be specified in {@code sm_length} field is 254 octets. If an
 * ESME wishes to submit a message of length greater than 254 octets, the
 * {@code sm_length} field must be set to {@code NULL} and the
 * {@code message_payload} optional parameter must be populated with the message
 * length value and user data. SMPP supports extended message lengths in the
 * {@code submit_sm}, {@code submit_multi}, {@code data_sm} and
 * {@code deliver_sm} PDUs. Note: The actual short message length which can be
 * transmitted to a MS may vary according to the underlying network.
 * 
 * <p>
 * This builder allows to select which field to use:
 * <ul>
 * <li>either {@code short_message} fields</li>
 * <li>or {@code message_payload} optional TLV (Tag-Length-Value) parameter</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class UserDataBuilder extends AbstractParent<CloudhopperBuilder> implements Builder<UserDataPropValues> {
	private final EnvironmentBuilder<?> environmentBuilder;
	private final List<String> useShortMessageProps;
	private final List<String> useTlvMessagePayloadProps;

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
	public UserDataBuilder(CloudhopperBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		useShortMessageProps = new ArrayList<>();
		useTlvMessagePayloadProps = new ArrayList<>();
	}

	/**
	 * Enable/disable use of {@code short_message} field.
	 * 
	 * <p>
	 * If several properties are already set using
	 * {@link #useShortMessage(String...)}, then the enabled value is appended.
	 * 
	 * For example,
	 * 
	 * <pre>
	 * {@code 
	 * .useShortMessage("${custom.property.high-priority}")
	 * .useShortMessage(true)
	 * }
	 * </pre>
	 * 
	 * If "custom.property.high-priority" property doesn't exist, then
	 * {@code .useTlvMessagePayload(true)} is used. If
	 * "custom.property.high-priority" property exists, then the value of
	 * "custom.property.high-priority" is used.
	 * 
	 * <pre>
	 * {@code 
	 * .useShortMessage(true)
	 * .useShortMessage("${custom.property.high-priority}")
	 * }
	 * </pre>
	 * 
	 * The value of {@code .useShortMessage(true)} is always used.
	 * 
	 * @param useShortMessage
	 *            enable (true) or disable (false) use of {@code short_message}
	 *            field
	 * @return this instance for fluent chaining
	 */
	public UserDataBuilder useShortMessage(boolean useShortMessage) {
		useShortMessageProps.add(String.valueOf(useShortMessage));
		return this;
	}

	/**
	 * Enable/disable use of {@code short_message} field.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .useShortMessage("true");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .useShortMessage("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param useShortMessage
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public UserDataBuilder useShortMessage(String... useShortMessage) {
		Collections.addAll(useShortMessageProps, useShortMessage);
		return this;
	}

	/**
	 * Enable/disable use of {@code message_payload} optional TLV
	 * (Tag-Value-Length) parameter.
	 * 
	 * <p>
	 * If several properties are already set using
	 * {@link #useTlvMessagePayload(String...)}, then the enabled value is
	 * appended.
	 * 
	 * For example,
	 * 
	 * <pre>
	 * {@code 
	 * .useTlvMessagePayload("${custom.property.high-priority}")
	 * .useTlvMessagePayload(true)
	 * }
	 * </pre>
	 * 
	 * If "custom.property.high-priority" property doesn't exist, then
	 * {@code .useTlvMessagePayload(true)} is used. If
	 * "custom.property.high-priority" property exists, then the value of
	 * "custom.property.high-priority" is used.
	 * 
	 * <pre>
	 * {@code 
	 * .useTlvMessagePayload(true)
	 * .useTlvMessagePayload("${custom.property.high-priority}")
	 * }
	 * </pre>
	 * 
	 * The value of {@code .useTlvMessagePayload(true)} is always used.
	 * 
	 * @param useTlvMessagePayload
	 *            enable (true) or disable (false) use of
	 *            {@code message_payload} optional TLV parameter
	 * @return this instance for fluent chaining
	 */
	public UserDataBuilder useTlvMessagePayload(boolean useTlvMessagePayload) {
		useTlvMessagePayloadProps.add(String.valueOf(useTlvMessagePayload));
		return this;
	}

	/**
	 * Enable/disable use of {@code message_payload} optional TLV
	 * (Tag-Length-Value) parameter.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .useTlvMessagePayload("true");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .useTlvMessagePayload("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param useTlvMessagePayload
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public UserDataBuilder useTlvMessagePayload(String... useTlvMessagePayload) {
		Collections.addAll(useTlvMessagePayloadProps, useTlvMessagePayload);
		return this;
	}

	@Override
	public UserDataPropValues build() {
		PropertyResolver propertyResolver = environmentBuilder.build();
		Boolean useShortMessage = evaluate(useShortMessageProps, propertyResolver, Boolean.class);
		Boolean useTlvMessagePayload = evaluate(useTlvMessagePayloadProps, propertyResolver, Boolean.class);
		return new UserDataPropValues(useShortMessage != null && useShortMessage, useTlvMessagePayload != null && useTlvMessagePayload);
	}

	/**
	 * Simple data class to provide configured values. This is used by parent
	 * builder to affect {@link MessagePreparator} strategy.
	 * 
	 * @author Aurélien Baudet
	 */
	public static class UserDataPropValues {
		private final boolean useShortMessage;
		private final boolean useTlvMessagePayload;

		/**
		 * Initializes with configured values.
		 * 
		 * @param useShortMessage
		 *            true if {@code short_message} field should be used
		 * @param useTlvMessagePayload
		 *            true {@code message_payload} optional parameter should be
		 *            used
		 */
		public UserDataPropValues(boolean useShortMessage, boolean useTlvMessagePayload) {
			super();
			this.useShortMessage = useShortMessage;
			this.useTlvMessagePayload = useTlvMessagePayload;
		}

		/**
		 * @return is the {@code short_message} field should be used
		 */
		public boolean isUseShortMessage() {
			return useShortMessage;
		}

		/**
		 * @return is the {@code message_payload} optional parameter should be
		 *         used
		 */
		public boolean isUseTlvMessagePayload() {
			return useTlvMessagePayload;
		}

	}
}
