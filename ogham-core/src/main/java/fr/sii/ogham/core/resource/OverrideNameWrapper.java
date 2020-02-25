package fr.sii.ogham.core.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps a {@link Resource} to provide a name.
 * 
 * If no name has been provided and the wrapped {@link Resource} is a
 * {@link NamedResource}, the name of the underlying {@link NamedResource} is
 * used.
 * 
 * @author Aurélien Baudet
 *
 */
public class OverrideNameWrapper implements NamedResource {
	private final Resource delegate;
	private String name;

	/**
	 * Wraps the resource with no overridden name.
	 * 
	 * @param delegate
	 *            the wrapped resource
	 */
	public OverrideNameWrapper(Resource delegate) {
		this(delegate, null);
	}

	/**
	 * Wraps the resource and override its name.
	 * 
	 * @param delegate
	 *            the wrapped resource
	 * @param name
	 *            the name to use
	 */
	public OverrideNameWrapper(Resource delegate, String name) {
		super();
		this.delegate = delegate;
		this.name = name;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return delegate.getInputStream();
	}

	@Override
	public String getName() {
		if (name != null) {
			return name;
		}
		if (delegate instanceof NamedResource) {
			return ((NamedResource) delegate).getName();
		}
		throw new IllegalStateException("The resource must have a name");
	}

	public Resource getDelegate() {
		return delegate;
	}
}
