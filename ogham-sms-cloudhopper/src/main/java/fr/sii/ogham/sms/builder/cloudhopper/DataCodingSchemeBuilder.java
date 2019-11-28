package fr.sii.ogham.sms.builder.cloudhopper;

import static fr.sii.ogham.core.util.BuilderUtils.evaluate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.commons.gsm.DataCoding;
import com.cloudhopper.smpp.SmppConstants;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.util.BuilderUtils;
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
	private final EnvironmentBuilder<?> environmentBuilder;
	private final Function<PropertyResolver, Byte> interfaceVersionProvider;
	private final List<String> autoProps;
	private final List<String> valueProps;
	private DataCodingProvider custom;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param environmentBuilder
	 *            the configuration for property resolution and evaluation
	 * @param interfaceVersionProvider
	 *            A function used to retrieve the value of the interface
	 *            version. This is needed when {@link #auto(String...)} mode is
	 *            enabled.
	 */
	public DataCodingSchemeBuilder(CloudhopperBuilder parent, EnvironmentBuilder<?> environmentBuilder, Function<PropertyResolver, Byte> interfaceVersionProvider) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		this.interfaceVersionProvider = interfaceVersionProvider;
		this.autoProps = new ArrayList<>();
		this.valueProps = new ArrayList<>();
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
	 * <h3>SMPP v3.3</h3>
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
	 * <h3>SMPP v3.4+</h3>
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
	 * <h3>Behavior</h3>
	 * <h4>Standalone</h4>
	 * <p>
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .enable("true");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .enable("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * <h4>Custom {@link DataCodingProvider} also configured</h4>
	 * <p>
	 * If a custom {@link DataCodingProvider} instance is registered, this
	 * instance is tried first (see {@link #custom(DataCodingProvider)} for more
	 * information). The automatic behavior is applied after if returned value
	 * of custom {@link DataCodingProvider} is {@code null}.
	 * 
	 * <h4>Fixed value also configured</h4>
	 * <p>
	 * If a fixed value is configured then it preempts any other configuration (
	 * {@link #auto(String...)} and {@link #custom(DataCodingProvider)} are not
	 * used at all).
	 * </p>
	 * 
	 * @param enable
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public DataCodingSchemeBuilder auto(String... enable) {
		Collections.addAll(autoProps, enable);
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
	 * <h3>SMPP v3.3</h3>
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
	 * <h3>SMPP v3.4+</h3>
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
	 * <h3>Behavior</h3>
	 * <h4>Standalone</h4>
	 * <p>
	 * If several properties are already set using {@link #auto(String...)},
	 * then the enabled value is appended.
	 * 
	 * For example,
	 * 
	 * <pre>
	 * {@code
	 * .auto("${custom.property.high-priority}")
	 * .auto(true)
	 * }
	 * </pre>
	 * 
	 * If "custom.property.high-priority" property doesn't exist, then
	 * {@code .auto(true)} is used. If "custom.property.high-priority" property
	 * exists, then the value of "custom.property.high-priority" is used.
	 * 
	 * <pre>
	 * {@code
	 * .auto(true)
	 * .auto("${custom.property.high-priority}")
	 * }
	 * </pre>
	 * 
	 * The value of {@code .auto(true)} is always used.
	 * 
	 * <h4>Custom {@link DataCodingProvider} also configured</h4>
	 * <p>
	 * If a custom {@link DataCodingProvider} instance is registered, this
	 * instance is tried first (see {@link #custom(DataCodingProvider)} for more
	 * information). The automatic behavior is applied after if returned value
	 * of custom {@link DataCodingProvider} is {@code null}.
	 * 
	 * <h4>Fixed value also configured</h4>
	 * <p>
	 * If a fixed value is configured then it preempts any other configuration (
	 * {@link #auto(String...)} and {@link #custom(DataCodingProvider)} are not
	 * used at all).
	 * </p>
	 * 
	 * @param enable
	 *            enable (true) or disable (false) message splitting
	 * @return this instance for fluent chaining
	 */
	public DataCodingSchemeBuilder auto(boolean enable) {
		Collections.addAll(autoProps, String.valueOf(enable));
		return this;
	}

	/**
	 * Use the same Data Coding Scheme value for all messages.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .value((byte) 0x10);
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .value("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param dataCoding
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public DataCodingSchemeBuilder value(String... dataCoding) {
		Collections.addAll(valueProps, dataCoding);
		return this;
	}

	/**
	 * Use the same Data Coding Scheme value for all messages.
	 * 
	 * <p>
	 * If several properties are already set using {@link #value(String...)},
	 * then the Data Coding Scheme value is appended.
	 * 
	 * For example,
	 * 
	 * <pre>
	 * {@code 
	 * .value("${custom.property.high-priority}")
	 * .value((byte) 0x10)
	 * }
	 * </pre>
	 * 
	 * If "custom.property.high-priority" property doesn't exist, then
	 * {@code .value((byte) 0x10)} is used. If "custom.property.high-priority"
	 * property exists, then the value of "custom.property.high-priority" is
	 * used.
	 * 
	 * <pre>
	 * {@code 
	 * .value((byte) 0x10)
	 * .value("${custom.property.high-priority}")
	 * }
	 * </pre>
	 * 
	 * The value of {@code .value((byte) 0x10)} is always used.
	 * 
	 * @param dataCoding
	 *            the Data Coding Scheme value to use for all messages
	 * @return this instance for fluent chaining
	 */
	public DataCodingSchemeBuilder value(byte dataCoding) {
		Collections.addAll(valueProps, String.valueOf(dataCoding));
		return this;
	}

	/**
	 * Register a custom strategy to determine Data Coding Scheme value.
	 * 
	 * <p>
	 * Automatic behavior (see {@link #auto(String...)} is still active but
	 * custom strategy is executed first. As the custom
	 * {@link DataCodingProvider} can return {@code null}, the automatic
	 * behavior is executed in that case.
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
	public DataCodingProvider build() {
		PropertyResolver propertyResolver = environmentBuilder.build();
		Byte dataCodingValue = BuilderUtils.evaluate(valueProps, propertyResolver, Byte.class);
		if (dataCodingValue != null) {
			return new FixedByteValueDataCodingProvider(dataCodingValue);
		}
		FirstSupportingDataCodingProvider firstSupporting = new FirstSupportingDataCodingProvider();
		if (custom != null) {
			firstSupporting.register(custom);
		}
		Boolean auto = evaluate(autoProps, propertyResolver, Boolean.class);
		if (auto != null && auto) {
			registerAuto(propertyResolver, firstSupporting);
		}
		return firstSupporting;
	}

	private void registerAuto(PropertyResolver propertyResolver, FirstSupportingDataCodingProvider firstSupporting) {
		Byte interfaceVersion = interfaceVersionProvider.apply(propertyResolver);
		if (interfaceVersion == SmppConstants.VERSION_3_3) {
			firstSupporting.register(new CharsetMapToGeneralGroupDataCodingProvider());
			return;
		}
		// 3.4+
		firstSupporting.register(new CharsetMapToCharacterEncodingGroupDataCodingProvider());
	}

}
