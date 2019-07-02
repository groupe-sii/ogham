package fr.sii.ogham.core.convert;

import java.net.URL;

/**
 * The default converter delegates conversion to registered
 * {@link SupportingConverter}s. It provides a default conversion behavior:
 * <ul>
 * <li>Converts a string separated by {@literal ,} to an array of anything (see
 * {@link StringToArrayConverter}). Conversion of each element is applied
 * through the {@link DefaultConverter} too</li>
 * <li>Converts a string to a boolean (see
 * {@link StringToBooleanConverter})</li>
 * <li>Converts a string to a number or byte (see
 * {@link StringToNumberConverter})
 * </li>
 * <li>Converts a string to an {@link URL} or byte (see
 * {@link StringToURLConverter})
 * </li>
 * </ul>
 * 
 * If no conversion is required but types differ (sub-class for example), a cast
 * is applied.
 * 
 * @author Aurélien Baudet
 *
 */
public class DefaultConverter extends DelegateConverter {
	/**
	 * Registers the default converters
	 */
	public DefaultConverter() {
		super();
		register(new StringToArrayConverter(this));
		register(new StringToBooleanConverter());
		register(new StringToNumberConverter());
		register(new StringToURLConverter());
		register(new NoConversionNeededConverter());
	}
}
