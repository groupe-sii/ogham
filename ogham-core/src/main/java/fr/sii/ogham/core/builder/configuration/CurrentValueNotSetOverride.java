package fr.sii.ogham.core.builder.configuration;

/**
 * Override the current value only if it is not set ({@code null}).
 * 
 * @author Aurélien Baudet
 *
 * @param <V>
 *            the type of the vlaue
 */
public class CurrentValueNotSetOverride<V> implements MayOverride<V> {
	private final V value;

	/**
	 * Initializes with the new value to set if current value is not set.
	 * 
	 * @param value
	 *            the new value
	 */
	public CurrentValueNotSetOverride(V value) {
		super();
		this.value = value;
	}

	@Override
	public V override(V currentValue) {
		return currentValue == null ? value : currentValue;
	}

}
