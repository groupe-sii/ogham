package fr.sii.ogham.spring.template.thymeleaf;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.springframework.context.ApplicationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.thymeleaf.context.IContext;

import fr.sii.ogham.core.exception.template.ContextException;
import fr.sii.ogham.spring.util.compat.HttpServletRequestWrapper;
import fr.sii.ogham.spring.util.compat.HttpServletResponseWrapper;
import fr.sii.ogham.spring.util.compat.ServletContextWrapper;
import fr.sii.ogham.template.thymeleaf.common.ThymeleafContextConverter;

/**
 * Specific context converter for Spring that registers static variables and
 * {@link EvaluationContext} for SpEL expressions.
 * 
 * The aim is to provide the same support as if user was using Spring in web
 * context (access to Spring beans from templates, be able to use static
 * variables, ...).
 * 
 * @author Aurélien Baudet
 *
 */
public class SpringWebThymeleafContextConverter implements ThymeleafContextConverter {
	private final ThymeleafContextConverter delegate;
	private final String springRequestContextVariableName;
	private final ApplicationContext applicationContext;
	private final WebContextProvider webContextProvider;
	private final ThymeleafRequestContextWrapper thymeleafRequestContextWrapper;
	private final ThymeleafWebContextProvider thymeleafWebContextProvider;
	private final ContextMerger contextMerger;
	private final ServletRequestContextProvider requestContextProvider;

	public SpringWebThymeleafContextConverter(ThymeleafContextConverter delegate, String springRequestContextVariableName, ApplicationContext applicationContext, WebContextProvider webContextProvider,
			ThymeleafRequestContextWrapper thymeleafRequestContextWrapper, ThymeleafWebContextProvider thymeleafWebContextProvider, ContextMerger contextMerger,
			ServletRequestContextProvider requestContextProvider) {
		super();
		this.delegate = delegate;
		this.springRequestContextVariableName = springRequestContextVariableName;
		this.applicationContext = applicationContext;
		this.webContextProvider = webContextProvider;
		this.thymeleafRequestContextWrapper = thymeleafRequestContextWrapper;
		this.thymeleafWebContextProvider = thymeleafWebContextProvider;
		this.contextMerger = contextMerger;
		this.requestContextProvider = requestContextProvider;
	}

	/*
	 * If this is not null, we are using Spring 3.1+ and there is the
	 * possibility to automatically add @PathVariable's to models. This will be
	 * computed at class initialization time.
	 */
	private static final String pathVariablesSelector;

	static {

		/*
		 * Compute whether we can obtain @PathVariable's from the request and
		 * add them automatically to the model (Spring 3.1+)
		 */

		String pathVariablesSelectorValue = null;
		try {
			// We are looking for the value of the View.PATH_VARIABLES constant,
			// which is a String
			final Field pathVariablesField = View.class.getDeclaredField("PATH_VARIABLES");
			pathVariablesSelectorValue = (String) pathVariablesField.get(null);
		} catch (final NoSuchFieldException | IllegalAccessException ignored) {
			pathVariablesSelectorValue = null;
		}
		pathVariablesSelector = pathVariablesSelectorValue;
	}

	@Override
	public IContext convert(fr.sii.ogham.core.template.context.Context context) throws ContextException {
		IContext base = delegate.convert(context);
		
		// the web context may be lost due to @Async method call
		if (isAsyncCall()) {
			return base;
		}

		// partially borrowed from org.thymeleaf.spring5.view.ThymeleafView
		final Map<String, Object> springModel = new HashMap<>(30);

		HttpServletRequestWrapper request = webContextProvider.getRequest(context);
		HttpServletResponseWrapper response = webContextProvider.getResponse(context);
		ServletContextWrapper servletContext = webContextProvider.getServletContext(context);

		if (pathVariablesSelector != null) {
			@SuppressWarnings("unchecked")
			final Map<String, Object> pathVars = (Map<String, Object>) request.getAttribute(pathVariablesSelector);
			if (pathVars != null) {
				springModel.putAll(pathVars);
			}
		}

		final RequestContext requestContext = requestContextProvider.getRequestContext(request, response, servletContext, springModel);

		// For compatibility with ThymeleafView
		addRequestContextAsVariable(springModel, springRequestContextVariableName, requestContext);
		// For compatibility with AbstractTemplateView
		addRequestContextAsVariable(springModel, AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, requestContext);

		thymeleafRequestContextWrapper.wrapAndRegister(requestContext, request, response, servletContext, springModel);

		return contextMerger.merge(thymeleafWebContextProvider.getWebContext(context, base, request, response, servletContext, applicationContext, springModel), base);
	}

	private boolean isAsyncCall() {
		try {
			RequestContextHolder.currentRequestAttributes();
			return false;
		} catch(IllegalStateException e) {
			return true;
		}
	}

	protected static void addRequestContextAsVariable(final Map<String, Object> model, final String variableName, final RequestContext requestContext) throws ContextException {

		if (model.containsKey(variableName)) {
			throw new ContextException(new ServletException("Cannot expose request context in model attribute '" + variableName + "' because of an existing model object of the same name"));
		}
		model.put(variableName, requestContext);

	}

}
