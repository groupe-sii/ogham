package fr.sii.ogham.core.convert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.exception.convert.ConversionException;

/**
 * Calls registered {@link SupportingConverter}s to make the real conversion. It
 * asks each {@link SupportingConverter} if it can make the conversion. If the
 * {@link SupportingConverter} can do the conversion, the conversion is applied
 * using that {@link SupportingConverter} and the result is immediately
 * returned.
 * 
 * If none of the registered {@link SupportingConverter}s can make the
 * conversion, then a {@link ConversionException} is thrown.
 * 
 * If the source is null, then the result is null too.
 * 
 * <strong>Registration order is important.</strong>
 * 
 * @author Aurélien Baudet
 *
 */
public class DelegateConverter implements Converter, ConverterRegistry {
	private final List<SupportingConverter> delegates;

	/**
	 * Registers none, one or several converters
	 * 
	 * @param delegates
	 *            the converters to register
	 */
	public DelegateConverter(SupportingConverter... delegates) {
		this(new ArrayList<>(Arrays.asList(delegates)));
	}

	/**
	 * Registers a list of converters. The list must not be null
	 * 
	 * @param delegates
	 *            the converters to register
	 */
	public DelegateConverter(List<SupportingConverter> delegates) {
		super();
		this.delegates = delegates;
	}

	@Override
	public ConverterRegistry register(SupportingConverter converter) {
		if (!delegates.contains(converter)) {
			delegates.add(converter);
		}
		return this;
	}

	@Override
	public <T> T convert(Object source, Class<T> targetType) {
		if (source == null) {
			return null;
		}
		for (SupportingConverter converter : delegates) {
			if (converter.supports(source.getClass(), targetType)) {
				return converter.convert(source, targetType);
			}
		}
		throw new ConversionException("No converter available to convert " + source + " into " + targetType.getSimpleName());
	}

	@Override
	public List<SupportingConverter> getRegisteredConverters() {
		return delegates;
	}

}
