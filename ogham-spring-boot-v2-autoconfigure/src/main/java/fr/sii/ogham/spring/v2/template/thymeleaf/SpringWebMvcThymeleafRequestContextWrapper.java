package fr.sii.ogham.spring.v2.template.thymeleaf;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.spring5.context.IThymeleafRequestContext;
import org.thymeleaf.spring5.context.webmvc.SpringWebMvcThymeleafRequestContext;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.spring5.view.ThymeleafView;

import fr.sii.ogham.spring.template.thymeleaf.ThymeleafRequestContextWrapper;

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
public class SpringWebMvcThymeleafRequestContextWrapper implements ThymeleafRequestContextWrapper {

	@Override
	public void wrapAndRegister(RequestContext requestContext, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, Map<String, Object> springModel) {
		final SpringWebMvcThymeleafRequestContext thymeleafRequestContext = new SpringWebMvcThymeleafRequestContext(requestContext, request);
		// Add the Thymeleaf RequestContext wrapper that we will be using in
		// this dialect (the bare RequestContext
		// stays in the context to for compatibility with other dialects)
		springModel.put(SpringContextVariableNames.THYMELEAF_REQUEST_CONTEXT, thymeleafRequestContext);
	}

}
