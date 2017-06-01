package fr.sii.ogham.sms.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.util.BuilderUtils;
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
	private EnvironmentBuilder<?> environmentBuilder;
	private Boolean enableInternational;
	private List<String> enableInternationalProps;

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
		enableInternationalProps = new ArrayList<>();
	}

	/**
	 * Enable/disable international number conversion: if the sender starts with
	 * a "+", TON is set to 1, and NPI is set to 1.
	 * 
	 * You can specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .charset("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param properties
	 *            one or several property keys
	 * @return this instance for fluent chaining
	 */
	public RecipientNumberFormatBuilder internationalNumber(String... properties) {
		enableInternationalProps.addAll(Arrays.asList(properties));
		return this;
	}

	/**
	 * Enable/disable international number conversion: if the sender starts with
	 * a "+", TON is set to 1, and NPI is set to 1.
	 * 
	 * @param enable
	 *            true to enable the conversion, false to disable
	 * @return this instance for fluent chaining
	 */
	public RecipientNumberFormatBuilder internationalNumber(boolean enable) {
		enableInternational = enable;
		return this;
	}

	@Override
	public PhoneNumberTranslator build() throws BuildException {
		CompositePhoneNumberTranslator translator = new CompositePhoneNumberTranslator();
		if (enabled(enableInternational, enableInternationalProps)) {
			translator.add(new InternationalNumberFormatHandler());
		}
		translator.add(new DefaultHandler());
		return translator;
	}

	private boolean enabled(Boolean enable, List<String> enableProps) {
		if (enable != null && enable) {
			return true;
		}
		PropertyResolver propertyResolver = environmentBuilder.build();
		Boolean value = BuilderUtils.evaluate(enableProps, propertyResolver, Boolean.class);
		return value != null && value;
	}
}
