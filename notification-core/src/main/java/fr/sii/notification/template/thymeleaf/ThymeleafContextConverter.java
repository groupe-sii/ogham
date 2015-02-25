package fr.sii.notification.template.thymeleaf;

import fr.sii.notification.core.exception.ContextException;
import fr.sii.notification.core.template.context.Context;

public class ThymeleafContextConverter {
	public org.thymeleaf.context.Context convert(Context context) throws ContextException {
		org.thymeleaf.context.Context thymeleafContext = new org.thymeleaf.context.Context();
		thymeleafContext.setVariables(context.getVariables());
		return thymeleafContext;
	}
}
