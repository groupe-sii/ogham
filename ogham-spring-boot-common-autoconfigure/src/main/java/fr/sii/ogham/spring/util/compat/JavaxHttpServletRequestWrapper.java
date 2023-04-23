package fr.sii.ogham.spring.util.compat;

import javax.servlet.http.HttpServletRequest;

public class JavaxHttpServletRequestWrapper implements HttpServletRequestWrapper {
	private final HttpServletRequest request;
	

	public JavaxHttpServletRequestWrapper(HttpServletRequest request) {
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
