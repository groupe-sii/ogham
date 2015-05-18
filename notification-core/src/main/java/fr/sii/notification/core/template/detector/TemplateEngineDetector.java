package fr.sii.notification.core.template.detector;

import fr.sii.notification.core.exception.template.EngineDetectionException;
import fr.sii.notification.core.template.Template;
import fr.sii.notification.core.template.context.Context;

public interface TemplateEngineDetector {
	public boolean canParse(String templateName, Context ctx, Template template) throws EngineDetectionException;
}
