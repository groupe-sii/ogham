package fr.sii.notification.core.template.resolver;

/**
 * Extension of the basic template resolution interface for adding conditional
 * use of the implementations. This extension adds a method that indicates to
 * the system that uses it if the implementation is able to handle the provided
 * template path.
 * 
 * @author Aurélien Baudet
 *
 */
public interface ConditionalResolver extends TemplateResolver {
	/**
	 * Indicates if the template path can be handled by this template resolver
	 * or not.
	 * 
	 * @param templateName
	 *            the name or the path of the template
	 * @return true if the template path can be handled by this template
	 *         resolver, false otherwise
	 */
	public boolean supports(String templateName);
}
