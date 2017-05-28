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
import fr.sii.ogham.sms.message.addressing.translator.CompositePhoneNumberTranslator;
import fr.sii.ogham.sms.message.addressing.translator.DefaultHandler;
import fr.sii.ogham.sms.message.addressing.translator.InternationalNumberFormatHandler;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;

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

	public RecipientNumberFormatBuilder internationalNumber(String... properties) {
		enableInternationalProps.addAll(Arrays.asList(properties));
		return this;
	}
	
	public RecipientNumberFormatBuilder internationalNumber(boolean enable) {
		enableInternational = enable;
		return this;
	}

	@Override
	public PhoneNumberTranslator build() throws BuildException {
		CompositePhoneNumberTranslator translator = new CompositePhoneNumberTranslator();
		if(enabled(enableInternational, enableInternationalProps)) {
			translator.add(new InternationalNumberFormatHandler());
		}
		translator.add(new DefaultHandler());
		return translator;
	}
	
	private boolean enabled(Boolean enable, List<String> enableProps) {
		if(enable!=null && enable) {
			return true;
		}
		PropertyResolver propertyResolver = environmentBuilder.build();
		Boolean value = BuilderUtils.evaluate(enableProps, propertyResolver, Boolean.class);
		return value!=null && value;
	}
}
