package fr.sii.ogham.spring.v3.util.compat;

import fr.sii.ogham.spring.util.compat.ServletContextWrapper;
import jakarta.servlet.ServletContext;

public class JakartaServletContextWrapper implements ServletContextWrapper {
	private final ServletContext context;
	
	public JakartaServletContextWrapper(ServletContext context) {
		super();
		this.context = context;
	}


	@Override
	@SuppressWarnings("unchecked")
	public <T> T get() {
		return (T) context;
	}

}
