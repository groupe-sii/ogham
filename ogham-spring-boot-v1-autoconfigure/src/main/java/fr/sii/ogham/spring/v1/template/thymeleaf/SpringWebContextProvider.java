package fr.sii.ogham.spring.v1.template.thymeleaf;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafView;

import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.spring.template.thymeleaf.ThymeleafWebContextProvider;
import fr.sii.ogham.spring.util.compat.HttpServletRequestWrapper;
import fr.sii.ogham.spring.util.compat.HttpServletResponseWrapper;
import fr.sii.ogham.spring.util.compat.ServletContextWrapper;

/**
 * Generates an instance of {@link SpringWebContext} for Thymeleaf v2.
 * 
 * This is used to mimic the behavior of {@link ThymeleafView} in order to give
 * access to {@link HttpServletRequest}, {@link HttpServletResponse} and
 * {@link ServletContext} from templates.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class SpringWebContextProvider implements ThymeleafWebContextProvider {

	@Override
	public IContext getWebContext(Context context, IContext base, HttpServletRequestWrapper request, HttpServletResponseWrapper response, ServletContextWrapper servletContext, ApplicationContext applicationContext,
			Map<String, Object> springModel) {
		return new SpringWebContext(request.get(), response.get(), servletContext.get(), base.getLocale(), springModel, applicationContext);
	}

}
