package fr.sii.ogham.spring.template.thymeleaf;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import fr.sii.ogham.core.template.context.Context;

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
	public HttpServletRequest getRequest(Context context) {
		return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
	}

	@Override
	public HttpServletResponse getResponse(Context context) {
		return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
	}

	@Override
	public ServletContext getServletContext(Context context) {
		return servletContext;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
