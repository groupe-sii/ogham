package fr.sii.ogham.template.thymeleaf.v2.adapter;

import java.util.function.Consumer;

import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import fr.sii.ogham.template.thymeleaf.common.TemplateResolverOptions;
import fr.sii.ogham.template.thymeleaf.common.adapter.TemplateResolverOptionsApplier;

/**
 * Apply options on {@link TemplateResolver} instance (configurable).
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeleafV2TemplateOptionsApplier implements TemplateResolverOptionsApplier {

	@Override
	public void apply(ITemplateResolver templateResolver, TemplateResolverOptions options) {
		if (templateResolver instanceof TemplateResolver) {
			// TODO: handle other options ?
			TemplateResolver resolver = (TemplateResolver) templateResolver;
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
