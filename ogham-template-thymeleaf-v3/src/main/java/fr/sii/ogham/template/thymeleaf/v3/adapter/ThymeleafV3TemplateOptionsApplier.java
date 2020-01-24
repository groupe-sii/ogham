package fr.sii.ogham.template.thymeleaf.v3.adapter;

import java.util.function.Consumer;

import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.ogham.template.thymeleaf.common.TemplateResolverOptions;
import fr.sii.ogham.template.thymeleaf.common.adapter.TemplateResolverOptionsApplier;

/**
 * Apply options on {@link AbstractConfigurableTemplateResolver} instance.
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeleafV3TemplateOptionsApplier implements TemplateResolverOptionsApplier {

	@Override
	public void apply(ITemplateResolver templateResolver, TemplateResolverOptions options) {
		if (templateResolver instanceof AbstractConfigurableTemplateResolver) {
			// TODO: handle other options ?
			AbstractConfigurableTemplateResolver resolver = (AbstractConfigurableTemplateResolver) templateResolver;
			set(resolver::setTemplateMode, options.getTemplateMode());
			set(resolver::setCacheable, options.getCacheable());
		}
	}

	private <T> void set(Consumer<T> setter, T value) {
		if (value != null) {
			setter.accept(value);
		}
	}

}
