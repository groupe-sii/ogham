package fr.sii.ogham.sms.builder;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.addressing.AddressedPhoneNumber;
import fr.sii.ogham.sms.message.addressing.translator.CompositePhoneNumberTranslator;
import fr.sii.ogham.sms.message.addressing.translator.DefaultHandler;
import fr.sii.ogham.sms.message.addressing.translator.InternationalNumberFormatHandler;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;

/**
 * Defines which standard conversions may be applied on the phone number to
 * convert it from a {@link PhoneNumber} to an {@link AddressedPhoneNumber}.
 * 
 * @author Aurélien Baudet
 *
 */
public class RecipientNumberFormatBuilder extends AbstractParent<RecipientNumberBuilder> implements Builder<PhoneNumberTranslator> {
	private final EnvironmentBuilder<?> environmentBuilder;
	private final ConfigurationValueBuilderHelper<RecipientNumberFormatBuilder, Boolean> enableInternationalValueBuilder;

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
	public RecipientNumberFormatBuilder(RecipientNumberBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		enableInternationalValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class);
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
	 *            enable or disable international format
	 * @return this instance for fluent chaining
	 */
	public RecipientNumberFormatBuilder internationalNumber(Boolean enable) {
		enableInternationalValueBuilder.setValue(enable);
		return this;
	}

	
	/**
	 * Enable/disable international number conversion: if the sender starts with
	 * a "+", TON is set to 1, and NPI is set to 1.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
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
	public ConfigurationValueBuilder<RecipientNumberFormatBuilder, Boolean> internationalNumber() {
		return enableInternationalValueBuilder;
	}

	@Override
	public PhoneNumberTranslator build() {
		CompositePhoneNumberTranslator translator = new CompositePhoneNumberTranslator();
		if (enabled(enableInternationalValueBuilder)) {
			translator.add(new InternationalNumberFormatHandler());
		}
		translator.add(new DefaultHandler());
		return translator;
	}

	private boolean enabled(ConfigurationValueBuilderHelper<?, Boolean> enableInternational) {
		PropertyResolver propertyResolver = environmentBuilder.build();
		return enableInternational.getValue(propertyResolver, false);
	}
}
