package fr.sii.ogham.core.convert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.exception.convert.ConversionException;

public class DelegateConverter implements Converter {
	private final List<SupportingConverter> delegates;
	
	public DelegateConverter(SupportingConverter... delegates) {
		this(new ArrayList<>(Arrays.asList(delegates)));
	}

	public DelegateConverter(List<SupportingConverter> delegates) {
		super();
		this.delegates = delegates;
	}
	
	public DelegateConverter add(SupportingConverter converter) {
		delegates.add(converter);
		return this;
	}

	@Override
	public <T> T convert(Object source, Class<T> targetType) throws ConversionException {
		if(source==null) {
			return null;
		}
		for(SupportingConverter converter : delegates) {
			if(converter.supports(source.getClass(), targetType)) {
				return converter.convert(source, targetType);
			}
		}
		throw new ConversionException("No converter available to convert "+source+" into "+targetType.getSimpleName());
	}
	
}
