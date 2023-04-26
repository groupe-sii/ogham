package fr.sii.ogham.spring.template.thymeleaf;

import java.util.Map;

import org.springframework.web.servlet.support.RequestContext;

import fr.sii.ogham.spring.util.compat.HttpServletRequestWrapper;
import fr.sii.ogham.spring.util.compat.HttpServletResponseWrapper;
import fr.sii.ogham.spring.util.compat.ServletContextWrapper;

/**
 * This aim of this interface is to be able to handle different versions of
 * Thymeleaf in a Spring context.
 *
 * In Thymeleaf v2, there is no need to wrap the {@link RequestContext} into a
 * wrapper and register it as another variable in the model.
 *
 * In Thymeleaf v3, the {@link RequestContext} is wrapped into a
 * org.thymeleaf.spring5.context.IThymeleafRequestContext and registered as a variable in the model.
 * Since Spring WebFlux, we also have to handle both Spring Web and Spring
 * WebFlux.
 *
 *
 * @author Aurélien Baudet
 *
 */
public interface ThymeleafRequestContextWrapper {

	/**
	 * Wrap the RequestContext and current Web context
	 * (HttpServletRequest, HttpServletResponse and
	 * ServletContext) into a new object and register it into the
	 * provided model.
	 *
	 * @param requestContext
	 *            the request context
	 * @param request
	 *            the current HTTP request
	 * @param response
	 *            the current HTTP response
	 * @param servletContext
	 *            the servlet execution context
	 * @param springModel
	 *            the model to fill
	 */
	void wrapAndRegister(RequestContext requestContext, HttpServletRequestWrapper request, HttpServletResponseWrapper response, ServletContextWrapper servletContext, Map<String, Object> springModel);

}
