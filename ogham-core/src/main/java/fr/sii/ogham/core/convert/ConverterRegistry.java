package fr.sii.ogham.core.convert;

import java.util.List;

/**
 * Registers {@link SupportingConverter}s for later use.
 * 
 * @author Aurélien Baudet
 */
public interface ConverterRegistry {
	/**
	 * Registers a converter.
	 * 
	 * @param converter
	 *            the converter to register
	 * @return this instance for fluent chaining
	 */
	ConverterRegistry register(SupportingConverter converter);

	/**
	 * Get the list of registered converters
	 * 
	 * @return the registered converters
	 */
	List<SupportingConverter> getRegisteredConverters();
}
