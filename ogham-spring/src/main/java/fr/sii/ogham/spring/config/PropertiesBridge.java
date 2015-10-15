package fr.sii.ogham.spring.config;

import java.util.Properties;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

/**
 * <p>
 * Spring {@link Environment} provides access to properties according to the
 * current environment. But it doesn't provide any way to access all properties.
 * </p>
 * <p>
 * This bridge converts Spring {@link Environment} into {@link Properties}.
 * </p>
 * 
 * @see "https://jira.spring.io/browse/SPR-10241"
 * 
 * @author Aurélien Baudet
 *
 */
public class PropertiesBridge {
	/**
	 * Convert the environment to properties. It only works on
	 * {@link ConfigurableEnvironment} and with {@link PropertySource}s that
	 * implement {@link EnumerablePropertySource}.
	 * 
	 * @param env
	 *            the environment that handles properties in Spring
	 * @return the properties for using it in Ogham
	 */
	public Properties convert(Environment env) {
		Properties rtn = new Properties();
		if (env instanceof ConfigurableEnvironment) {
			for (PropertySource<?> propertySource : ((ConfigurableEnvironment) env).getPropertySources()) {
				if (propertySource instanceof EnumerablePropertySource) {
					for (String key : ((EnumerablePropertySource<?>) propertySource).getPropertyNames()) {
						// do not override if already provided
						// Spring provides property sources in higher priority order
						// first property source has higher priority => overrides values if not set
						if(!rtn.containsKey(key)) {
							rtn.setProperty(key, String.valueOf(propertySource.getProperty(key)));
						}
					}
				}
			}
		}
		return rtn;
	}
}
