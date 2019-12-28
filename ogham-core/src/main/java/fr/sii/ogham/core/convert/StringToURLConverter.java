package fr.sii.ogham.core.convert;

import java.net.MalformedURLException;
import java.net.URL;

import fr.sii.ogham.core.exception.convert.ConversionException;

/**
 * Converts a string to an {@link URL}. It uses {@link URL#URL(String)}
 * constructor to parse the provided url.
 * 
 * For example:
 * 
 * <pre>
 * <code>
 * String source = "http://foo.bar:8520";
 * // calling the converter
 * URL result = converter.convert(source, URL.class);
 * System.out.println(result.getProtocol());
 * System.out.println(result.getHost());
 * System.out.println(result.getPort());
 * // prints:
 * // 'http'
 * // 'foo.bar'
 * // 8520
 * </code>
 * </pre>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class StringToURLConverter implements SupportingConverter {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Object source, Class<T> targetType) {
		String text = (String) source;
		if(text == null || text.isEmpty()) {
			return null;
		}
		try {
			return (T) new URL(text);
		} catch (MalformedURLException e) {
			throw new ConversionException("Invalid URL '" + source + "'", e);
		}
	}

	@Override
	public boolean supports(Class<?> sourceType, Class<?> targetType) {
		return String.class.isAssignableFrom(sourceType) && URL.class.isAssignableFrom(targetType);
	}

}
