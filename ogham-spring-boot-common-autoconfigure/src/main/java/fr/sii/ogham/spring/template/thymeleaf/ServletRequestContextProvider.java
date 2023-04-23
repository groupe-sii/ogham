package fr.sii.ogham.spring.template.thymeleaf;

import java.util.Map;

import org.springframework.web.servlet.support.RequestContext;

import fr.sii.ogham.spring.util.compat.HttpServletRequestWrapper;
import fr.sii.ogham.spring.util.compat.HttpServletResponseWrapper;
import fr.sii.ogham.spring.util.compat.ServletContextWrapper;

/**
 * A simple indirection since Spring Boot 3 which
 * uses Jakarta instead of Javax now.
 * So we need to delegate creation of {@link RequestContext} to
 * because it won't compile here.
 * 
 * @author Aurélien Baudet
 *
 */
public interface ServletRequestContextProvider {
	RequestContext getRequestContext(HttpServletRequestWrapper request, HttpServletResponseWrapper response, ServletContextWrapper servletContext, Map<String, Object> springModel);
}
