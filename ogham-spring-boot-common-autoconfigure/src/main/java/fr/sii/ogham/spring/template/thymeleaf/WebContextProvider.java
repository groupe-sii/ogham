package fr.sii.ogham.spring.template.thymeleaf;

import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.spring.util.compat.HttpServletRequestWrapper;
import fr.sii.ogham.spring.util.compat.HttpServletResponseWrapper;
import fr.sii.ogham.spring.util.compat.ServletContextWrapper;

/**
 * Provides access to {@link javax.servlet.http.HttpServletRequest}, {@link javax.servlet.http.HttpServletResponse}
 * and {@link javax.servlet.ServletContext} for old versions using javax or 
 * {@link jakarta.servlet.http.HttpServletRequest}, {@link jakarta.servlet.http.HttpServletResponse}
 * and {@link jakarta.servlet.ServletContext} for newer versions.
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
	HttpServletRequestWrapper getRequest(Context context);

	/**
	 * Get the current response
	 * 
	 * @param context
	 *            Ogham context that may contain useful information for
	 *            retrieval
	 * @return the current response
	 */
	HttpServletResponseWrapper getResponse(Context context);

	/**
	 * Access to the servlet execution context
	 * 
	 * @param context
	 *            Ogham context that may contain useful information for
	 *            retrieval
	 * @return the servlet context
	 */
	ServletContextWrapper getServletContext(Context context);

}
