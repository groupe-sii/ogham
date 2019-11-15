package fr.sii.ogham.spring.template.thymeleaf;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.spring5.context.IThymeleafRequestContext;

/**
 * This aim of this interface is to be able to handle different versions of
 * Thymeleaf in a Spring context.
 * 
 * In Thymeleaf v2, there is no need to wrap the {@link RequestContext} into a
 * wrapper and register it as another variable in the model.
 * 
 * In Thymeleaf v3, the {@link RequestContext} is wrapped into a
 * {@link IThymeleafRequestContext} and registered as a variable in the model.
 * Since Spring WebFlux, we also have to handle both Spring Web and Spring
 * WebFlux.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public interface ThymeleafRequestContextWrapper {

	/**
	 * Wrap the {@link RequestContext} and current Web context
	 * ({@link HttpServletRequest}, {@link HttpServletResponse} and
	 * {@link ServletContext}) into a new object and register it into the
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
	void wrapAndRegister(RequestContext requestContext, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, Map<String, Object> springModel);

}
