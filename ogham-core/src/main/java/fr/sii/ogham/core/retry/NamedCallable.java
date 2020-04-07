package fr.sii.ogham.core.retry;

import java.util.concurrent.Callable;

/**
 * A simple wrapper that allows to name a callable. This is useful to debug and
 * when there is an error.
 * 
 * @author Aurélien Baudet
 *
 * @param <V>
 *            the result type of method call
 */
public class NamedCallable<V> implements Callable<V> {
	private final String name;
	private final Callable<V> delegate;

	/**
	 * Name a callable (action) for debugging purpose.
	 * 
	 * @param name
	 *            the name of the action
	 * @param delegate
	 *            the real callable
	 */
	public NamedCallable(String name, Callable<V> delegate) {
		super();
		this.name = name;
		this.delegate = delegate;
	}

	@Override
	public V call() throws Exception {
		return delegate.call();
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Wrap an action to provide a name. This is useful for debugging purpose
	 * and in logs.
	 * 
	 * @param <V>
	 *            the type of the returned value of the original action
	 * @param name
	 *            the name to use for the action
	 * @param action
	 *            the real action to execute
	 * @return the wrapped action
	 */
	public static <V> Callable<V> named(String name, Callable<V> action) {
		return new NamedCallable<>(name, action);
	}

	/**
	 * Wrap an action to provide a name. This is useful for debugging purpose
	 * and in logs.
	 * 
	 * @param name
	 *            the name to use for the action
	 * @param action
	 *            the real action to execute
	 * @return the wrapped action
	 */
	public static Callable<Void> named(String name, Executable action) {
		return new NamedCallable<>(name, () -> {
			action.execute();
			return null;
		});
	}
}
