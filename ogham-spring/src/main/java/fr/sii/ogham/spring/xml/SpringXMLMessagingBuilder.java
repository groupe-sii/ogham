package fr.sii.ogham.spring.xml;

import org.springframework.core.env.Environment;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.MessagingServiceBuilder;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.spring.config.PropertiesBridge;

/**
 * Decorator builder used for helping integration with Spring.
 * 
 * @author Aurélien Baudet
 *
 */
public class SpringXMLMessagingBuilder implements MessagingServiceBuilder {
	Environment environment;

	PropertiesBridge propertiesBridge;

	MessagingBuilder builder;

	/**
	 * Default constructor to let the user use properties instead of constructor
	 */
	public SpringXMLMessagingBuilder() {
		this(null);
	}

	/**
	 * Constructor with the required environment dependency
	 * 
	 * @param environment
	 *            the Spring environment required to read configuration
	 *            properties
	 */
	public SpringXMLMessagingBuilder(Environment environment) {
		this(environment, new PropertiesBridge());
	}

	/**
	 * Constructor with the required environment dependency
	 * 
	 * @param environment
	 *            the Spring environment required to read configuration
	 *            properties
	 * @param propertiesBridge
	 *            the converter to use for getting configuration properties
	 *            values
	 */
	public SpringXMLMessagingBuilder(Environment environment, PropertiesBridge propertiesBridge) {
		this(environment, propertiesBridge, new MessagingBuilder().useAllDefaults(propertiesBridge.convert(environment)));
	}

	/**
	 * Constructor with the required environment dependency, optional bridge and
	 * optional builder.
	 * 
	 * @param environment
	 *            the Spring environment required to read configuration
	 *            properties
	 * @param propertiesBridge
	 *            the converter to use for getting configuration properties
	 *            values
	 * @param builder
	 *            the specific builder to use
	 */
	public SpringXMLMessagingBuilder(Environment environment, PropertiesBridge propertiesBridge, MessagingBuilder builder) {
		super();
		this.environment = environment;
		this.propertiesBridge = propertiesBridge;
		this.builder = builder;
	}

	@Override
	public MessagingService build() {
		return builder.build();
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public void setPropertiesBridge(PropertiesBridge propertiesBridge) {
		this.propertiesBridge = propertiesBridge;
	}

	public void setBuilder(MessagingBuilder builder) {
		this.builder = builder;
	}
}
