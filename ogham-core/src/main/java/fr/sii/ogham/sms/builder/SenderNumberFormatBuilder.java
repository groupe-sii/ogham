package fr.sii.ogham.sms.builder;

import fr.sii.ogham.core.builder.BuildContext;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.addressing.AddressedPhoneNumber;
import fr.sii.ogham.sms.message.addressing.translator.AlphanumericCodeNumberFormatHandler;
import fr.sii.ogham.sms.message.addressing.translator.CompositePhoneNumberTranslator;
import fr.sii.ogham.sms.message.addressing.translator.DefaultHandler;
import fr.sii.ogham.sms.message.addressing.translator.InternationalNumberFormatHandler;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;
import fr.sii.ogham.sms.message.addressing.translator.ShortCodeNumberFormatHandler;

/**
 * Defines which standard conversions may be applied on the phone number to
 * convert it from a {@link PhoneNumber} to an {@link AddressedPhoneNumber}.
 * 
 * @author Aurélien Baudet
 *
 */
public class SenderNumberFormatBuilder extends AbstractParent<SenderNumberBuilder> implements Builder<PhoneNumberTranslator> {
	private final ConfigurationValueBuilderHelper<SenderNumberFormatBuilder, Boolean> enableAlphanumericValueBuilder;
	private final ConfigurationValueBuilderHelper<SenderNumberFormatBuilder, Boolean> enableShortCodeValueBuilder;
	private final ConfigurationValueBuilderHelper<SenderNumberFormatBuilder, Boolean> enableInternationalValueBuilder;

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
	public SenderNumberFormatBuilder(SenderNumberBuilder parent, BuildContext buildContext) {
		super(parent);
		enableAlphanumericValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class, buildContext);
		enableShortCodeValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class, buildContext);
		enableInternationalValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class, buildContext);
	}

	/**
	 * Enable/disable alphanumeric code conversion: if the sender address is
	 * alphanumeric (contains both letters and numbers) or non-numeric, TON is
	 * set to 5 and NPI to 0.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #alphanumericCode()}.
	 * 
	 * <pre>
	 * .alphanumericCode(false)
	 * .alphanumericCode()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <pre>
	 * .alphanumericCode(false)
	 * .alphanumericCode()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * In both cases, {@code alphanumericCode(false)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param enable
	 *            enable or disable alphanumeric code convertion
	 * @return this instance for fluent chaining
	 */
	public SenderNumberFormatBuilder alphanumericCode(Boolean enable) {
		enableAlphanumericValueBuilder.setValue(enable);
		return this;
	}

	/**
	 * Enable/disable alphanumeric code conversion: if the sender address is
	 * alphanumeric (contains both letters and numbers) or non-numeric, TON is
	 * set to 5 and NPI to 0.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .alphanumericCode()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #alphanumericCode(Boolean)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .alphanumericCode(true)
	 * .alphanumericCode()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * The value {@code true} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<SenderNumberFormatBuilder, Boolean> alphanumericCode() {
		return enableAlphanumericValueBuilder;
	}

	/**
	 * Enable/disable short code conversion: if the sender address is a short
	 * code, TON is set to 3, and NPI is set to 0. A number is considered to be
	 * a short code if the length of the number is 5 digits or less.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #shortCode()}.
	 * 
	 * <pre>
	 * .shortCode(false)
	 * .shortCode()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <pre>
	 * .shortCode(false)
	 * .shortCode()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * In both cases, {@code shortCode(false)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param enable
	 *            enable or disable short code conversion
	 * @return this instance for fluent chaining
	 */
	public SenderNumberFormatBuilder shortCode(Boolean enable) {
		enableShortCodeValueBuilder.setValue(enable);
		return this;
	}

	/**
	 * Enable/disable short code conversion: if the sender address is a short
	 * code, TON is set to 3, and NPI is set to 0. A number is considered to be
	 * a short code if the length of the number is 5 digits or less.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .shortCode()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #shortCode(Boolean)} takes precedence
	 * over property values and default value.
	 * 
	 * <pre>
	 * .shortCode(false)
	 * .shortCode()
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
	public ConfigurationValueBuilder<SenderNumberFormatBuilder, Boolean> shortCode() {
		return enableShortCodeValueBuilder;
	}

	/**
	 * Enable/disable international number conversion: if the sender starts with
	 * a "+", TON is set to 1, and NPI is set to 1.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #internationalNumber()}.
	 * 
	 * <pre>
	 * .internationalNumber(false)
	 * .internationalNumber()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <pre>
	 * .internationalNumber(false)
	 * .internationalNumber()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * In both cases, {@code internationalNumber(false)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param enable
	 *            enable or disable international number conversion
	 * @return this instance for fluent chaining
	 */
	public SenderNumberFormatBuilder internationalNumber(Boolean enable) {
		enableInternationalValueBuilder.setValue(enable);
		return this;
	}

	/**
	 * Enable/disable international number conversion: if the sender starts with
	 * a "+", TON is set to 1, and NPI is set to 1.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .internationalNumber()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #internationalNumber(Boolean)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .internationalNumber(false)
	 * .internationalNumber()
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
	public ConfigurationValueBuilder<SenderNumberFormatBuilder, Boolean> internationalNumber() {
		return enableInternationalValueBuilder;
	}

	@Override
	public PhoneNumberTranslator build() {
		CompositePhoneNumberTranslator translator = new CompositePhoneNumberTranslator();
		if (enabled(enableAlphanumericValueBuilder)) {
			translator.add(new AlphanumericCodeNumberFormatHandler());
		}
		if (enabled(enableShortCodeValueBuilder)) {
			translator.add(new ShortCodeNumberFormatHandler());
		}
		if (enabled(enableInternationalValueBuilder)) {
			translator.add(new InternationalNumberFormatHandler());
		}
		translator.add(new DefaultHandler());
		return translator;
	}

	private static boolean enabled(ConfigurationValueBuilderHelper<?, Boolean> props) {
		return props.getValue(false);
	}
}
