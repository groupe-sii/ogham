package fr.sii.ogham.spring.v3.template.thymeleaf;

import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.spring.template.thymeleaf.WebContextProvider;
import fr.sii.ogham.spring.util.compat.HttpServletRequestWrapper;
import fr.sii.ogham.spring.util.compat.HttpServletResponseWrapper;
import fr.sii.ogham.spring.util.compat.ServletContextWrapper;
import fr.sii.ogham.spring.v3.util.compat.JakartaHttpServletRequestWrapper;
import fr.sii.ogham.spring.v3.util.compat.JakartaHttpServletResponseWrapper;
import fr.sii.ogham.spring.v3.util.compat.JakartaServletContextWrapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Implementation that retrieves the current {@link HttpServletRequest} and
 * {@link HttpServletResponse} using {@link RequestContextHolder}.
 *
 * @author Aurélien Baudet
 *
 */
public class RequestContextHolderWebContextProvider implements WebContextProvider, ServletContextAware {
	private ServletContext servletContext;

	@Override
	public HttpServletRequestWrapper getRequest(Context context) {
		return new JakartaHttpServletRequestWrapper(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
	}

	@Override
	public HttpServletResponseWrapper getResponse(Context context) {
		return new JakartaHttpServletResponseWrapper(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse());
	}

	@Override
	public ServletContextWrapper getServletContext(Context context) {
		return new JakartaServletContextWrapper(servletContext);
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
