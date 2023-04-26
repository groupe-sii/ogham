package fr.sii.ogham.spring.v1.template.thymeleaf;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.spring.template.thymeleaf.WebContextProvider;
import fr.sii.ogham.spring.util.compat.HttpServletRequestWrapper;
import fr.sii.ogham.spring.util.compat.HttpServletResponseWrapper;
import fr.sii.ogham.spring.util.compat.JavaxHttpServletRequestWrapper;
import fr.sii.ogham.spring.util.compat.JavaxHttpServletResponseWrapper;
import fr.sii.ogham.spring.util.compat.JavaxServletContextWrapper;
import fr.sii.ogham.spring.util.compat.ServletContextWrapper;

/**
 * Implementation that retrieves the current {@link HttpServletRequest} and
 * {@link HttpServletResponse} using {@link RequestContextHolder}.
 *
 * @author Aurélien Baudet
 *
 */
public class JavaxRequestContextHolderWebContextProvider implements WebContextProvider, ServletContextAware {
	private ServletContext servletContext;

	@Override
	public HttpServletRequestWrapper getRequest(Context context) {
		return new JavaxHttpServletRequestWrapper(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
	}

	@Override
	public HttpServletResponseWrapper getResponse(Context context) {
		return new JavaxHttpServletResponseWrapper(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse());
	}

	@Override
	public ServletContextWrapper getServletContext(Context context) {
		return new JavaxServletContextWrapper(servletContext);
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
