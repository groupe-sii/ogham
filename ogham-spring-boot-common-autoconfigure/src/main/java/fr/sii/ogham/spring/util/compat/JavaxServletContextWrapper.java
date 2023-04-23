package fr.sii.ogham.spring.util.compat;

import javax.servlet.ServletContext;

public class JavaxServletContextWrapper implements ServletContextWrapper {
	private final ServletContext context;
	
	public JavaxServletContextWrapper(ServletContext context) {
		super();
		this.context = context;
	}


	@Override
	@SuppressWarnings("unchecked")
	public <T> T get() {
		return (T) context;
	}

}
