package fr.sii.ogham.sms.builder.cloudhopper;

import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM7;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_UCS_2;
import static fr.sii.ogham.core.util.BuilderUtils.evaluate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.commons.charset.CharsetUtil;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.core.util.PriorizedList;
import fr.sii.ogham.core.util.PropertyListOrValue;
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
 * is enabled (using {@link #autoGuess(boolean)} or
 * {@link #autoGuess(String...)}) and GSM 7-bit is enabled (using
 * {@link #gsm7bitPacked(int)}, {@link #gsm7bitPacked(String...)} or
 * {@link #gsm7bitPacked(int, boolean)}).</li>
 * <li>It encodes using GSM 8-bit data encoding if the message contains only
 * characters that can be encoded on one octet. This is enable only if automatic
 * guessing is enabled (using {@link #autoGuess(boolean)} or
 * {@link #autoGuess(String...)}) and GSM 8-bit is enabled (using
 * {@link #gsm8bit(int)}, {@link #gsm8bit(String...)} or
 * {@link #gsm8bit(int, boolean)}).</li>
 * <li>It encodes using Latin 1 (ISO-8859-1) data encoding if the message
 * contains only characters that can be encoded on one octet. This is enable
 * only if automatic guessing is enabled (using {@link #autoGuess(boolean)} or
 * {@link #autoGuess(String...)}) and GSM 8-bit is enabled (using
 * {@link #latin1(int)}, {@link #latin1(String...)} or
 * {@link #latin1(int, boolean)}).</li>
 * <li>It encodes using UCS-2 encoding if the message contains special
 * characters that can't be encoded on one octet. Each character is encoded on
 * two octets. This is enable only if automatic guessing is enabled (using
 * {@link #autoGuess(boolean)} or {@link #autoGuess(String...)}) and UCS-2 is
 * enabled (using {@link #ucs2(int)}, {@link #ucs2(String...)} or
 * {@link #ucs2(int, boolean)}).</li>
 * </ul>
 * 
 * <h3>Automatic guessing enabled</h3>
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
 * <h3>Automatic guessing disabled</h3>
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
 * (see {@link #fallback(String...)}).
 * </p>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class EncoderBuilder extends AbstractParent<CloudhopperBuilder> implements Builder<Encoder> {
	private final EnvironmentBuilder<?> environmentBuilder;
	protected final StandardEncodingHelper gsm7Packed;
	protected final StandardEncodingHelper gsm8;
	protected final StandardEncodingHelper ucs2;
	protected final StandardEncodingHelper latin1;
	protected final PriorizedList<Encoder> customEncoders;
	protected final PropertyListOrValue<Boolean> autoGuess;
	protected final List<String> fallbackCharsetNames;

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
	public EncoderBuilder(CloudhopperBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		gsm7Packed = new StandardEncodingHelper(NAME_GSM7);
		gsm8 = new StandardEncodingHelper(NAME_GSM);
		ucs2 = new StandardEncodingHelper(NAME_UCS_2);
		latin1 = new StandardEncodingHelper(CharsetUtil.NAME_ISO_8859_1);
		customEncoders = new PriorizedList<>();
		autoGuess = new PropertyListOrValue<>();
		fallbackCharsetNames = new ArrayList<>();
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
	 * If several properties are already set using {@link #gsm7bitPacked(String...)},
	 * then the priority value is appended (low priority).
	 * 
	 * For example,
	 * 
	 * <pre>
	 * {@code 
	 * .gsm7bitPacked("${custom.property.high-priority}")
	 * .gsm7bitPacked(1000)
	 * }
	 * </pre>
	 * 
	 * If "custom.property.high-priority" property doesn't exist, then
	 * {@code .gsm7bitPacked(1000)} is used. If "custom.property.high-priority"
	 * property exists, then the value of "custom.property.high-priority" is
	 * used.
	 * 
	 * <pre>
	 * {@code 
	 * .gsm7bitPacked(1000)
	 * .gsm7bitPacked("${custom.property.high-priority}")
	 * }
	 * </pre>
	 * 
	 * The value of {@code .gsm7bitPacked(1000)} is always used.
	 * 
	 * @param priority
	 *            the priority for GSM 7-bit encoding
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder gsm7bitPacked(int priority) {
		gsm7Packed.register(priority);
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
	 * If priority value is 0 (or negative number), the GSM 7-bit encoding is
	 * disabled.
	 * 
	 * <p>
	 * If several properties are already set using
	 * {@link #gsm7bitPacked(String...)} and override parameter is true, the
	 * priority value is added first (highest priority).
	 * 
	 * For example,
	 * 
	 * <pre>
	 * {@code 
	 * .gsm7bitPacked("${custom.property.high-priority}")
	 * .gsm7bitPacked(1000, true)
	 * }
	 * </pre>
	 * 
	 * The value of {@code .gsm7bitPacked(1000, true)} is always used.
	 * 
	 * <p>
	 * If override parameter is false, this method behaves exactly the same as
	 * {@link #gsm7bitPacked(int)}.
	 * 
	 * @param priority
	 *            the priority for GSM 7-bit encoding
	 * @param override
	 *            if true the priority value is added at the beginning, if false
	 *            the priority value is added at the end
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder gsm7bitPacked(int priority, boolean override) {
		gsm7Packed.register(priority, override);
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
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .gsm8bit("1000");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .gsm8bit("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param priority
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder gsm7bitPacked(String... priority) {
		gsm7Packed.register(priority);
		return this;
	}

	/**
	 * Set priority for encoding text messages using GSM 8-bit encoding. GSM
	 * 7-bit encoding and GSM 8-bit encoding use the same character tables. Only
	 * 7 bits are necessary to represents characters. In GSM 8-bit encoding a
	 * leading 0 is added.
	 * 
	 * <p>
	 * If several properties are already set using {@link #gsm8bit(String...)},
	 * then the priority value is appended (low priority).
	 * 
	 * For example,
	 * 
	 * <pre>
	 * {@code 
	 * .gsm8bit("${custom.property.high-priority}")
	 * .gsm8bit(1000)
	 * }
	 * </pre>
	 * 
	 * If "custom.property.high-priority" property doesn't exist, then
	 * {@code .gsm8bit(1000)} is used. If "custom.property.high-priority"
	 * property exists, then the value of "custom.property.high-priority" is
	 * used.
	 * 
	 * <pre>
	 * {@code 
	 * .gsm8bit(1000)
	 * .gsm8bit("${custom.property.high-priority}")
	 * }
	 * </pre>
	 * 
	 * The value of {@code .gsm8bit(1000)} is always used.
	 * 
	 * @param priority
	 *            the priority for GSM 8-bit encoding
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder gsm8bit(int priority) {
		gsm8.register(priority);
		return this;
	}

	/**
	 * Set priority for encoding text messages using GSM 8-bit encoding. GSM
	 * 7-bit encoding and GSM 8-bit encoding use the same character tables. Only
	 * 7 bits are necessary to represents characters. In GSM 8-bit encoding a
	 * leading 0 is added.
	 * 
	 * <p>
	 * If priority value is 0 (or negative number), the GSM 8-bit encoding is
	 * disabled.
	 * 
	 * <p>
	 * If several properties are already set using {@link #gsm8bit(String...)}
	 * and override parameter is true, the priority value is added first
	 * (highest priority).
	 * 
	 * For example,
	 * 
	 * <pre>
	 * {@code 
	 * .gsm8bit("${custom.property.high-priority}")
	 * .gsm8bit(1000, true)
	 * }
	 * </pre>
	 * 
	 * The value of {@code .gsm8bit(1000, true)} is always used.
	 * 
	 * <p>
	 * If override parameter is false, this method behaves exactly the same as
	 * {@link #gsm8bit(int)}.
	 * 
	 * @param priority
	 *            the priority for GSM 8-bit encoding
	 * @param override
	 *            if true the priority value is added at the beginning, if false
	 *            the priority value is added at the end
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder gsm8bit(int priority, boolean override) {
		gsm8.register(priority, override);
		return this;
	}

	/**
	 * Set priority for encoding text messages using GSM 8-bit encoding. GSM
	 * 7-bit encoding and GSM 8-bit encoding use the same character tables. Only
	 * 7 bits are necessary to represents characters. In GSM 8-bit encoding a
	 * leading 0 is added.
	 * 
	 * <p>
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .gsm8bit("1000");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .gsm8bit("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param priority
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder gsm8bit(String... priority) {
		gsm8.register(priority);
		return this;
	}

	/**
	 * Set priority for encoding text messages using UCS-2. UCS-2 uses two
	 * octets per character.
	 * 
	 * <p>
	 * If several properties are already set using {@link #ucs2(String...)},
	 * then the priority value is appended (low priority).
	 * 
	 * For example,
	 * 
	 * <pre>
	 * {@code 
	 * .ucs2("${custom.property.high-priority}")
	 * .ucs2(1000)
	 * }
	 * </pre>
	 * 
	 * If "custom.property.high-priority" property doesn't exist, then
	 * {@code .ucs2(1000)} is used. If "custom.property.high-priority" property
	 * exists, then the value of "custom.property.high-priority" is used.
	 * 
	 * <pre>
	 * {@code 
	 * .ucs2(1000)
	 * .ucs2("${custom.property.high-priority}")
	 * }
	 * </pre>
	 * 
	 * The value of {@code .ucs2(1000)} is always used.
	 * 
	 * @param priority
	 *            the priority for UCS-2 encoding
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder ucs2(int priority) {
		ucs2.register(priority);
		return this;
	}

	/**
	 * Set priority for encoding text messages using UCS-2. UCS-2 uses two
	 * octets per character.
	 * 
	 * <p>
	 * If priority value is 0 (or negative number), the UCS-2 is disabled.
	 * 
	 * <p>
	 * If several properties are already set using {@link #ucs2(String...)} and
	 * override parameter is true, the priority value is added first (highest
	 * priority).
	 * 
	 * For example,
	 * 
	 * <pre>
	 * {@code 
	 * .ucs2("${custom.property.high-priority}")
	 * .ucs2(1000, true)
	 * }
	 * </pre>
	 * 
	 * The value of {@code .ucs2(1000, true)} is always used.
	 * 
	 * <p>
	 * If override parameter is false, this method behaves exactly the same as
	 * {@link #ucs2(int)}.
	 * 
	 * @param priority
	 *            the priority for UCS-2 encoding
	 * @param override
	 *            if true the priority value is added at the beginning, if false
	 *            the priority value is added at the end
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder ucs2(int priority, boolean override) {
		ucs2.register(priority, override);
		return this;
	}

	/**
	 * Set priority for encoding text messages using UCS-2. UCS-2 uses two
	 * octets per character.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .ucs2("1000");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .ucs2("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param priority
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder ucs2(String... priority) {
		ucs2.register(priority);
		return this;
	}

	/**
	 * Set priority for encoding text messages using Latin 1 (ISO-8859-1).
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .latin1("1000");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .latin1("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param priority
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder latin1(String... priority) {
		latin1.register(priority);
		return this;
	}

	/**
	 * Set priority for encoding text messages using Latin 1 (ISO-8859-1).
	 * 
	 * <p>
	 * If several properties are already set using {@link #latin1(String...)},
	 * then the priority value is appended (low priority).
	 * 
	 * For example,
	 * 
	 * <pre>
	 * {@code 
	 * .latin1("${custom.property.high-priority}")
	 * .latin1(1000)
	 * }
	 * </pre>
	 * 
	 * If "custom.property.high-priority" property doesn't exist, then
	 * {@code .latin1(1000)} is used. If "custom.property.high-priority"
	 * property exists, then the value of "custom.property.high-priority" is
	 * used.
	 * 
	 * <pre>
	 * {@code 
	 * .latin1(1000)
	 * .latin1("${custom.property.high-priority}")
	 * }
	 * </pre>
	 * 
	 * The value of {@code .latin1(1000)} is always used.
	 * 
	 * @param priority
	 *            the priority for Latin 1 (ISO-8859-1) encoding
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder latin1(int priority) {
		latin1.register(priority);
		return this;
	}

	/**
	 * Set priority for encoding text messages using Latin 1 (ISO-8859-1).
	 * 
	 * <p>
	 * If priority value is 0 (or negative number), the Latin 1 (ISO-8859-1) is
	 * disabled.
	 * 
	 * <p>
	 * If several properties are already set using {@link #latin1(String...)}
	 * and override parameter is true, the priority value is added first
	 * (highest priority).
	 * 
	 * For example,
	 * 
	 * <pre>
	 * {@code 
	 * .latin1("${custom.property.high-priority}")
	 * .latin1(1000, true)
	 * }
	 * </pre>
	 * 
	 * The value of {@code .latin1(1000, true)} is always used.
	 * 
	 * <p>
	 * If override parameter is false, this method behaves exactly the same as
	 * {@link #latin1(int)}.
	 * 
	 * @param priority
	 *            the priority for Latin 1 (ISO-8859-1) encoding
	 * @param override
	 *            if true the priority value is added at the beginning, if false
	 *            the priority value is added at the end
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder latin1(int priority, boolean override) {
		latin1.register(priority, override);
		return this;
	}

	/**
	 * Register a custom {@link Encoder} with associated priority.
	 * 
	 * <p>
	 * The encoder is registered like standard encoders (see
	 * {@link #gsm7bitPacked(String...)}, {@link #gsm8bit(String...)},
	 * {@link #latin1(String...)}, {@link #ucs2(String...)}).
	 * 
	 * <p>
	 * If automatic guessing is enabled (see {@link #autoGuess(String...)}), the
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
	 * automatic guessing is enabled (using {@link #autoGuess(boolean)} or
	 * {@link #autoGuess(String...)}) and GSM 7-bit is enabled (using
	 * {@link #gsm7bitPacked(int)}, {@link #gsm7bitPacked(String...)} or
	 * {@link #gsm7bitPacked(int, boolean)}).</li>
	 * <li>It encodes using GSM 8-bit data encoding if the message contains only
	 * characters that can be encoded on one octet. This is enable only if
	 * automatic guessing is enabled (using {@link #autoGuess(boolean)} or
	 * {@link #autoGuess(String...)}) and GSM 8-bit is enabled (using
	 * {@link #gsm8bit(int)}, {@link #gsm8bit(String...)} or
	 * {@link #gsm8bit(int, boolean)}).</li>
	 * <li>It encodes using Latin 1 (ISO-8859-1) data encoding if the message
	 * contains only characters that can be encoded on one octet. This is enable
	 * only if automatic guessing is enabled (using {@link #autoGuess(boolean)}
	 * or {@link #autoGuess(String...)}) and GSM 8-bit is enabled (using
	 * {@link #latin1(int)}, {@link #latin1(String...)} or
	 * {@link #latin1(int, boolean)}).</li>
	 * <li>It encodes using UCS-2 encoding if the message contains special
	 * characters that can't be encoded on one octet. Each character is encoded
	 * on two octets. This is enable only if automatic guessing is enabled
	 * (using {@link #autoGuess(boolean)} or {@link #autoGuess(String...)}) and
	 * UCS-2 is enabled (using {@link #ucs2(int)}, {@link #ucs2(String...)} or
	 * {@link #ucs2(int, boolean)}).</li>
	 * </ul>
	 * 
	 * 
	 * <p>
	 * If several properties are already set using
	 * {@link #autoGuess(String...)}, then the enabled value is appended (low
	 * priority).
	 * 
	 * For example,
	 * 
	 * <pre>
	 * {@code 
	 * .autoGuess("${custom.property.high-priority}")
	 * .autoGuess(true)
	 * }
	 * </pre>
	 * 
	 * If "custom.property.high-priority" property doesn't exist, then
	 * {@code .autoGuess(true)} is used. If "custom.property.high-priority"
	 * property exists, then the value of "custom.property.high-priority" is
	 * used.
	 * 
	 * <pre>
	 * {@code 
	 * .autoGuess(true)
	 * .autoGuess("${custom.property.high-priority}")
	 * }
	 * </pre>
	 * 
	 * The value of {@code .autoGuess(true)} is always used.
	 * 
	 * @param enable
	 *            enable (true) or disable (false) automatic encoding guessing
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder autoGuess(boolean enable) {
		autoGuess.register(enable);
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
	 * automatic guessing is enabled (using {@link #autoGuess(boolean)} or
	 * {@link #autoGuess(String...)}) and GSM 7-bit is enabled (using
	 * {@link #gsm7bitPacked(int)}, {@link #gsm7bitPacked(String...)} or
	 * {@link #gsm7bitPacked(int, boolean)}).</li>
	 * <li>It encodes using GSM 8-bit data encoding if the message contains only
	 * characters that can be encoded on one octet. This is enable only if
	 * automatic guessing is enabled (using {@link #autoGuess(boolean)} or
	 * {@link #autoGuess(String...)}) and GSM 8-bit is enabled (using
	 * {@link #gsm8bit(int)}, {@link #gsm8bit(String...)} or
	 * {@link #gsm8bit(int, boolean)}).</li>
	 * <li>It encodes using Latin 1 (ISO-8859-1) data encoding if the message
	 * contains only characters that can be encoded on one octet. This is enable
	 * only if automatic guessing is enabled (using {@link #autoGuess(boolean)}
	 * or {@link #autoGuess(String...)}) and GSM 8-bit is enabled (using
	 * {@link #latin1(int)}, {@link #latin1(String...)} or
	 * {@link #latin1(int, boolean)}).</li>
	 * <li>It encodes using UCS-2 encoding if the message contains special
	 * characters that can't be encoded on one octet. Each character is encoded
	 * on two octets. This is enable only if automatic guessing is enabled
	 * (using {@link #autoGuess(boolean)} or {@link #autoGuess(String...)}) and
	 * UCS-2 is enabled (using {@link #ucs2(int)}, {@link #ucs2(String...)} or
	 * {@link #ucs2(int, boolean)}).</li>
	 * </ul>
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .autoGuess("true");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .autoGuess("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param enable
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder autoGuess(String... enable) {
		autoGuess.register(enable);
		return this;
	}

	/**
	 * Set which Cloudhopper {@link Charset} should be used if nothing else is
	 * configured.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .fallback("GSM");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .fallback("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param charsetName
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public EncoderBuilder fallback(String... charsetName) {
		Collections.addAll(fallbackCharsetNames, charsetName);
		return this;
	}

	@Override
	public Encoder build() {
		PropertyResolver propertyResolver = environmentBuilder.build();
		if (autoGuessEnabled(propertyResolver)) {
			return buildAutoGuessEncoder(propertyResolver);
		}
		if (customEncodersRegistered()) {
			return new GuessEncodingEncoder(customEncoders.getOrdered());
		}
		String fallbackCharsetName = evaluate(fallbackCharsetNames, propertyResolver, String.class);
		return buildFixedEncoder(fallbackCharsetName == null ? NAME_GSM : fallbackCharsetName);
	}

	protected boolean autoGuessEnabled(PropertyResolver propertyResolver) {
		Boolean autoGuessValue = autoGuess.evaluate(propertyResolver, Boolean.class);
		return autoGuessValue != null && autoGuessValue;
	}

	private boolean customEncodersRegistered() {
		return !customEncoders.isEmpty();
	}

	private Encoder buildAutoGuessEncoder(PropertyResolver propertyResolver) {
		PriorizedList<Encoder> registry = new PriorizedList<>();
		registerStandardEncoder(propertyResolver, gsm7Packed, registry);
		registerStandardEncoder(propertyResolver, gsm8, registry);
		registerStandardEncoder(propertyResolver, latin1, registry);
		registerStandardEncoder(propertyResolver, ucs2, registry);
		registry.register(customEncoders);
		return new GuessEncodingEncoder(registry.getOrdered());
	}

	private static Encoder buildFixedEncoder(String charsetName) {
		return new CloudhopperCharsetSupportingEncoder(NamedCharset.from(charsetName));
	}

	private static void registerStandardEncoder(PropertyResolver propertyResolver, StandardEncodingHelper helper, PriorizedList<Encoder> registry) {
		Integer priority = BuilderUtils.evaluate(helper.getProperties(), propertyResolver, Integer.class);
		if (priority == null || priority <= 0) {
			return;
		}
		registry.register(new CloudhopperCharsetSupportingEncoder(helper.getCharset()), priority);
	}

	protected static class StandardEncodingHelper extends PropertyListOrValue<Integer> {
		private final String charsetName;

		public StandardEncodingHelper(String charsetName) {
			super();
			this.charsetName = charsetName;
		}

		public NamedCharset getCharset() {
			return NamedCharset.from(charsetName);
		}
	}
}
