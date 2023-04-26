package fr.sii.ogham.spring.v3.template.thymeleaf;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.WebExpressionContext;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.spring.template.thymeleaf.ThymeleafWebContextProvider;
import fr.sii.ogham.spring.util.compat.HttpServletRequestWrapper;
import fr.sii.ogham.spring.util.compat.HttpServletResponseWrapper;
import fr.sii.ogham.spring.util.compat.ServletContextWrapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Generate an instance of {@link WebExpressionContext} for Thymeleaf v3 with Spring 6.
 *
 * This is used to mimic the behavior of {@link org.thymeleaf.spring6.view.ThymeleafView} in order to give
 * access to {@link HttpServletRequest}, {@link HttpServletResponse} and
 * {@link ServletContext} from templates.
 *
 * @author Aurélien Baudet
 *
 */
public class WebExpressionContextProvider implements ThymeleafWebContextProvider {
	private final ITemplateEngine viewTemplateEngine;

	public WebExpressionContextProvider(ITemplateEngine viewTemplateEngine) {
		super();
		this.viewTemplateEngine = viewTemplateEngine;
	}

	@Override
	public IContext getWebContext(Context context, IContext base, HttpServletRequestWrapper request, HttpServletResponseWrapper response, ServletContextWrapper servletContext, ApplicationContext applicationContext,
								  Map<String, Object> springModel) {
		final IEngineConfiguration configuration = viewTemplateEngine.getConfiguration();
		IServletWebExchange exchange = JakartaServletWebApplication.buildApplication(servletContext.get()).buildExchange(request.get(), response.get());
		return new WebExpressionContext(configuration, exchange, base.getLocale(), springModel);
	}

}
