package fr.sii.ogham.template.thymeleaf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;

import fr.sii.ogham.core.util.LookupUtils;

/**
 * <p>
 * Decorator resolver that is able to manage lookup prefix. It associates each
 * prefix to a dedicated resolver.
 * </p>
 * <p>
 * The lookup is a prefix that contains at least one ':' character. The lookup
 * prefix is case sensitive. For example, if the path is
 * <code>"classpath:/foo/bar.txt"</code> then the lookup prefix is
 * <code>"classpath:"</code>. If the path is <code>"foo:bar:/foobar.txt"</code>
 * then the lookup prefix is <code>"foo:bar:"</code>.
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
public class ThymeleafLookupMappingResolver implements ITemplateResolver {

	private Map<String, ITemplateResolver> mapping;

	public ThymeleafLookupMappingResolver() {
		this(new HashMap<String, ITemplateResolver>());
	}

	public ThymeleafLookupMappingResolver(Map<String, ITemplateResolver> mapping) {
		super();
		this.mapping = mapping;
	}

	/**
	 * Add a resolver for the associated lookup. If a resolver already exists
	 * with the same lookup, the new provided resolver will replace it.
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
		return LookupUtils.getResolver(mapping, templateName);
	}

	/**
	 * Extract the name of the template (remove the lookup)
	 * 
	 * @param templateName
	 *            the name of the template with lookup
	 * @return the name of the template without lookup
	 */
	public String getTemplateName(String templateName) {
		return LookupUtils.getRealPath(mapping, templateName);
	}

	@Override
	public String getName() {
		return "LookupMappingResolver";
	}

	@Override
	public Integer getOrder() {
		return 0;
	}

	@Override
	public TemplateResolution resolveTemplate(TemplateProcessingParameters templateProcessingParameters) {
		return getResolver(templateProcessingParameters.getTemplateName()).resolveTemplate(
				new TemplateProcessingParameters(templateProcessingParameters.getConfiguration(), getTemplateName(templateProcessingParameters.getTemplateName()), templateProcessingParameters
						.getContext()));
	}

	@Override
	public void initialize() {
		for (ITemplateResolver resolver : getResolvers()) {
			resolver.initialize();
		}
	}

	public List<ITemplateResolver> getResolvers() {
		return new ArrayList<ITemplateResolver>(mapping.values());
	}

}
