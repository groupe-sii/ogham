package fr.sii.ogham.template.thymeleaf.common.adapter;

import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.ogham.template.thymeleaf.common.TemplateResolverOptions;

/**
 * Abstract class to handle options configuration for the adapted
 * {@link ITemplateResolver}.
 * 
 * @author Cyril Dejonghe
 *
 */
public abstract class AbstractTemplateResolverOptionsAdapter implements TemplateResolverAdapter {
	private final TemplateResolverOptionsApplier optionsSetter;
	private TemplateResolverOptions options;
	

	public AbstractTemplateResolverOptionsAdapter(TemplateResolverOptionsApplier optionsSetter) {
		super();
		this.optionsSetter = optionsSetter;
	}

	public TemplateResolverOptions getOptions() {
		return options;
	}

	@Override
	public void setOptions(TemplateResolverOptions options) {
		this.options = options;
	}

	/**
	 * Applies the options to the given {@link ITemplateResolver}
	 * 
	 * @param templateResolver
	 *            TemplateResolver to configure
	 */
	protected void applyOptions(ITemplateResolver templateResolver) {
		optionsSetter.apply(templateResolver, options);
	}
}
