package fr.sii.ogham.core.builder.template;

import fr.sii.ogham.core.template.detector.TemplateEngineDetector;

public interface DetectorBuilder<MYSELF> {
	MYSELF detector(TemplateEngineDetector detector);
	
	TemplateEngineDetector buildDetector();
}
