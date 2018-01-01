package fr.sii.ogham.core.util.bean;

/**
 * The value is already known so provide the value directly.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            The type of the value
 */
public class DirectAccessor<T> implements Accessor<T> {
	private final T value;

	/**
	 * Initialize with the provided value
	 * 
	 * @param value
	 *            the value to always return on {@link #getValue()}
	 */
	public DirectAccessor(T value) {
		super();
		this.value = value;
	}

	@Override
	public T getValue() {
		return value;
	}
}
