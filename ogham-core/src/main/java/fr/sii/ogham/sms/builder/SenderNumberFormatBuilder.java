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
import fr.sii.ogham.sms.message.addressing.translator.AlphanumericCodeNumberFormatHandler;
import fr.sii.ogham.sms.message.addressing.translator.CompositePhoneNumberTranslator;
import fr.sii.ogham.sms.message.addressing.translator.DefaultHandler;
import fr.sii.ogham.sms.message.addressing.translator.InternationalNumberFormatHandler;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;
import fr.sii.ogham.sms.message.addressing.translator.ShortCodeNumberFormatHandler;

public class SenderNumberFormatBuilder extends AbstractParent<SenderNumberBuilder> implements Builder<PhoneNumberTranslator> {
	private EnvironmentBuilder<?> environmentBuilder;
	private Boolean enableAplhanumeric;
	private Boolean enableShortCode;
	private Boolean enableInternational;
	private List<String> enableAlphanumericProps;
	private List<String> enableShortCodeProps;
	private List<String> enableInternationalProps;
	
	public SenderNumberFormatBuilder(SenderNumberBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		enableAlphanumericProps = new ArrayList<>();
		enableShortCodeProps = new ArrayList<>();
		enableInternationalProps = new ArrayList<>();
	}

	public SenderNumberFormatBuilder alphanumericCode(String... properties) {
		enableAlphanumericProps.addAll(Arrays.asList(properties));
		return this;
	}
	
	public SenderNumberFormatBuilder alphanumericCode(boolean enable) {
		enableAplhanumeric = enable;
		return this;
	}
	
	public SenderNumberFormatBuilder shortCode(String... properties) {
		enableShortCodeProps.addAll(Arrays.asList(properties));
		return this;
	}

	public SenderNumberFormatBuilder shortCode(boolean enable) {
		enableShortCode = enable;
		return this;
	}
	
	public SenderNumberFormatBuilder internationalNumber(String... properties) {
		enableInternationalProps.addAll(Arrays.asList(properties));
		return this;
	}
	
	public SenderNumberFormatBuilder internationalNumber(boolean enable) {
		enableInternational = enable;
		return this;
	}

	@Override
	public PhoneNumberTranslator build() throws BuildException {
		CompositePhoneNumberTranslator translator = new CompositePhoneNumberTranslator();
		if(enabled(enableAplhanumeric, enableAlphanumericProps)) {
			translator.add(new AlphanumericCodeNumberFormatHandler());
		}
		if(enabled(enableShortCode, enableShortCodeProps)) {
			translator.add(new ShortCodeNumberFormatHandler());
		}
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
