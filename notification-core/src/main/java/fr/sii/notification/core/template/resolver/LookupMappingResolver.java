package fr.sii.notification.core.template.resolver;

import java.util.Map;

import fr.sii.notification.core.exception.template.TemplateResolutionException;
import fr.sii.notification.core.template.Template;

public class LookupMappingResolver implements ConditionalResolver {

	private Map<String, TemplateResolver> mapping;
	
	public LookupMappingResolver(Map<String, TemplateResolver> mapping) {
		super();
		this.mapping = mapping;
	}

	@Override
	public Template getTemplate(String templateName) throws TemplateResolutionException {
		TemplateResolver resolver = mapping.get(getLookupType(templateName));
		return resolver.getTemplate(getTemplateName(templateName));
	}

	@Override
	public boolean supports(String templateName) {
		String lookupType = getLookupType(templateName);
		return lookupType!=null && mapping.containsKey(lookupType);
	}

	private String getLookupType(String templateName) {
		int idx = templateName.indexOf(":");
		return idx>0 ? templateName.substring(0, idx) : null;
	}
	
	private String getTemplateName(String templateName) {
		int idx = templateName.indexOf(":");
		return templateName.substring(idx+1);
	}
}
