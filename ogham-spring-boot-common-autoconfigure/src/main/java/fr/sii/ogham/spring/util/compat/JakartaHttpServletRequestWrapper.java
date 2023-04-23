package fr.sii.ogham.spring.util.compat;

import jakarta.servlet.http.HttpServletRequest;

public class JakartaHttpServletRequestWrapper implements HttpServletRequestWrapper {
	private final HttpServletRequest request;
	

	public JakartaHttpServletRequestWrapper(HttpServletRequest request) {
		super();
		this.request = request;
	}


	@Override
	@SuppressWarnings("unchecked")
	public <T> T get() {
		return (T) request;
	}


	@Override
	public Object getAttribute(String name) {
		return request.getAttribute(name);
	}

}
