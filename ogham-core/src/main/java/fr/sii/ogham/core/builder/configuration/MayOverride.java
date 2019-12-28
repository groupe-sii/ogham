package fr.sii.ogham.core.builder.configuration;

/**
 * If the new value should override the current value, the new value is
 * returned. If the new value should not override, the current value is
 * returned.
 * 
 * @author Aurélien Baudet
 *
 * @param <V>
 *            the type of the value
 */
public interface MayOverride<V> {

	/**
	 * If the new value should override the current value, the new value is
	 * returned. If the new value should not override, the current value is
	 * returned.
	 * 
	 * @param currentValue
	 *            the current value that may be overridden
	 * @return the new value
	 */
	V override(V currentValue);

	/**
	 * Override current value only if the value parameter is not {@code null}.
	 * 
	 * @param <V>
	 *            the type of the value
	 * @param value
	 *            the new value to set if not {@code null}
	 * @return the override control
	 */
	static <V> MayOverride<V> overrideIfNonNull(V value) {
		return new NonNullOverride<>(value);
	}

	/**
	 * Always override current value.
	 * 
	 * @param <V>
	 *            the type of the value
	 * @param value
	 *            the new value to set
	 * @return the override control
	 */
	static <V> MayOverride<V> alwaysOverride(V value) {
		return new AlwaysOverride<>(value);
	}

	/**
	 * Override current value only if the current value is not set
	 * ({@code null}). If current value is set, the new value is not applied.
	 * 
	 * @param <V>
	 *            the type of the value
	 * @param value
	 *            the new value to set if current value is {@code null}
	 * @return the override control
	 */
	static <V> MayOverride<V> overrideIfNotSet(V value) {
		return new CurrentValueNotSetOverride<>(value);
	}

}
