package fr.sii.notification.core.template.detector;

import fr.sii.notification.core.exception.template.EngineDetectionException;
import fr.sii.notification.core.template.Template;
import fr.sii.notification.core.template.context.Context;

public class FixedEngineDetector implements TemplateEngineDetector {

	private boolean canParse;
	
	public FixedEngineDetector() {
		this(true);
	}
	
	public FixedEngineDetector(boolean canParse) {
		super();
		this.canParse = canParse;
	}

	@Override
	public boolean canParse(String templateName, Context ctx, Template template) throws EngineDetectionException {
		return canParse;
	}

}
