package fr.sii.ogham.email.sendgrid.v4.builder.sendgrid;

import java.net.URL;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.Client;
import com.sendgrid.SendGrid;

import fr.sii.ogham.core.builder.BuildContext;
import fr.sii.ogham.core.builder.DefaultBuildContext;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.message.content.MayHaveStringContent;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.content.ContentWithAttachments;
import fr.sii.ogham.email.sendgrid.builder.AbstractSendGridBuilder;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.SendGridV4Sender;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.CustomizableUrlClient;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.DelegateSendGridClient;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.SendGridClient;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.SendGridInterceptor;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler.ContentWithAttachmentsHandler;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler.MultiContentHandler;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler.PriorizedContentHandler;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler.StringContentHandler;

/**
 * Configures how SendGrid implementation will send {@link Email}s.
 * 
 * This implementation uses SendGrid HTTP API.
 * 
 * <p>
 * To send {@link Email} using SendGrid, you need to register this builder into
 * a {@link MessagingBuilder} like this:
 * 
 * <pre>
 * <code>
 * MessagingBuilder msgBuilder = ...
 * msgBuilder.email()
 *    .sender(SendGridV4Builder.class)    // registers the builder and accesses to that builder for configuring it
 * </code>
 * </pre>
 * 
 * Once the builder is registered, sending email through SendGrid requires
 * either an API key or a username/password pair. You can define it using:
 * 
 * <pre>
 * <code>
 * msgBuilder.email()
 *    .sender(SendGridV4Builder.class)    // registers the builder and accesses to that builder for configuring it
 *       .apiKey("foo")
 * </code>
 * </pre>
 * 
 * Or you can also use property keys (using interpolation):
 * 
 * <pre>
 * <code>
 * msgBuilder
 * .environment()
 *    .properties()
 *       .set("custom.property.for.api-key", "foo")
 *       .and()
 *    .and()
 * .email()
 *    .sender(SendGridV4Builder.class)    // registers the builder and accesses to that builder for configuring it
 *       .apiKey("${custom.property.for.api-key}")
 * </code>
 * </pre>
 * 
 * <p>
 * Finally, Ogham will transform general {@link Email} object into
 * {@link SendGrid}.Email object. This transformation will fit almost all use
 * cases but you may need to customize a part of the SendGrid message. Instead
 * of doing again the same work Ogham does, this builder allows you to intercept
 * the message to modify it just before sending it:
 * 
 * <pre>
 * <code>
 * .sender(SendGridV4Builder.class)
 *    .intercept(new MyCustomInterceptor())
 * </code>
 * </pre>
 * 
 * See {@link SendGridInterceptor} for more information.
 * 
 * @author Aurélien Baudet
 *
 */
public class SendGridV4Builder extends AbstractSendGridBuilder<SendGridV4Builder, EmailBuilder> {
	private static final Logger LOG = LoggerFactory.getLogger(SendGridV4Builder.class);

	private SendGridClient client;
	private SendGridInterceptor interceptor;
	private Client clientHelper;
	private ConfigurationValueBuilderHelper<SendGridV4Builder, Boolean> unitTestingValueBuilder;

	/**
	 * Default constructor when using SendGrid sender without all Ogham work.
	 * 
	 * <strong>WARNING: use is only if you know what you are doing !</strong>
	 */
	public SendGridV4Builder() {
		this(null, new DefaultBuildContext());
	}

	/**
	 * Constructor that is called when using Ogham builder:
	 * 
	 * <pre>
	 * MessagingBuilder msgBuilder = ...
	 * msgBuilder
	 * .email()
	 *    .sender(SendGridV4Builder.class)
	 * </pre>
	 * 
	 * @param parent
	 *            the parent builder instance for fluent chaining
	 * @param buildContext
	 *            for property resolution and evaluation
	 */
	public SendGridV4Builder(EmailBuilder parent, BuildContext buildContext) {
		super(SendGridV4Builder.class, parent, buildContext);
		unitTestingValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class, buildContext);
	}

	/**
	 * @deprecated SendGrid v4 doesn't use username/password anymore. You must
	 *             use an {@link #apiKey(String)}.
	 */
	@Deprecated
	@Override
	public SendGridV4Builder username(String username) {
		LOG.warn("username and password are no more available with SendGrid v4");
		return this;
	}

	/**
	 * @deprecated SendGrid v4 doesn't use username/password anymore. You must
	 *             use an {@link #apiKey(String)}.
	 */
	@Deprecated
	@Override
	public ConfigurationValueBuilder<SendGridV4Builder, String> username() {
		LOG.warn("username and password are no more available with SendGrid v4");
		return new ConfigurationValueBuilderHelper<>(this, String.class, buildContext);
	}

	/**
	 * @deprecated SendGrid v4 doesn't use username/password anymore. You must
	 *             use an {@link #apiKey(String)}.
	 */
	@Deprecated
	@Override
	public SendGridV4Builder password(String password) {
		LOG.warn("username and password are no more available with SendGrid v4");
		return this;
	}

	/**
	 * @deprecated SendGrid v4 doesn't use username/password anymore. You must
	 *             use an {@link #apiKey(String)}.
	 */
	@Deprecated
	@Override
	public ConfigurationValueBuilder<SendGridV4Builder, String> password() {
		LOG.warn("username and password are no more available with SendGrid v4");
		return new ConfigurationValueBuilderHelper<>(this, String.class, buildContext);
	}

	/**
	 * SendGrid allows to call API for unit tests. Set this to true if you are
	 * unit testing.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #unitTesting()}.
	 * 
	 * <pre>
	 * .unitTesting(true)
	 * .unitTesting()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <pre>
	 * .unitTesting(true)
	 * .unitTesting()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * In both cases, {@code unitTesting(true)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param unitTesting
	 *            true to use SendGrid in unit testing mode
	 * @return this instance for fluent chaining
	 */
	public SendGridV4Builder unitTesting(Boolean unitTesting) {
		unitTestingValueBuilder.setValue(unitTesting);
		return this;
	}

	/**
	 * SendGrid allows to call API for unit tests. Set this to true if you are
	 * unit testing.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .unitTesting()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #unitTesting(Boolean)} takes precedence
	 * over property values and default value.
	 * 
	 * <pre>
	 * .unitTesting(true)
	 * .unitTesting()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * The value {@code true} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<SendGridV4Builder, Boolean> unitTesting() {
		return unitTestingValueBuilder;
	}

	/**
	 * By default, SendGrid uses a {@link Client} instance as an helper to
	 * perform HTTP requests. You may want to use custom client configuration
	 * such as providing custom protocol and port:
	 * 
	 * <pre>
	 * .client(new CustomizableUrlClient(false, "http", 8080))
	 * </pre>
	 * 
	 * NOTE: if you provide a custom {@link Client}, the
	 * {@link #unitTesting(Boolean)} or
	 * {@link #httpClient(org.apache.http.impl.client.CloseableHttpClient)}
	 * configurations are not used. You have to handle it manually.
	 * 
	 * @param clientHelper
	 *            the custom client used to call SendGrid HTTP API
	 * @return this instance for fluent chaining
	 */
	public SendGridV4Builder clientHelper(Client clientHelper) {
		this.clientHelper = clientHelper;
		return this;
	}

	/**
	 * By default, calling SendGrid HTTP API is done through the default
	 * {@link SendGrid} implementation. If you want to use another client
	 * implementation (creating your custom HTTP API caller for example), you
	 * can implement the {@link SendGridClient} interface and provide it:
	 * 
	 * <pre>
	 * .client(new MyCustomHttpApiCaller())
	 * </pre>
	 * 
	 * NOTE: if you provide your custom implementation, any defined properties
	 * and values using {@link #apiKey(String)}, {@link #username(String)} or
	 * {@link #password(String)} won't be used at all. You then have to handle
	 * it by yourself.
	 * 
	 * @param client
	 *            the custom client used to call SendGrid HTTP API
	 * @return this instance for fluent chaining
	 */
	public SendGridV4Builder client(SendGridClient client) {
		this.client = client;
		return this;
	}

	/**
	 * By default, calling SendGrid HTTP API is done through the default
	 * {@link SendGrid} implementation that uses default {@link HttpClient}
	 * (calling {@code HttpClientBuilder.create().build()}). If you want to use
	 * another HTTP client implementation, you can extend the
	 * {@link CloseableHttpClient} class and provide it:
	 * 
	 * <pre>
	 * .client(new MyCustomHttpClient())
	 * </pre>
	 * 
	 * NOTE: if you provide your custom implementation, any defined properties
	 * and values using {@link #unitTesting(Boolean)} won't be used at all. You
	 * then have to handle it by yourself.
	 * 
	 * @param httpClient
	 *            the custom implementation of {@link HttpClient} used to call
	 *            SendGrid HTTP API. SendGrid requires a
	 *            {@link CloseableHttpClient}.
	 * @return this instance for fluent chaining
	 */
	@SuppressWarnings("squid:S1185")
	@Override
	public SendGridV4Builder httpClient(CloseableHttpClient httpClient) {
		return super.httpClient(httpClient);
	}

	/**
	 * Ogham will transform general {@link Email} object into
	 * {@link SendGrid}.Email objects. This transformation will fit almost all
	 * use cases but you may need to customize a part of the SendGrid message.
	 * Instead of doing again the same work Ogham does, this builder allows you
	 * to intercept the message to modify it just before sending it:
	 * 
	 * <pre>
	 * .sender(SendGridV4Builder.class)
	 *    .intercept(new MyCustomInterceptor())
	 * </pre>
	 * 
	 * See {@link SendGridInterceptor} for more information.
	 * 
	 * @param interceptor
	 *            the custom interceptor used to modify {@link SendGrid}.Email
	 * @return this instance for fluent chaining
	 */
	public SendGridV4Builder intercept(SendGridInterceptor interceptor) {
		this.interceptor = interceptor;
		return this;
	}

	@Override
	public SendGridV4Sender build() {
		String apiKey = apiKeyValueBuilder.getValue();
		boolean test = unitTestingValueBuilder.getValue(false);
		URL url = urlValueBuilder.getValue();
		SendGridClient builtClient = buildClient(apiKey, buildClientHelper(clientHelper, httpClient, test, url), url);
		if (builtClient == null) {
			return null;
		}
		LOG.info("Sending email using SendGrid API is registered");
		if (client == null) {
			LOG.debug("SendGrid account: apiKey={}, test={}", apiKey, test);
		} else {
			LOG.debug("SendGrid instance provided so apiKey and unitTesting properties are not used");
		}
		return new SendGridV4Sender(builtClient, buildContentHandler(), buildMimetypeProvider(), interceptor);
	}

	private static Client buildClientHelper(Client clientHelper, CloseableHttpClient httpClient, boolean test, URL url) {
		// custom implementation
		if (clientHelper != null) {
			return clientHelper;
		}
		// case where custom URL is set.
		// SendGrid Client doesn't support neither custom port nor custom
		// protocol
		if (url != null && httpClient != null) {
			return new CustomizableUrlClient(httpClient, url.getProtocol(), url.getPort());
		}
		if (url != null) {
			return new CustomizableUrlClient(test, url.getProtocol(), url.getPort());
		}
		// custom http client
		if (httpClient != null) {
			return new Client(httpClient);
		}
		// test client (just to allow http instead of https)
		if (test) {
			return new Client(true);
		}
		// use default Client implementation created directly by SendGrid
		return null;
	}

	private SendGridClient buildClient(String apiKey, Client client, URL url) {
		if (this.client != null) {
			return this.client;
		}
		if (apiKey != null) {
			return new DelegateSendGridClient(buildSendGrid(apiKey, client, url));
		}
		return null;
	}

	private SendGrid buildSendGrid(String apiKey, Client client, URL url) {
		SendGrid sendGrid = newSendGrid(apiKey, client);
		if (url != null) {
			sendGrid.setHost(url.getHost());
		}
		return sendGrid;
	}

	private static SendGrid newSendGrid(String apiKey, Client client) {
		if (client != null) {
			return new SendGrid(apiKey, client);
		}
		return new SendGrid(apiKey);
	}

	private PriorizedContentHandler buildContentHandler() {
		MimeTypeProvider mimetypeProvider = buildMimetypeProvider();
		PriorizedContentHandler contentHandler = new PriorizedContentHandler();
		contentHandler.register(MultiContent.class, new MultiContentHandler(contentHandler));
		contentHandler.register(ContentWithAttachments.class, new ContentWithAttachmentsHandler(contentHandler));
		contentHandler.register(MayHaveStringContent.class, new StringContentHandler(mimetypeProvider));
		return contentHandler;
	}

	private MimeTypeProvider buildMimetypeProvider() {
		if (mimetypeBuilder == null) {
			return null;
		}
		return mimetypeBuilder.build();
	}
}
