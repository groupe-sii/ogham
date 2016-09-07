package fr.sii.ogham.template.thymeleaf.adapter;

import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.ogham.core.resource.resolver.ClassPathResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

/**
 * Adapter that converts general {@link ClassPathResolver} into Thymeleaf
 * specific {@link ClassLoaderTemplateResolver}.
 * 
 * @author Aurélien Baudet
 *
 */
public class ClassPathResolverAdapter extends AbstractSimpleThymeleafResolverAdapter implements ThymeleafResolverAdapter {
	@Override
	public boolean supports(ResourceResolver resolver) {
		return resolver.getActualResourceResolver() instanceof ClassPathResolver;
	}

	@Override
	public ITemplateResolver adapt(ResourceResolver resolver) {
		FixClassLoaderTemplateResolver templateResolver = new FixClassLoaderTemplateResolver();
		templateResolver.setPrefix(getParentPath());
		templateResolver.setSuffix(getExtension());
		return templateResolver;
	}

}
