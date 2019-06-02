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

}
