package fr.sii.ogham.spring.v1.template.thymeleaf;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.support.RequestContext;

import fr.sii.ogham.spring.template.thymeleaf.ThymeleafRequestContextWrapper;

/**
 * In Thymeleaf v2, no need to register extra wrapper in model.
 * 
 * @author Aurélien Baudet
 *
 */
public class NoOpRequestContextWrapper implements ThymeleafRequestContextWrapper {

	@Override
	public void wrapAndRegister(RequestContext requestContext, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, Map<String, Object> springModel) {
		// nothing to do if using spring4
	}

}
