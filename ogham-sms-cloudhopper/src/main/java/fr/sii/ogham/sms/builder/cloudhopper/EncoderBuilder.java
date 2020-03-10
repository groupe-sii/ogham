package fr.sii.ogham.sms.builder.cloudhopper;

import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM7;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_ISO_8859_1;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_UCS_2;

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.commons.charset.CharsetUtil;

import fr.sii.ogham.core.builder.BuildContext;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.core.util.PriorizedList;
import fr.sii.ogham.sms.encoder.Encoder;
import fr.sii.ogham.sms.encoder.SupportingEncoder;
import fr.sii.ogham.sms.exception.message.EncodingException;
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.CloudhopperCharsetSupportingEncoder;
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.GuessEncodingEncoder;
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.NamedCharset;

/**
 * Configures text message encoding:
 * 
 * It supports <a href="https://en.wikipedia.org/wiki/GSM_03.38">GSM 03.38</a>
 * standard encodings. It automatically guess the best supported encoding in
 * order to use the minimum octets:
 * <ul>
 * <li>It encodes using GSM 7-bit default alphabet if the message contains only
 * characters defined in the table. Message is packed so the message can have a
 * maximum length of 160 characters. This is enable only if automatic guessing
 * is enabled (using {@link #autoGuess(Boolean)}) and GSM 7-bit is enabled
 * (using {@link #gsm7bitPacked(Integer)}).</li>
 * <li>It encodes using GSM 8-bit data encoding if the message contains only
 * characters that can be encoded on one octet. This is enable only if automatic
 * guessing is enabled (using {@link #autoGuess(Boolean)}) and GSM 8-bit is
 * enabled (using {@link #gsm8bit(Integer)}).</li>
 * <li>It encodes using Latin 1 (ISO-8859-1) data encoding if the message
 * contains only characters that can be encoded on one octet. This is enable
 * only if automatic guessing is enabled (using {@link #autoGuess(Boolean)}) and
 * Latin-1 is enabled (using {@link #latin1(Integer)}).</li>
 * <li>It encodes using UCS-2 encoding if the message contains special
 * characters that can't be encoded on one octet. Each character is encoded on
 * two octets. This is enable only if automatic guessing is enabled (using
 * {@link #autoGuess(Boolean)}) and UCS-2 is enabled (using
 * {@link #ucs2(Integer)}).</li>
 * </ul>
 * 
 * <strong>Automatic guessing enabled</strong>
 * <p>
 * Standard encodings are registered with a priority. The priority is used when
 * auto-guessing is enabled. Each registered encoding is tested against the text
 * message starting with the encoding with the highest priority.
 * </p>
 * 
 * <p>
 * If a priority is set to 0 (or negative number), the encoding is disabled.
 * </p>
 * 
 * <p>
 * Any registered custom encoder is added into the guessing list according to
 * its priority. Use a the highest value to use custom encoder first. To know
 * default priority values for encodings, see
 * {@link DefaultCloudhopperConfigurer}.
 * </p>
 * 
 * <strong>Automatic guessing disabled</strong>
 * <p>
 * Standard encodings are not registered at all.
 * </p>
 * 
 * <p>
 * If custom encoders are registered then only those encoders are used.
 * </p>
 * 
 * <p>
 * If no custom encoders are registered, then default charset encoding is used
 * (see {@link #fallback(String)}).
 * </p>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class EncoderBuilder extends AbstractParent<CloudhopperBuilder> implements Builder<Encoder> {
	protected final StandardEncodingHelper gsm7PackedValueBuilder;
	protected final StandardEncodingHelper gsm8ValueBuilder;
	protected final StandardEncodingHelper ucs2ValueBuilder;
	protected final StandardEncodingHelper latin1ValueBuilder;
	protected final PriorizedList<Encoder> customEncoders;
	protected final ConfigurationValueBuilderHelper<EncoderBuilder, Boolean> autoGuessValueBuilder;
	protected final ConfigurationValueBuilderHelper<EncoderBuilder, String> fallbackCharsetNameValueBuilder;

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
	public EncoderBuilder(CloudhopperBuilder parent, BuildContext buildContext) {
		super(parent);
		gsm7PackedValueBuilder = new StandardEncodingHelper(this, NAME_GSM7, buildContext);
		gsm8ValueBuilder = new StandardEncodingHelper(this, NAME_GSM, buildContext);
		ucs2ValueBuilder = new StandardEncodingHelper(this, NAME_UCS_2, buildContext);
		latin1ValueBuilder = new StandardEncodingHelper(this, NAME_ISO_8859_1, buildContext);
		customEncoders = new PriorizedList<>();
		autoGuessValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class, buildContext);
		fallbackCharsetNameValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class, buildContext);
	}

	/**
	 * Set priority for encoding text messages using GSM 7-bit encoding. GSM
	 * 7-bit encoding and GSM 8-bit encoding use the same character tables. Only
	 * 7 bits are necessary to represents characters. In GSM 8-bit encoding a
	 * leading 0 is added. However, GSM 7-bit encoding is packed. Every
	 * character is "merged" with the next one in order to use more characters
	 * for the same number of octets.
	 * 
	 * <p>
	 * If priority value is 0 or negative, it disables GSM 7-bit encoding.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #gsm7bitPacked()}.
	 * 
	 * <pre>
	 * .gsm7bitPacked(10)
	 * .gsm7bitPacked()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(0)
	 * </pre>
	 * 
	 * <pre>
	 * .gsm7bitPacked(10)
	 * .gsm7bitPacked()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(0)
	 * </pre>
	 * 
	 * In both cases, {@code gsm7bitPacked(10)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param priority
	 *            the priority (highest value means that GSM 7-bit encoding is
	 *            tried first)
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder gsm7bitPacked(Integer priority) {
		gsm7PackedValueBuilder.setValue(priority);
		return this;
	}

	/**
	 * Set priority for encoding text messages using GSM 7-bit encoding. GSM
	 * 7-bit encoding and GSM 8-bit encoding use the same character tables. Only
	 * 7 bits are necessary to represents characters. In GSM 8-bit encoding a
	 * leading 0 is added. However, GSM 7-bit encoding is packed. Every
	 * character is "merged" with the next one in order to use more characters
	 * for the same number of octets.
	 * 
	 * <p>
	 * If priority value is 0 or negative, it disables GSM 7-bit encoding.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .gsm7bitPacked()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(0)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #gsm7bitPacked(Integer)} takes precedence
	 * over property values and default value.
	 * 
	 * <pre>
	 * .gsm7bitPacked(10)
	 * .gsm7bitPacked()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(0)
	 * </pre>
	 * 
	 * The value {@code 10} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<EncoderBuilder, Integer> gsm7bitPacked() {
		return gsm7PackedValueBuilder;
	}

	/**
	 * Set priority for encoding text messages using GSM 8-bit encoding. GSM
	 * 7-bit encoding and GSM 8-bit encoding use the same character tables. Only
	 * 7 bits are necessary to represents characters. In GSM 8-bit encoding a
	 * leading 0 is added.
	 * 
	 * <p>
	 * If priority value is 0 or negative, it disables GSM 8-bit encoding.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #gsm8bit()}.
	 * 
	 * <pre>
	 * .gsm8bit(10)
	 * .gsm8bit()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5)
	 * </pre>
	 * 
	 * <pre>
	 * .gsm8bit(10)
	 * .gsm8bit()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5)
	 * </pre>
	 * 
	 * In both cases, {@code gsm8bit(10)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param priority
	 *            the priority (highest value means that GSM 8-bit encoding is
	 *            tried first)
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder gsm8bit(Integer priority) {
		gsm8ValueBuilder.setValue(priority);
		return this;
	}

	/**
	 * Set priority for encoding text messages using GSM 8-bit encoding. GSM
	 * 7-bit encoding and GSM 8-bit encoding use the same character tables. Only
	 * 7 bits are necessary to represents characters. In GSM 8-bit encoding a
	 * leading 0 is added.
	 * 
	 * <p>
	 * If priority value is 0 or negative, it disables GSM 8-bit encoding.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .gsm8bit()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #gsm8bit(Integer)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .gsm8bit(10)
	 * .gsm8bit()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5)
	 * </pre>
	 * 
	 * The value {@code 10} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<EncoderBuilder, Integer> gsm8bit() {
		return gsm8ValueBuilder;
	}

	/**
	 * Set priority for encoding text messages using UCS-2. UCS-2 uses two
	 * octets per character.
	 * 
	 * <p>
	 * If priority value is 0 or negative, it disables UCS-2 encoding.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #ucs2()}.
	 * 
	 * <pre>
	 * .ucs2(10)
	 * .ucs2()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(2)
	 * </pre>
	 * 
	 * <pre>
	 * .ucs2(10)
	 * .ucs2()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(2)
	 * </pre>
	 * 
	 * In both cases, {@code ucs2(10)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param priority
	 *            the priority (highest value means that UCS-2 encoding is tried
	 *            first)
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder ucs2(Integer priority) {
		ucs2ValueBuilder.setValue(priority);
		return this;
	}

	/**
	 * Set priority for encoding text messages using UCS-2. UCS-2 uses two
	 * octets per character.
	 * 
	 * <p>
	 * If priority value is 0 or negative, it disables UCS-2 encoding.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .ucs2()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(2)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #ucs2(Integer)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .ucs2(10)
	 * .ucs2()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(2)
	 * </pre>
	 * 
	 * The value {@code 10} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<EncoderBuilder, Integer> ucs2() {
		return ucs2ValueBuilder;
	}

	/**
	 * Set priority for encoding text messages using Latin-1 (ISO-8859-1).
	 * 
	 * <p>
	 * If priority value is 0 or negative, it disables Latin-1 encoding.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #latin1()}.
	 * 
	 * <pre>
	 * .latin1(10)
	 * .latin1()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(4)
	 * </pre>
	 * 
	 * <pre>
	 * .latin1(10)
	 * .latin1()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(4)
	 * </pre>
	 * 
	 * In both cases, {@code latin1(10)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param priority
	 *            the priority (highest value means that Latin-1 encoding is
	 *            tried first)
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder latin1(Integer priority) {
		latin1ValueBuilder.setValue(priority);
		return this;
	}

	/**
	 * Set priority for encoding text messages using Latin-1 (ISO-8859-1).
	 * 
	 * <p>
	 * If priority value is 0 or negative, it disables Latin-1 encoding.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .latin1()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(4)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #latin1(Integer)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .latin1(10)
	 * .latin1()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(4)
	 * </pre>
	 * 
	 * The value {@code 10} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<EncoderBuilder, Integer> latin1() {
		return latin1ValueBuilder;
	}

	/**
	 * Register a custom {@link Encoder} with associated priority.
	 * 
	 * <p>
	 * The encoder is registered like standard encoders (see
	 * {@link #gsm7bitPacked(Integer)}, {@link #gsm8bit(Integer)},
	 * {@link #latin1(Integer)}, {@link #ucs2(Integer)}).
	 * 
	 * <p>
	 * If automatic guessing is enabled (see {@link #autoGuess(Boolean)}), the
	 * registered encoder is also used in automatic guessing (according to
	 * priorities).
	 * 
	 * <p>
	 * If automatic guessing is disabled, only custom {@link Encoder}(s) that
	 * are registered using this method are used. They are executed according to
	 * priority order (highest priority is executed first). If encoder fails to
	 * encode (throws {@link EncodingException}) then the next one is tried. The
	 * registered encoder can also implement {@link SupportingEncoder} interface
	 * to indicate if the encoder is able to encode or not the text.
	 * 
	 * <p>
	 * If priority is set to 0 (or negative number), the associated encoder is
	 * disabled.
	 * 
	 * @param encoder
	 *            the encoder to register
	 * @param priority
	 *            the associated priority (the highest priority is executed
	 *            first)
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder register(Encoder encoder, int priority) {
		customEncoders.register(encoder, priority);
		return this;
	}

	/**
	 * Enable/disable automatic guessing of message encoding.
	 * 
	 * <p>
	 * If enables, it automatically guess the best supported encoding in order
	 * to use the minimum octets:
	 * <ul>
	 * <li>It encodes using GSM 7-bit default alphabet if the message contains
	 * only characters defined in the table. Message is packed so the message
	 * can have a maximum length of 160 characters. This is enable only if
	 * automatic guessing is enabled (using {@link #autoGuess(Boolean)}) and GSM
	 * 7-bit is enabled (using {@link #gsm7bitPacked(Integer)}).</li>
	 * <li>It encodes using GSM 8-bit data encoding if the message contains only
	 * characters that can be encoded on one octet. This is enable only if
	 * automatic guessing is enabled (using {@link #autoGuess(Boolean)} and GSM
	 * 8-bit is enabled (using {@link #gsm8bit(Integer)}).</li>
	 * <li>It encodes using Latin 1 (ISO-8859-1) data encoding if the message
	 * contains only characters that can be encoded on one octet. This is enable
	 * only if automatic guessing is enabled (using {@link #autoGuess(Boolean)}
	 * and GSM 8-bit is enabled (using {@link #latin1(Integer)}).</li>
	 * <li>It encodes using UCS-2 encoding if the message contains special
	 * characters that can't be encoded on one octet. Each character is encoded
	 * on two octets. This is enable only if automatic guessing is enabled
	 * (using {@link #autoGuess(Boolean)}) and UCS-2 is enabled (using
	 * {@link #ucs2(Integer)}).</li>
	 * </ul>
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #autoGuess()}.
	 * 
	 * <pre>
	 * .autoGuess(false)
	 * .autoGuess()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <pre>
	 * .autoGuess(false)
	 * .autoGuess()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * In both cases, {@code autoGuess(false)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param enable
	 *            enable or disable automatic guessing of encoding
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder autoGuess(Boolean enable) {
		autoGuessValueBuilder.setValue(enable);
		return this;
	}

	/**
	 * Enable/disable automatic guessing of message encoding.
	 * 
	 * <p>
	 * If enabled, it automatically guess the best supported encoding in order
	 * to use the minimum octets:
	 * <ul>
	 * <li>It encodes using GSM 7-bit default alphabet if the message contains
	 * only characters defined in the table. Message is packed so the message
	 * can have a maximum length of 160 characters. This is enabled only if
	 * automatic guessing is enabled (using {@link #autoGuess(Boolean)}) and GSM
	 * 7-bit is enabled (using {@link #gsm7bitPacked(Integer)}).</li>
	 * <li>It encodes using GSM 8-bit data encoding if the message contains only
	 * characters that can be encoded on one octet. This is enabled only if
	 * automatic guessing is enabled (using {@link #autoGuess(Boolean)} and GSM
	 * 8-bit is enabled (using {@link #gsm8bit(Integer)}).</li>
	 * <li>It encodes using Latin 1 (ISO-8859-1) data encoding if the message
	 * contains only characters that can be encoded on one octet. This is
	 * enabled only if automatic guessing is enabled (using
	 * {@link #autoGuess(Boolean)} and Latin-1 is enabled (using
	 * {@link #latin1(Integer)}).</li>
	 * <li>It encodes using UCS-2 encoding if the message contains special
	 * characters that can't be encoded on one octet. Each character is encoded
	 * on two octets. This is enabled only if automatic guessing is enabled
	 * (using {@link #autoGuess(Boolean)}) and UCS-2 is enabled (using
	 * {@link #ucs2(Integer)}).</li>
	 * </ul>
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .autoGuess()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #autoGuess(Boolean)} takes precedence
	 * over property values and default value.
	 * 
	 * <pre>
	 * .autoGuess(false)
	 * .autoGuess()
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
	public ConfigurationValueBuilder<EncoderBuilder, Boolean> autoGuess() {
		return autoGuessValueBuilder;
	}

	/**
	 * Set which Cloudhopper {@link Charset} should be used if nothing else is
	 * configured.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #fallback()}.
	 * 
	 * <pre>
	 * .fallback(CharsetUtil.NAME_GSM8)
	 * .fallback()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(CharsetUtil.NAME_GSM)
	 * </pre>
	 * 
	 * <pre>
	 * .fallback(CharsetUtil.NAME_GSM8)
	 * .fallback()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(CharsetUtil.NAME_GSM)
	 * </pre>
	 * 
	 * In both cases, {@code fallback(CharsetUtil.NAME_GSM8)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param charsetName
	 *            the name of the charset to use (see {@link CharsetUtil})
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder fallback(String charsetName) {
		fallbackCharsetNameValueBuilder.setValue(charsetName);
		return this;
	}

	/**
	 * Set which Cloudhopper {@link Charset} should be used if nothing else is
	 * configured.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .fallback()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(CharsetUtil.NAME_GSM)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #fallback(String)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .fallback(CharsetUtil.NAME_GSM8)
	 * .fallback()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(CharsetUtil.NAME_GSM)
	 * </pre>
	 * 
	 * The value {@code CharsetUtil.NAME_GSM8} is used regardless of the value
	 * of the properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<EncoderBuilder, String> fallback() {
		return fallbackCharsetNameValueBuilder;
	}

	@Override
	public Encoder build() {
		if (autoGuessEnabled()) {
			return buildAutoGuessEncoder();
		}
		if (customEncodersRegistered()) {
			return new GuessEncodingEncoder(customEncoders.getOrdered());
		}
		String fallbackCharsetName = fallbackCharsetNameValueBuilder.getValue();
		return buildFixedEncoder(fallbackCharsetName == null ? NAME_GSM : fallbackCharsetName);
	}

	protected boolean autoGuessEnabled() {
		return autoGuessValueBuilder.getValue(false);
	}

	private boolean customEncodersRegistered() {
		return !customEncoders.isEmpty();
	}

	private Encoder buildAutoGuessEncoder() {
		PriorizedList<Encoder> registry = new PriorizedList<>();
		registerStandardEncoder(gsm7PackedValueBuilder, registry);
		registerStandardEncoder(gsm8ValueBuilder, registry);
		registerStandardEncoder(latin1ValueBuilder, registry);
		registerStandardEncoder(ucs2ValueBuilder, registry);
		registry.register(customEncoders);
		return new GuessEncodingEncoder(registry.getOrdered());
	}

	private static Encoder buildFixedEncoder(String charsetName) {
		return new CloudhopperCharsetSupportingEncoder(NamedCharset.from(charsetName));
	}

	private static void registerStandardEncoder(StandardEncodingHelper helper, PriorizedList<Encoder> registry) {
		Integer priority = helper.getValue();
		if (priority == null || priority <= 0) {
			return;
		}
		registry.register(new CloudhopperCharsetSupportingEncoder(helper.getCharset()), priority);
	}
}
