package fr.sii.ogham.spring.util.compat;

import javax.servlet.http.HttpServletResponse;

public class JavaxHttpServletResponseWrapper implements HttpServletResponseWrapper {
	private final HttpServletResponse response;
	
	public JavaxHttpServletResponseWrapper(HttpServletResponse response) {
		super();
		this.response = response;
	}


	@Override
	@SuppressWarnings("unchecked")
	public <T> T get() {
		return (T) response;
	}

}
