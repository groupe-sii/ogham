package fr.sii.ogham.spring.template.thymeleaf;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.sii.ogham.core.template.context.Context;

/**
 * Provides access to {@link HttpServletRequest}, {@link HttpServletResponse}
 * and {@link ServletContext}.
 * 
 * The aim is to retrieve the current web context in a web application in order
 * to provide Spring extensions in Thymeleaf templates such as <a href=
 * "https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html#building-uris-to-controllers">#mvc.url</a>
 * and <a href=
 * "https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html#handling-the-command-object">&#64;{/path/to/controller}</a>.
 * 
 * @author Aurélien Baudet
 *
 */
public interface WebContextProvider {

	/**
	 * Get the current request
	 * 
	 * @param context
	 *            Ogham context that may contain useful information for
	 *            retrieval
	 * @return the current request
	 */
	HttpServletRequest getRequest(Context context);

	/**
	 * Get the current response
	 * 
	 * @param context
	 *            Ogham context that may contain useful information for
	 *            retrieval
	 * @return the current response
	 */
	HttpServletResponse getResponse(Context context);

	/**
	 * Access to the servlet execution context
	 * 
	 * @param context
	 *            Ogham context that may contain useful information for
	 *            retrieval
	 * @return the servlet context
	 */
	ServletContext getServletContext(Context context);

}
