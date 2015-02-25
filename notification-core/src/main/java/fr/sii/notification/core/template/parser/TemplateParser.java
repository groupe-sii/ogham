package fr.sii.notification.core.template.parser;

import fr.sii.notification.core.exception.ParseException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.template.context.Context;

public interface TemplateParser {
	public Content parse(String templateName, Context ctx) throws ParseException;
}
