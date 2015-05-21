package fr.sii.notification.core.template.resolver;

import fr.sii.notification.core.condition.Condition;
import fr.sii.notification.core.exception.template.TemplateResolutionException;
import fr.sii.notification.core.template.Template;

/**
 * <p>
 * Decorator that delegates to other implementations. It acts as bridge between
 * {@link ConditionalResolver} and {@link Condition}. It indicates if the
 * template can be handled by delegating to a {@link Condition}. The template
 * resolution is also delegated to a {@link TemplateResolver}.
 * </p>
 * <p>
 * This class may be useful for a template resolver that doesn't implement the
 * {@link ConditionalResolver} interface. It makes a basic template resolver
 * becoming a a conditional template resolver.
 * </p>
 * 
 * @author Aurélien Baudet
 * @see Condition
 */
public class ConditionalDelegateResolver implements ConditionalResolver {
	/**
	 * The condition to evaluate when calling {@link #supports(String)}
	 */
	private Condition<String> condition;

	/**
	 * The resolver to call when trying to find the template
	 */
	private TemplateResolver resolver;

	/**
	 * Initialize with a condition and a resolver.
	 * 
	 * @param condition
	 *            The condition that is evaluated when {@link #supports(String)}
	 *            is called. The condition receive the template name as
	 *            argument
	 * @param resolver
	 *            The resolver to call if the condition has indicated that the
	 *            template is supported
	 */
	public ConditionalDelegateResolver(Condition<String> condition, TemplateResolver resolver) {
		super();
		this.condition = condition;
		this.resolver = resolver;
	}

	@Override
	public boolean supports(String lookup) {
		return condition.accept(lookup);
	}

	@Override
	public Template getTemplate(String lookup) throws TemplateResolutionException {
		return resolver.getTemplate(lookup);
	}
}
