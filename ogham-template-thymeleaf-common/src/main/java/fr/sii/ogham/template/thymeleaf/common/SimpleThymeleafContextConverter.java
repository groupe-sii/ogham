package fr.sii.ogham.template.thymeleaf.common;

import fr.sii.ogham.core.exception.template.ContextException;
import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.core.template.context.LocaleContext;

/**
 * Simple converter that is able to handle {@link Context} and
 * {@link LocaleContext}.
 * 
 * @author Aurélien Baudet
 *
 */
public class SimpleThymeleafContextConverter implements ThymeleafContextConverter {
	@Override
	public org.thymeleaf.context.Context convert(Context context) throws ContextException {
		org.thymeleaf.context.Context thymeleafContext = new org.thymeleaf.context.Context();
		thymeleafContext.setVariables(context.getVariables());
		if (context instanceof LocaleContext) {
			thymeleafContext.setLocale(((LocaleContext) context).getLocale());
		}
		return thymeleafContext;
	}
}
