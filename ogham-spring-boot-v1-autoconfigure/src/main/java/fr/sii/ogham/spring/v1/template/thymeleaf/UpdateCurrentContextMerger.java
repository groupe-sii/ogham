package fr.sii.ogham.spring.v1.template.thymeleaf;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.IContext;

import fr.sii.ogham.spring.template.thymeleaf.ContextMerger;

/**
 * Add additional variables to an existing Thymeleaf context. It only works if
 * the {@link IContext} instance extends {@link AbstractContext}.
 * 
 * It updates the original context in place returning the same instance.
 * 
 * If you need to set variables on an instance that doesn't inherit from
 * {@link AbstractContext}, you have to provide another implementation.
 * 
 * @author Aurélien Baudet
 *
 */
public class UpdateCurrentContextMerger implements ContextMerger {
	private static final Logger LOG = LoggerFactory.getLogger(UpdateCurrentContextMerger.class);

	@Override
	public IContext mergeVariables(IContext base, Map<String, Object> additionalVariables) {
		if (base instanceof AbstractContext) {
			((AbstractContext) base).setVariables(additionalVariables);
		} else {
			LOG.debug("Not an AbstractContext => skip additional variables");
		}
		return base;
	}

	@Override
	public IContext merge(IContext base, IContext other) {
		for (Entry<String, Object> variable : other.getVariables().entrySet()) {
			if (base instanceof AbstractContext) {
				((AbstractContext) base).setVariable(variable.getKey(), variable.getValue());
			} else {
				LOG.debug("Not an AbstractContext => skip additional variables");
			}
		}
		return base;
	}
}
