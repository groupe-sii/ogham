package fr.sii.ogham.spring.v3.template.thymeleaf;

import fr.sii.ogham.spring.template.thymeleaf.ServletRequestContextProvider;
import fr.sii.ogham.spring.util.compat.HttpServletRequestWrapper;
import fr.sii.ogham.spring.util.compat.HttpServletResponseWrapper;
import fr.sii.ogham.spring.util.compat.ServletContextWrapper;
import org.springframework.web.servlet.support.RequestContext;

import java.util.Map;

public class RequestContextProvider implements ServletRequestContextProvider {

	@Override
	public RequestContext getRequestContext(HttpServletRequestWrapper request, HttpServletResponseWrapper response, ServletContextWrapper servletContext, Map<String, Object> springModel) {
		return new RequestContext(request.get(), response.get(), servletContext.get(), springModel);
	}

}
