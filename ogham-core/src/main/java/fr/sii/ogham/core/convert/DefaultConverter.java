package fr.sii.ogham.core.convert;

public class DefaultConverter extends DelegateConverter {
	public DefaultConverter() {
		super();
		add(new StringToArrayConverter(this));
		add(new NoConversionNeededConverter());
	}


}
