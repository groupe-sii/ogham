package fr.sii.ogham.core.builder.configuration;

/**
 * Override current value only if the new value is not {@code null}.
 * 
 * @author Aurélien Baudet
 *
 * @param <V>
 *            the type of the value
 */
public class NonNullOverride<V> implements MayOverride<V> {
	private final V value;

	/**
	 * Initializes with the new value to set if the new value is not
	 * {@code null}.
	 * 
	 * @param value
	 *            the new value
	 */
	public NonNullOverride(V value) {
		super();
		this.value = value;
	}

	@Override
	public V override(V currentValue) {
		return value == null ? currentValue : value;
	}

}
