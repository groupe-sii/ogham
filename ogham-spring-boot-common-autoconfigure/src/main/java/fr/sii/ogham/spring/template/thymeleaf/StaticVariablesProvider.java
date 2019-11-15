package fr.sii.ogham.spring.template.thymeleaf;

import java.util.Map;

import org.thymeleaf.spring5.view.AbstractThymeleafView;

import fr.sii.ogham.core.template.context.Context;

/**
 * Spring {@link AbstractThymeleafView} allows to provide static variables.
 * 
 * The aim of this interface is to mimic {@link AbstractThymeleafView} behavior
 * and to let the possibility to register static variables like in web context.
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
