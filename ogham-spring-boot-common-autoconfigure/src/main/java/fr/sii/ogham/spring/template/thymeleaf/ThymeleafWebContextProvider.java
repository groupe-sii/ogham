package fr.sii.ogham.spring.template.thymeleaf;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.thymeleaf.context.IContext;

import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.spring.util.compat.HttpServletRequestWrapper;
import fr.sii.ogham.spring.util.compat.HttpServletResponseWrapper;
import fr.sii.ogham.spring.util.compat.ServletContextWrapper;

/**
 * Generate an instance of a Thymeleaf {@link IContext} that is suitable in a
 * Web application.
 *
 * The generated context is then used to provide access to Spring extensions
 * from templates such as <a href=
 * "https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html#building-uris-to-controllers">#mvc.url</a>,
 * <a href=
 * "https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html#handling-the-command-object">&#64;{/path/to/controller}</a>
 * and any other Spring extension.
 *
 * @author Aurélien Baudet
 *
 */
public interface ThymeleafWebContextProvider {

	/**
	 * Generates the suitable context for a Web application.
	 *
	 * @param context
	 *            Ogham original context that may be used to retrieve extra
	 *            information
	 * @param base
	 *            Thymeleaf context that has been converted from Ogham original
	 *            context. It contains the base variables extracted from Ogham
	 *            context.
	 * @param request
	 *            the current HTTP request
	 * @param response
	 *            the current HTTP response
	 * @param servletContext
	 *            the servlet execution context
	 * @param applicationContext
	 *            the application context
	 * @param springModel
	 *            the model that contains both variables extracted from Ogham
	 *            context and Spring variables (static variables, evaluation
	 *            context, ...)
	 * @return the suitable context for a Web application to provide access to
	 *         Spring extensions
	 */
	IContext getWebContext(Context context, IContext base, HttpServletRequestWrapper request, HttpServletResponseWrapper response, ServletContextWrapper servletContext, ApplicationContext applicationContext,
						   Map<String, Object> springModel);

}
