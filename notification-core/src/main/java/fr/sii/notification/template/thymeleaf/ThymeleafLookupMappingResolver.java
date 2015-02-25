package fr.sii.notification.template.thymeleaf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.sii.notification.core.exception.TemplateResolutionException;

public class ThymeleafLookupMappingResolver<R> {

	private Map<String, R> mapping;
	
	public ThymeleafLookupMappingResolver() {
		this(new HashMap<String, R>());
	}

	public ThymeleafLookupMappingResolver(Map<String, R> mapping) {
		super();
		this.mapping = mapping;
	}
	
	public void addMapping(String lookup, R resolver) {
		mapping.put(lookup, resolver);
	}

	public R getResolver(String templateName) throws TemplateResolutionException {
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
	
	public List<R> getResolvers() {
		return new ArrayList<R>(mapping.values());
	}
}
