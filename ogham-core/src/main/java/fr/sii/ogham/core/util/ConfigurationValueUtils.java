package fr.sii.ogham.core.util;

/**
 * Some utility methods for configuration values.
 * 
 * @author Aurélien Baudet
 *
 */
public final class ConfigurationValueUtils {
	/**
	 * Returns the first non-null value.
	 * 
	 * @param <T>
	 *            the type of values
	 * @param values
	 *            the value array
	 * @return the first non-null value
	 */
	@SafeVarargs
	public static <T> T firstValue(T... values) {
		for (T value : values) {
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	private ConfigurationValueUtils() {
		super();
	}
}
