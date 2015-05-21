package fr.sii.notification.template.thymeleaf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * <p>
 * Decorator resolver that is able to manage lookup prefix. It associates each
 * prefix to a dedicated resolver. The lookup prefix is case sensitive and must
 * end with a ':'. It must not contain another ':' character.
 * </p>
 * <p>
 * For example, a template path could be "classpath:/email/hello.html". The
 * lookup prefix is "classpath:".
 * </p>
 * <p>
 * The lookup can also be empty in order to define a kind of default resolver if
 * no lookup is provided. The template path could then be "/email/hello.html".
 * The resolver associated to empty string lookup will be used in this case.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeleafLookupMappingResolver {

	private Map<String, ITemplateResolver> mapping;

	public ThymeleafLookupMappingResolver() {
		this(new HashMap<String, ITemplateResolver>());
	}

	public ThymeleafLookupMappingResolver(Map<String, ITemplateResolver> mapping) {
		super();
		this.mapping = mapping;
	}

	/**
	 * Add a resolver for the associated lookup.
	 * 
	 * @param lookup
	 *            the lookup string without the ':' character (example:
	 *            "classpath")
	 * @param resolver
	 *            the resolver to call for the lookup string
	 */
	public void addMapping(String lookup, ITemplateResolver resolver) {
		mapping.put(lookup, resolver);
	}

	/**
	 * Get the resolver according to the template name.
	 * 
	 * @param templateName
	 *            the name of the template
	 * @return the template resolver to use
	 */
	public ITemplateResolver getResolver(String templateName) {
		return mapping.get(getLookupType(templateName));
	}

	/**
	 * Extract the name of the template (remove the lookup)
	 * 
	 * @param templateName
	 *            the name of the template with lookup
	 * @return the name of the template without lookup
	 */
	public String getTemplateName(String templateName) {
		int idx = templateName.indexOf(":");
		return idx > 0 ? templateName.substring(idx + 1) : templateName;
	}

	private String getLookupType(String templateName) {
		int idx = templateName.indexOf(":");
		return idx > 0 ? templateName.substring(0, idx) : "";
	}

	public List<ITemplateResolver> getResolvers() {
		return new ArrayList<ITemplateResolver>(mapping.values());
	}
}
