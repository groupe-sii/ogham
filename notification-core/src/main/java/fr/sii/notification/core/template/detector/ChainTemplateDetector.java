package fr.sii.notification.core.template.detector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.notification.core.exception.template.EngineDetectionException;
import fr.sii.notification.core.template.Template;
import fr.sii.notification.core.template.context.Context;

public class ChainTemplateDetector implements TemplateEngineDetector {

	private List<TemplateEngineDetector> detectors;
	
	public ChainTemplateDetector(TemplateEngineDetector... detectors) {
		this(new ArrayList<>(Arrays.asList(detectors)));
	}
	
	public ChainTemplateDetector(List<TemplateEngineDetector> detectors) {
		super();
		this.detectors = detectors;
	}

	@Override
	public boolean canParse(String templateName, Context ctx, Template template) throws EngineDetectionException {
		for(TemplateEngineDetector detector : detectors) {
			if(detector.canParse(templateName, ctx, template)) {
				return true;
			}
		}
		return false;
	}

	public void addDetector(TemplateEngineDetector detector) {
		detectors.add(detector);
	}
}
