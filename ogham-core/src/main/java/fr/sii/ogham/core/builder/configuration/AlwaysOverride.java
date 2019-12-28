package fr.sii.ogham.core.builder.configuration;

/**
 * Always overrides current value and returns the new value (even if null).
 * 
 * @author Aurélien Baudet
 *
 * @param <V>
 *            the type of the value
 */
public class AlwaysOverride<V> implements MayOverride<V> {
	private final V value;

	/**
	 * Initializes with the new value to set.
	 * 
	 * @param value
	 *            the new value
	 */
	public AlwaysOverride(V value) {
		super();
		this.value = value;
	}

	@Override
	public V override(V currentValue) {
		return value;
	}

}
