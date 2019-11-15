package fr.sii.ogham.core.template.context;

import static java.util.Collections.emptyMap;

import java.util.Map;

import fr.sii.ogham.core.exception.template.ContextException;

/**
 * Context used when no variables is used. This is used to avoid
 * {@link NullPointerException} and many null checks.
 * 
 * @author Aurélien Baudet
 *
 */
public class NullContext implements Context {

	@Override
	public Map<String, Object> getVariables() throws ContextException {
		return emptyMap();
	}

	@Override
	public String toString() {
		return "NullContext";
	}

}
