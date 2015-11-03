package fr.sii.ogham.core.convert;

public class DefaultConverter extends DelegateConverter {
	public DefaultConverter() {
		super();
		register(new StringToArrayConverter(this));
		register(new StringToBooleanConverter());
		register(new StringToNumberConverter());
		register(new NoConversionNeededConverter());
	}
}
