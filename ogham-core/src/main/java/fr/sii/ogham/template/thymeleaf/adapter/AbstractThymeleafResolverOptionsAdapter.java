package fr.sii.ogham.template.thymeleaf.adapter;

import org.thymeleaf.templateresolver.TemplateResolver;

/**
 * Abstract class to handle options configuration for the adapted
 * {@link TemplateResolver}.
 * 
 * @author Cyril Dejonghe
 *
 */
public abstract class AbstractThymeleafResolverOptionsAdapter implements ThymeleafResolverAdapter {
	private ThymeleafResolverOptions options;

	public ThymeleafResolverOptions getOptions() {
		return options;
	}

	@Override
	public void setOptions(ThymeleafResolverOptions options) {
		this.options = options;
	}

	/**
	 * Applies the options to the given {@link TemplateResolver}
	 * 
	 * @param templateResolver
	 *            TemplateResolver to configure
	 */
	protected void applyOptions(TemplateResolver templateResolver) {
		templateResolver.setPrefix(getOptions().getParentPath());
		templateResolver.setSuffix(getOptions().getExtension());
	}
}
