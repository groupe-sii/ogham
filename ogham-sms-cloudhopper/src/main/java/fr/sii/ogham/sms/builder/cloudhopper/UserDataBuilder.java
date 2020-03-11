package fr.sii.ogham.sms.builder.cloudhopper;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
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
	private final ConfigurationValueBuilderHelper<UserDataBuilder, Boolean> useShortMessageValueBuilder;
	private final ConfigurationValueBuilderHelper<UserDataBuilder, Boolean> useTlvMessagePayloadValueBuilder;

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
	public UserDataBuilder(CloudhopperBuilder parent, BuildContext buildContext) {
		super(parent);
		useShortMessageValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class, buildContext);
		useTlvMessagePayloadValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class, buildContext);
	}

	/**
	 * Enable/disable use of {@code short_message} field.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #useShortMessage()}.
	 * 
	 * <pre>
	 * .useShortMessage(true)
	 * .useShortMessage()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <pre>
	 * .useShortMessage(true)
	 * .useShortMessage()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * In both cases, {@code useShortMessage(true)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param useShortMessage
	 *            enable (true) or disable (false) use of {@code short_message}
	 *            field
	 * @return this instance for fluent chaining
	 */
	public UserDataBuilder useShortMessage(Boolean useShortMessage) {
		useShortMessageValueBuilder.setValue(useShortMessage);
		return this;
	}

	/**
	 * Enable/disable use of {@code short_message} field to carry text message
	 * (named User Data).
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .useShortMessage()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #useShortMessage(Boolean)} takes
	 * precedence over property values and default value:
	 * 
	 * <pre>
	 * .useShortMessage(false)
	 * .useShortMessage()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * The value {@code false} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<UserDataBuilder, Boolean> useShortMessage() {
		return useShortMessageValueBuilder;
	}

	/**
	 * Enable/disable use of {@code message_payload} optional TLV
	 * (Tag-Value-Length) parameter to carry text message (named User Data).
	 * 
	 * <strong>NOTE:</strong> The TLV optional parameters are available since
	 * SMPP version 3.4.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #useTlvMessagePayload()}.
	 * 
	 * <pre>
	 * .useTlvMessagePayload(false)
	 * .useTlvMessagePayload()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <pre>
	 * .useTlvMessagePayload(false)
	 * .useTlvMessagePayload()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * In both cases, {@code useTlvMessagePayload(false)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param useTlvMessagePayload
	 *            enable (true) or disable (false) use of
	 *            {@code message_payload} optional parameter to carry text
	 *            message
	 * @return this instance for fluent chaining
	 */
	public UserDataBuilder useTlvMessagePayload(Boolean useTlvMessagePayload) {
		useTlvMessagePayloadValueBuilder.setValue(useTlvMessagePayload);
		return this;
	}

	/**
	 * Enable/disable use of {@code message_payload} optional TLV
	 * (Tag-Value-Length) parameter to carry text message (named User Data).
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .useTlvMessagePayload()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #useTlvMessagePayload(Boolean)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .useTlvMessagePayload(false)
	 * .useTlvMessagePayload()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * The value {@code false} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<UserDataBuilder, Boolean> useTlvMessagePayload() {
		return useTlvMessagePayloadValueBuilder;
	}

	@Override
	public UserDataPropValues build() {
		boolean useShort = useShortMessageValueBuilder.getValue(false);
		boolean useTlv = useTlvMessagePayloadValueBuilder.getValue(false);
		return new UserDataPropValues(useShort, useTlv);
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
