package fr.sii.ogham.template.thymeleaf.resolver;

import org.thymeleaf.templateresolver.TemplateResolver;

/**
 * Template resolver that provides the template directly using the template
 * string (nothing to load or resolve).
 * 
 * @author Aurélien Baudet
 *
 */
public class StringTemplateResolver extends TemplateResolver {
    public StringTemplateResolver() {
        super();
        super.setResourceResolver(new StringResourceResolver());
    }
}
