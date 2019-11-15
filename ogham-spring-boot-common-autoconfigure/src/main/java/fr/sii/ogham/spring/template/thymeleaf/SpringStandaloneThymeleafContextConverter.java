package fr.sii.ogham.spring.template.thymeleaf;

import java.util.HashMap;
import java.util.Map;

import org.springframework.expression.EvaluationContext;
import org.thymeleaf.context.IContext;

import fr.sii.ogham.core.exception.template.ContextException;
import fr.sii.ogham.template.thymeleaf.common.ThymeleafContextConverter;

/**
 * Specific context converter for Spring that registers static variables and
 * {@link EvaluationContext} for SpEL expressions.
 * 
 * The aim is to provide the almost same support as if user was using Spring in
 * web context (access to Spring beans from templates, be able to use static
 * variables, ...).
 * 
 * @author Aurélien Baudet
 *
 */
public class SpringStandaloneThymeleafContextConverter implements ThymeleafContextConverter {
	private final ThymeleafContextConverter delegate;
	private final String evaluationContextVariableName;
	private final StaticVariablesProvider staticVariablesProvider;
	private final ThymeleafEvaluationContextProvider thymeleafEvaluationContextProvider;
	private final ContextMerger contextMerger;

	public SpringStandaloneThymeleafContextConverter(ThymeleafContextConverter delegate, String evaluationContextVariableName, StaticVariablesProvider staticVariablesProvider,
			ThymeleafEvaluationContextProvider thymeleafEvaluationContextProvider, ContextMerger contextMerger) {
		super();
		this.delegate = delegate;
		this.evaluationContextVariableName = evaluationContextVariableName;
		this.staticVariablesProvider = staticVariablesProvider;
		this.thymeleafEvaluationContextProvider = thymeleafEvaluationContextProvider;
		this.contextMerger = contextMerger;
	}

	@Override
	public IContext convert(fr.sii.ogham.core.template.context.Context context) throws ContextException {
		IContext base = delegate.convert(context);

		// partially borrowed from org.thymeleaf.spring4.view.ThymeleafView
		final Map<String, Object> springModel = new HashMap<>(30);

		final Map<String, Object> templateStaticVariables = staticVariablesProvider.getStaticVariables(context);
		if (templateStaticVariables != null) {
			springModel.putAll(templateStaticVariables);
		}

		final EvaluationContext evaluationContext = thymeleafEvaluationContextProvider.getEvaluationContext(context);
		springModel.put(evaluationContextVariableName, evaluationContext);

		return contextMerger.mergeVariables(base, springModel);
	}

}
