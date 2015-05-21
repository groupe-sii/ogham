package fr.sii.notification.template.thymeleaf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.notification.core.exception.template.TemplateResolutionException;

public class ThymeleafLookupMappingResolver {

	private Map<String, ITemplateResolver> mapping;
	
	public ThymeleafLookupMappingResolver() {
		this(new HashMap<String, ITemplateResolver>());
	}

	public ThymeleafLookupMappingResolver(Map<String, ITemplateResolver> mapping) {
		super();
		this.mapping = mapping;
	}
	
	public void addMapping(String lookup, ITemplateResolver resolver) {
		mapping.put(lookup, resolver);
	}

	public ITemplateResolver getResolver(String templateName) throws TemplateResolutionException {
		return mapping.get(getLookupType(templateName));
	}
	
	public String getTemplateName(String templateName) {
		int idx = templateName.indexOf(":");
		return templateName.substring(idx+1);
	}

	public boolean supports(String templateName) {
		String lookupType = getLookupType(templateName);
		return lookupType!=null && mapping.containsKey(lookupType);
	}

	private String getLookupType(String templateName) {
		int idx = templateName.indexOf(":");
		return idx>0 ? templateName.substring(0, idx) : null;
	}
	
	public List<ITemplateResolver> getResolvers() {
		return new ArrayList<ITemplateResolver>(mapping.values());
	}
}
