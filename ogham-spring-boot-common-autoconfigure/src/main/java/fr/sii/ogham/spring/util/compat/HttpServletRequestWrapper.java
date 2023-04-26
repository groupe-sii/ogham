package fr.sii.ogham.spring.util.compat;

public interface HttpServletRequestWrapper {
	<T> T get();

	Object getAttribute(String name);
}
