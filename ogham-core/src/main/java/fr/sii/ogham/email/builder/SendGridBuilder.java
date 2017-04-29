package fr.sii.ogham.email.builder;

import java.util.Properties;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.mimetype.FallbackMimeTypeProvider;
import fr.sii.ogham.core.mimetype.FixedMimeTypeProvider;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.mimetype.TikaProvider;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.email.EmailConstants.SendGridConstants;
import fr.sii.ogham.email.sender.impl.SendGridSender;
import fr.sii.ogham.email.sender.impl.sendgrid.client.DelegateSendGridClient;
import fr.sii.ogham.email.sender.impl.sendgrid.client.SendGridClient;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.MapContentHandler;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.MultiContentHandler;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.SendGridContentHandler;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.StringContentHandler;

/**
 * Builder for the SendGrid-backed sender. It can only build instances using
 * default parameters.
 */
public final class SendGridBuilder implements Builder<SendGridSender> {
	/**
	 * The SendGrid client the built {@link SendGridSender} will use.
	 */
	private SendGridClient client;

	/**
	 * The content handler to use. By default, it uses a
	 * {@link MapContentHandler}.
	 */
	private SendGridContentHandler contentHandler;

	/**
	 * The content handler that associates the content class to the content
	 * handler implementation
	 */
	private MapContentHandler mapContentHandler;

	/**
	 * The provider for Mime Type detection
	 */
	private FallbackMimeTypeProvider mimetypeProvider;

	/**
	 * The account user
	 */
	private String username;

	/**
	 * The account password
	 */
	private String password;

	/**
	 * The API key
	 */
	private String apiKey;

	/**
	 * Constructor.
	 */
	public SendGridBuilder() {
		mapContentHandler = new MapContentHandler();
		contentHandler = mapContentHandler;
		mimetypeProvider = new FallbackMimeTypeProvider();
	}

	/**
	 * Tells the builder to use all default behaviors and values:
	 * <ul>
	 * <li>Use the system properties for credentials</li>
	 * <li>Register Mime Type detection using MimeMagic library</li>
	 * <li>Register default Mime Type (text/plain)</li>
	 * <li>Handle {@link MultiContent}</li>
	 * <li>Handle {@link StringContent}</li>
	 * </ul>
	 * 
	 * @return this instance for fluent use
	 */
	public SendGridBuilder useDefaults() {
		useDefaults(BuilderUtils.getDefaultProperties());
		return this;
	}

	/**
	 * Tells the builder to use all default behaviors and values:
	 * <ul>
	 * <li>Use the provided properties for credentials</li>
	 * <li>Register Mime Type detection using MimeMagic library</li>
	 * <li>Register default Mime Type (text/plain)</li>
	 * <li>Handle {@link MultiContent}</li>
	 * <li>Handle {@link StringContent}</li>
	 * </ul>
	 * 
	 * @param props
	 *            the properties to use
	 * @return this instance for fluent use
	 */
	public SendGridBuilder useDefaults(Properties props) {
		return useDefaults(BuilderUtils.getDefaultPropertyResolver(props));
	}
	
	/**
	 * Tells the builder to use all default behaviors and values:
	 * <ul>
	 * <li>Use the provided properties for credentials</li>
	 * <li>Register Mime Type detection using MimeMagic library</li>
	 * <li>Register default Mime Type (text/plain)</li>
	 * <li>Handle {@link MultiContent}</li>
	 * <li>Handle {@link StringContent}</li>
	 * </ul>
	 * 
	 * @param propertyResolver
	 *            the property resolver used to get properties values
	 * @return this instance for fluent use
	 */
	public SendGridBuilder useDefaults(PropertyResolver propertyResolver) {
		withCredentials(propertyResolver.getProperty(SendGridConstants.USERNAME), propertyResolver.getProperty(SendGridConstants.PASSWORD));
		withApiKey(propertyResolver.getProperty(SendGridConstants.API_KEY));
		registerMimeTypeProvider(new TikaProvider());
		registerMimeTypeProvider(new FixedMimeTypeProvider());
		registerContentHandler(MultiContent.class, new MultiContentHandler(mapContentHandler));
		registerContentHandler(StringContent.class, new StringContentHandler(mimetypeProvider));
		return this;
	}

	/**
	 * <p>
	 * Register a new Mime Type provider. Registering several providers allows
	 * to try detecting using the first one. If it can't detect the mimetype, it
	 * tries with the next one until one detects successfully the Mime Type.
	 * </p>
	 * <p>
	 * The provider is added at the end so any previously registered provider
	 * that is able to provide a Mime Type prevents to use this provider.
	 * </p>
	 * 
	 * @param provider
	 *            the provider to register
	 * @return this instance for fluent use
	 */
	public SendGridBuilder registerMimeTypeProvider(MimeTypeProvider provider) {
		mimetypeProvider.addProvider(provider);
		return this;
	}

	/**
	 * Register a new handler for a specific content.
	 * 
	 * @param clazz
	 *            the class of the content to handle
	 * @param handler
	 *            the handler
	 * @return this instance for fluent use
	 */
	public SendGridBuilder registerContentHandler(Class<? extends Content> clazz, SendGridContentHandler handler) {
		mapContentHandler.register(clazz, handler);
		return this;
	}

	/**
	 * Configures the builder to create senders that connect to SendGrid using
	 * the provided credentials.
	 * 
	 * @param username
	 *            the SendGrid username
	 * @param password
	 *            the SendGrid password
	 * @return the current instance for fluent use
	 */
	public SendGridBuilder withCredentials(final String username, final String password) {
		this.username = username;
		this.password = password;
		return this;
	}

	/**
	 * Configures the builder to create senders that connect to SendGrid using
	 * the provided API key.
	 * 
	 * @param apiKey
	 *            the SendGrid API key
	 * @return the current instance for fluent use
	 */
	public SendGridBuilder withApiKey(final String apiKey) {
		this.apiKey = apiKey;
		return this;
	}

	/**
	 * Sets an alternative {@link SendGridClient} instance to be used.
	 * 
	 * @param client
	 *            the new client instance
	 * @return the current instance for fluent use
	 */
	public SendGridBuilder withClient(final SendGridClient client) {
		this.client = client;
		return this;
	}

	@Override
	public SendGridSender build() throws BuildException {
		if (client == null) {
			if(username!=null && password!=null) {
				client = new DelegateSendGridClient(username, password);
			} else {
				client = new DelegateSendGridClient(apiKey);
			}
		}

		return new SendGridSender(client, contentHandler);
	}

}
