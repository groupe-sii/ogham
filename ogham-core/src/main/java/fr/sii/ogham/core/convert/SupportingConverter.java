package fr.sii.ogham.core.convert;

/**
 * Converter that is able to indicate if he is able to support a source object
 * or a target type.
 * 
 * @author Aurélien Baudet
 *
 */
public interface SupportingConverter extends Converter {
	/**
	 * Indicates if the source type and the target type are supported. If the
	 * result is true, it means that the underlying converter is able to make
	 * the conversion. If false, the conversion won't be applied by the
	 * underlying converter
	 * 
	 * @param sourceType
	 *            the type of the source object that needs to be converted
	 * @param targetType
	 *            the type of the result object after conversion
	 * @return true if converter can make the conversion
	 */
	boolean supports(Class<?> sourceType, Class<?> targetType);
}
