package fr.sii.ogham.spring.v1.template.thymeleaf;

import java.util.Map;

import org.springframework.web.servlet.support.RequestContext;

import fr.sii.ogham.spring.template.thymeleaf.ThymeleafRequestContextWrapper;
import fr.sii.ogham.spring.util.compat.HttpServletRequestWrapper;
import fr.sii.ogham.spring.util.compat.HttpServletResponseWrapper;
import fr.sii.ogham.spring.util.compat.ServletContextWrapper;

/**
 * In Thymeleaf v2, no need to register extra wrapper in model.
 *
 * @author Aurélien Baudet
 *
 */
public class NoOpRequestContextWrapper implements ThymeleafRequestContextWrapper {

	@Override
	public void wrapAndRegister(RequestContext requestContext, HttpServletRequestWrapper request, HttpServletResponseWrapper response, ServletContextWrapper servletContext, Map<String, Object> springModel) {
		// nothing to do if using spring4
	}

}
