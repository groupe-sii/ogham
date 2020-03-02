package fr.sii.ogham.template.common.adapter;

import java.util.List;

import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.resource.path.ResourcePath;

/**
 * As a template path may be only partial (without extension), the only way to
 * determine the path of the template is to generate a potential path and check
 * if it exists.
 * 
 * <p>
 * For example, if there are two template engines registered with associated
 * mappings: Thymeleaf registered with ".html" and ".xhtml" extensions and
 * Freemarker registered with ".html.ftl" and ".html.ftlh".
 * 
 * If the path of the template is "register", it is impossible to know if the
 * right template is "register.html", "register.xhtml", "register.html.ftl" or
 * "register.html.ftlh" without checking if it exists.
 * 
 * So each possible path is tried and the first path that matches an existing
 * resource is used.
 * 
 * @author Aurélien Baudet
 *
 */
public interface CanProvidePossiblePaths {
	/**
	 * Provide the list of possible paths for the template.
	 * 
	 * <p>
	 * As the template path may contain only a partial path (without extension),
	 * the only way to get a path to a template that really exists is to test
	 * each path and check if it exists.
	 * 
	 * <p>
	 * This method provides all paths that are tested for the template.
	 * 
	 * <p>
	 * If the template has no variant or the variant is unknown, then an empty
	 * list is returned.
	 * 
	 * @param template
	 *            the template that is tested with all possible paths
	 * @return the list of possible paths
	 */
	List<ResourcePath> getPossiblePaths(TemplateContent template);
}
