package fr.sii.ogham.sms.builder.cloudhopper;

import java.util.function.Supplier;

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.commons.gsm.DataCoding;
import com.cloudhopper.smpp.SmppConstants;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.CharsetMapToCharacterEncodingGroupDataCodingProvider;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.CharsetMapToGeneralGroupDataCodingProvider;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.DataCodingProvider;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.FirstSupportingDataCodingProvider;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.FixedByteValueDataCodingProvider;

/**
 * Data Coding Scheme is a one-octet field in Short Messages (SM) and Cell
 * Broadcast Messages (CB) which carries a basic information how the recipient
 * handset should process the received message. The information includes:
 * <ul>
 * <li>the character set or message coding which determines the encoding of the
 * message user data</li>
 * <li>the message class which determines to which component of the Mobile
 * Station (MS) or User Equipment (UE) should be the message delivered</li>
 * <li>the request to automatically delete the message after reading</li>
 * <li>the state of flags indicating presence of unread voicemail, fax, e-mail
 * or other messages</li>
 * <li>the indication that the message content is compressed</li>
 * <li>the language of the cell broadcast message</li>
 * </ul>
 * The field is described in 3GPP 23.040 and 3GPP 23.038 under the name TP-DCS
 * (see <a href=
 * "https://en.wikipedia.org/wiki/Data_Coding_Scheme#SMS_Data_Coding_Scheme">SMS
 * Data Coding Scheme</a>).
 * 
 * <p>
 * Configures how Cloudhopper determines the Data Coding Scheme to use:
 * <ul>
 * <li>Automatic mode:
 * <ul>
 * <li>If SMPP v3.3 is used then
 * {@link CharsetMapToGeneralGroupDataCodingProvider} generates a
 * {@link DataCoding} with
 * {@link DataCoding#createGeneralGroup(byte, Byte, boolean)}</li>
 * <li>If SMPP v3.4+ is used then
 * {@link CharsetMapToCharacterEncodingGroupDataCodingProvider} generates a
 * {@link DataCoding} with
 * {@link DataCoding#createCharacterEncodingGroup(byte)}</li>
 * </ul>
 * </li>
 * <li>Allow registration of custom {@link DataCodingProvider}</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 */
public class DataCodingSchemeBuilder extends AbstractParent<CloudhopperBuilder> implements Builder<DataCodingProvider> {
	private final BuildContext buildContext;
	private final Supplier<Byte> interfaceVersionProvider;
	private final ConfigurationValueBuilderHelper<DataCodingSchemeBuilder, Boolean> autoValueBuilder;
	private final ConfigurationValueBuilderHelper<DataCodingSchemeBuilder, Byte> dcsValueBuilder;
	private DataCodingProvider custom;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for registering instances and property evaluation
	 * @param interfaceVersionProvider
	 *            A function used to retrieve the value of the interface
	 *            version. This is needed when {@link #auto(Boolean)} mode is
	 *            enabled.
	 */
	public DataCodingSchemeBuilder(CloudhopperBuilder parent, BuildContext buildContext, Supplier<Byte> interfaceVersionProvider) {
		super(parent);
		this.buildContext = buildContext;
		this.interfaceVersionProvider = interfaceVersionProvider;
		this.autoValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class, buildContext);
		this.dcsValueBuilder = new ConfigurationValueBuilderHelper<>(this, Byte.class, buildContext);
	}

	/**
	 * * Enable/disable automatic mode based on SMPP interface version.
	 * 
	 * <p>
	 * {@link DataCodingProvider} implementation is selected based on SMPP
	 * interface version. SMPP v3.3 Data Coding Scheme values are defined in
	 * <a href=
	 * "https://en.wikipedia.org/wiki/Data_Coding_Scheme#SMS_Data_Coding_Scheme">SMS
	 * Data Coding Scheme</a>. SMPP 3.4 introduced a new list of data_coding
	 * values (<a href=
	 * "https://en.wikipedia.org/wiki/Short_Message_Peer-to-Peer#PDU_body">PDU
	 * body</a>).
	 * </p>
	 * 
	 * <strong>SMPP v3.3</strong>
	 * <p>
	 * The text message is encoded using {@link Charset}. According to that
	 * charset, the Data Coding Scheme is determined using the <strong>General
	 * Data Coding group</strong> table. Therefore, a simple mapping is applied:
	 * <ul>
	 * <li>{@link CharsetUtil#NAME_GSM7} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
	 * <li>{@link CharsetUtil#NAME_PACKED_GSM} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
	 * <li>{@link CharsetUtil#NAME_GSM} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_8BIT}</li>
	 * <li>{@link CharsetUtil#NAME_GSM8} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_8BIT}</li>
	 * <li>{@link CharsetUtil#NAME_UCS_2} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_UCS2}</li>
	 * </ul>
	 * 
	 * 
	 * <strong>SMPP v3.4+</strong>
	 * <p>
	 * The text message is encoded using {@link Charset}. According to that
	 * charset, the Data Coding Scheme is determined using only the
	 * <strong>Alphabet</strong> table. Therefore, a simple mapping is applied:
	 * <ul>
	 * <li>{@link CharsetUtil#NAME_GSM7} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
	 * <li>{@link CharsetUtil#NAME_PACKED_GSM} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
	 * <li>{@link CharsetUtil#NAME_GSM} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_8BIT}</li>
	 * <li>{@link CharsetUtil#NAME_GSM8} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_8BIT}</li>
	 * <li>{@link CharsetUtil#NAME_ISO_8859_1} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_LATIN1}</li>
	 * <li>{@link CharsetUtil#NAME_UCS_2} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_UCS2}</li>
	 * </ul>
	 * 
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #auto()}.
	 * 
	 * <pre>
	 * .auto(false)
	 * .auto()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <pre>
	 * .auto(false)
	 * .auto()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * In both cases, {@code auto(false)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param enable
	 *            enable or disable automatic Data Coding Scheme detection
	 * @return this instance for fluent chaining
	 */
	public DataCodingSchemeBuilder auto(Boolean enable) {
		autoValueBuilder.setValue(enable);
		return this;
	}

	/**
	 * Enable/disable automatic mode based on SMPP interface version.
	 * 
	 * <p>
	 * {@link DataCodingProvider} implementation is selected based on SMPP
	 * interface version. SMPP v3.3 Data Coding Scheme values are defined in
	 * <a href=
	 * "https://en.wikipedia.org/wiki/Data_Coding_Scheme#SMS_Data_Coding_Scheme">SMS
	 * Data Coding Scheme</a>. SMPP 3.4 introduced a new list of data_coding
	 * values (<a href=
	 * "https://en.wikipedia.org/wiki/Short_Message_Peer-to-Peer#PDU_body">PDU
	 * body</a>).
	 * </p>
	 * 
	 * <strong>SMPP v3.3</strong>
	 * <p>
	 * The text message is encoded using {@link Charset}. According to that
	 * charset, the Data Coding Scheme is determined using the <strong>General
	 * Data Coding group</strong> table. Therefore, a simple mapping is applied:
	 * <ul>
	 * <li>{@link CharsetUtil#NAME_GSM7} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
	 * <li>{@link CharsetUtil#NAME_PACKED_GSM} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
	 * <li>{@link CharsetUtil#NAME_GSM} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_8BIT}</li>
	 * <li>{@link CharsetUtil#NAME_GSM8} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_8BIT}</li>
	 * <li>{@link CharsetUtil#NAME_UCS_2} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_UCS2}</li>
	 * </ul>
	 * 
	 * 
	 * <strong>SMPP v3.4+</strong>
	 * <p>
	 * The text message is encoded using {@link Charset}. According to that
	 * charset, the Data Coding Scheme is determined using only the
	 * <strong>Alphabet</strong> table. Therefore, a simple mapping is applied:
	 * <ul>
	 * <li>{@link CharsetUtil#NAME_GSM7} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
	 * <li>{@link CharsetUtil#NAME_PACKED_GSM} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
	 * <li>{@link CharsetUtil#NAME_GSM} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_8BIT}</li>
	 * <li>{@link CharsetUtil#NAME_GSM8} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_8BIT}</li>
	 * <li>{@link CharsetUtil#NAME_ISO_8859_1} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_LATIN1}</li>
	 * <li>{@link CharsetUtil#NAME_UCS_2} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_UCS2}</li>
	 * </ul>
	 * 
	 * 
	 * <strong>Custom {@link DataCodingProvider} also configured</strong>
	 * <p>
	 * If a custom {@link DataCodingProvider} instance is registered, this
	 * instance is tried first (see {@link #custom(DataCodingProvider)} for more
	 * information). The automatic behavior is applied after if returned value
	 * of custom {@link DataCodingProvider} is {@code null}.
	 * 
	 * <strong>Fixed value also configured</strong>
	 * <p>
	 * If a fixed value is configured then it preempts any other configuration (
	 * {@link #auto(Boolean)} and {@link #custom(DataCodingProvider)} are not
	 * used at all).
	 * 
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .auto()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #auto(Boolean)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .auto(false)
	 * .auto()
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
	public ConfigurationValueBuilder<DataCodingSchemeBuilder, Boolean> auto() {
		return autoValueBuilder;
	}

	/**
	 * Use the same Data Coding Scheme value for all messages.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #value()}.
	 * 
	 * <pre>
	 * .value((byte) 0x10)
	 * .value()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue((byte) 0)
	 * </pre>
	 * 
	 * <pre>
	 * .value((byte) 0x10)
	 * .value()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue((byte) 0)
	 * </pre>
	 * 
	 * In both cases, {@code value((byte) 0x10)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param value
	 *            the Data Coding Scheme value for all messages
	 * @return this instance for fluent chaining
	 */
	public DataCodingSchemeBuilder value(Byte value) {
		this.dcsValueBuilder.setValue(value);
		return this;
	}

	/**
	 * Use the same Data Coding Scheme value for all messages.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .value()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue((byte) 0)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #value(Byte)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .value((byte) 0x10)
	 * .value()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue((byte) 0)
	 * </pre>
	 * 
	 * The value {@code (byte) 0x10} is used regardless of the value of the
	 * properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<DataCodingSchemeBuilder, Byte> value() {
		return dcsValueBuilder;
	}

	/**
	 * Register a custom strategy to determine Data Coding Scheme value.
	 * 
	 * <p>
	 * Automatic behavior (see {@link #auto(Boolean)} is still active but custom
	 * strategy is executed first. As the custom {@link DataCodingProvider} can
	 * return {@code null}, the automatic behavior is executed in that case.
	 * 
	 * <p>
	 * If {@code null} value is provided, custom strategy is disabled.
	 * 
	 * @param custom
	 *            the Strategy to determine Data Coding Scheme value.
	 * @return this instance for fluent chaining
	 */
	public DataCodingSchemeBuilder custom(DataCodingProvider custom) {
		this.custom = custom;
		return this;
	}

	@Override
	@SuppressWarnings("squid:S5411")
	public DataCodingProvider build() {
		Byte dataCodingValue = dcsValueBuilder.getValue();
		if (dataCodingValue != null) {
			return buildContext.register(new FixedByteValueDataCodingProvider(dataCodingValue));
		}
		FirstSupportingDataCodingProvider firstSupporting = buildContext.register(new FirstSupportingDataCodingProvider());
		if (custom != null) {
			firstSupporting.register(custom);
		}
		if (autoValueBuilder.getValue(false)) {
			registerAuto(firstSupporting);
		}
		return firstSupporting;
	}

	private void registerAuto(FirstSupportingDataCodingProvider firstSupporting) {
		Byte interfaceVersion = interfaceVersionProvider.get();
		if (interfaceVersion == SmppConstants.VERSION_3_3) {
			firstSupporting.register(buildContext.register(new CharsetMapToGeneralGroupDataCodingProvider(false)));
			return;
		}
		// 3.4+
		firstSupporting.register(buildContext.register(new CharsetMapToCharacterEncodingGroupDataCodingProvider(false)));
	}

}
