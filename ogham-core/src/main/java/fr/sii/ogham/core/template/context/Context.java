package fr.sii.ogham.core.template.context;

import java.util.Map;

import fr.sii.ogham.core.exception.template.ContextException;

/**
 * A template contains variables. The set of variable values is provided by the
 * context. This interface define the contract for providing variable values.
 * 
 * @author Aurélien Baudet
 *
 */
public interface Context {
	/**
	 * Provide variable substitution values. The map of values is indexed by the
	 * variable names.
	 * 
	 * @return the variables values indexed by the variable names
	 * @throws ContextException
	 *             when the variable values couldn't be generated
	 */
	public Map<String, Object> getVariables() throws ContextException;
}
