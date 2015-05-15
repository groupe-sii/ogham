package fr.sii.notification.core.template.resolver;

import fr.sii.notification.core.condition.Condition;
import fr.sii.notification.core.exception.template.TemplateResolutionException;
import fr.sii.notification.core.template.Template;

public class ConditionalDelegateResolver implements ConditionalResolver {

	private Condition<String> condition;
	
	private TemplateResolver resolver;
	
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
