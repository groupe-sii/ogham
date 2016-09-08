package fr.sii.ogham.template.thymeleaf.adapter;

import org.thymeleaf.templateresolver.TemplateResolver;

import fr.sii.ogham.template.thymeleaf.TemplateResolverOptions;

/**
 * Abstract class to handle options configuration for the adapted
 * {@link TemplateResolver}.
 * 
 * @author Cyril Dejonghe
 *
 */
public abstract class AbstractTemplateResolverOptionsAdapter implements TemplateResolverAdapter {
	private TemplateResolverOptions options;

	public TemplateResolverOptions getOptions() {
		return options;
	}

	@Override
	public void setOptions(TemplateResolverOptions options) {
		this.options = options;
	}

	/**
	 * Applies the options to the given {@link TemplateResolver}
	 * 
	 * @param templateResolver
	 *            TemplateResolver to configure
	 */
	protected void applyOptions(TemplateResolver templateResolver) {
		// TODO managing options
	}
}
