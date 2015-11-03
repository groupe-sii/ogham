package fr.sii.ogham.spring.xml;

import org.springframework.core.env.Environment;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.spring.env.SpringEnvironmentPropertyResolver;

/**
 * Decorator builder used for helping integration with Spring.
 * 
 * @author Aurélien Baudet
 *
 */
public class SpringXMLMessagingBuilder implements Builder<MessagingService> {
	Environment environment;

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
		this(environment, MessagingBuilder.standard().environment().resolver(new SpringEnvironmentPropertyResolver(environment)).and());
	}

	/**
	 * Constructor with the required environment dependency, optional bridge and
	 * optional builder.
	 * 
	 * @param environment
	 *            the Spring environment required to read configuration
	 *            properties
	 * @param builder
	 *            the specific builder to use
	 */
	public SpringXMLMessagingBuilder(Environment environment, MessagingBuilder builder) {
		super();
		this.environment = environment;
		this.builder = builder;
	}

	@Override
	public MessagingService build() {
		return builder.build();
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public void setBuilder(MessagingBuilder builder) {
		this.builder = builder;
	}
}
