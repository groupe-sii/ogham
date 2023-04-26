package fr.sii.ogham.spring.template.thymeleaf;

import java.util.Map;

import fr.sii.ogham.core.template.context.Context;

/**
 * Spring org.thymeleaf.spring5.view.AbstractThymeleafView allows to provide static variables.
 * 
 * The aim of this interface is to mimic org.thymeleaf.spring5.view.AbstractThymeleafView behavior
 * and to let the possibility register static variables like in web context.
 * 
 * @author Aurélien Baudet
 *
 */
public interface StaticVariablesProvider {
	/**
	 * Provide key/value pairs of static variables.
	 * 
	 * @param context
	 *            the Ogham context that may be used to retrieve static
	 *            variables
	 * @return the key/value pairs of static variables
	 */
	Map<String, Object> getStaticVariables(Context context);
}
