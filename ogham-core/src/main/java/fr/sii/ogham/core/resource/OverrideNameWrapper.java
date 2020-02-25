package fr.sii.ogham.core.resource;

import java.io.IOException;
import java.io.InputStream;

public class OverrideNameWrapper implements NamedResource {
	private final Resource delegate;
	private String name;

	public OverrideNameWrapper(Resource delegate) {
		this(delegate, null);
	}
	
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

}
