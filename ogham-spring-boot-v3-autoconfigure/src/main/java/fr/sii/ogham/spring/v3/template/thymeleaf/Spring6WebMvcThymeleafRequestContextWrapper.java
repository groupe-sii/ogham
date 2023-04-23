package fr.sii.ogham.spring.v3.template.thymeleaf;

import java.util.Map;

import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.spring6.context.IThymeleafRequestContext;
import org.thymeleaf.spring6.context.webmvc.SpringWebMvcThymeleafRequestContext;
import org.thymeleaf.spring6.naming.SpringContextVariableNames;
import org.thymeleaf.spring6.view.ThymeleafView;

import fr.sii.ogham.spring.template.thymeleaf.ThymeleafRequestContextWrapper;
import fr.sii.ogham.spring.util.compat.HttpServletRequestWrapper;
import fr.sii.ogham.spring.util.compat.HttpServletResponseWrapper;
import fr.sii.ogham.spring.util.compat.ServletContextWrapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Wraps the {@link RequestContext} into a {@link IThymeleafRequestContext} and
 * registers it into the model as a variable.
 * 
 * This is used to mimic the behavior of {@link ThymeleafView} in order to give
 * access to {@link HttpServletRequest}, {@link HttpServletResponse} and
 * {@link ServletContext} from templates.
 * 
 * @author Aurélien Baudet
 *
 */
public class Spring6WebMvcThymeleafRequestContextWrapper implements ThymeleafRequestContextWrapper {

	@Override
	public void wrapAndRegister(RequestContext requestContext, HttpServletRequestWrapper request, HttpServletResponseWrapper response, ServletContextWrapper servletContext, Map<String, Object> springModel) {
		final SpringWebMvcThymeleafRequestContext thymeleafRequestContext = new SpringWebMvcThymeleafRequestContext(requestContext, request.get());
		// Add the Thymeleaf RequestContext wrapper that we will be using in
		// this dialect (the bare RequestContext
		// stays in the context to for compatibility with other dialects)
		springModel.put(SpringContextVariableNames.THYMELEAF_REQUEST_CONTEXT, thymeleafRequestContext);
	}

}
