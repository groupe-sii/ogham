package fr.sii.ogham.email.sendgrid.v2.builder.sendgrid;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.SendGrid;

import fr.sii.ogham.core.builder.BuildContext;
import fr.sii.ogham.core.builder.DefaultBuildContext;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.message.content.MayHaveStringContent;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.content.ContentWithAttachments;
import fr.sii.ogham.email.sendgrid.builder.AbstractSendGridBuilder;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.SendGridV2Sender;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.client.DelegateSendGridClient;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.client.SendGridClient;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.client.SendGridInterceptor;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.ContentWithAttachmentsHandler;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.MultiContentHandler;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.PriorizedContentHandler;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.StringContentHandler;

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
 *    .sender(SendGridV2Builder.class)    // registers the builder and accesses to that builder for configuring it
 * </code>
 * </pre>
 * 
 * Once the builder is registered, sending email through SendGrid requires
 * either an API key or a username/password pair. You can define it using:
 * 
 * <pre>
 * <code>
 * msgBuilder.email()
 *    .sender(SendGridV2Builder.class)    // registers the builder and accesses to that builder for configuring it
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
 *    .sender(SendGridV2Builder.class)    // registers the builder and accesses to that builder for configuring it
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
 * .sender(SendGridV2Builder.class)
 *    .intercept(new MyCustomInterceptor())
 * </code>
 * </pre>
 * 
 * See {@link SendGridInterceptor} for more information.
 * 
 * @author Aurélien Baudet
 *
 */
public class SendGridV2Builder extends AbstractSendGridBuilder<SendGridV2Builder, EmailBuilder> {
	private static final Logger LOG = LoggerFactory.getLogger(SendGridV2Builder.class);

	private final ConfigurationValueBuilderHelper<SendGridV2Builder, String> usernameValueBuilder;
	private final ConfigurationValueBuilderHelper<SendGridV2Builder, String> passwordValueBuilder;
	private SendGridClient client;
	private SendGridInterceptor interceptor;

	/**
	 * Default constructor when using SendGrid sender without all Ogham work.
	 * 
	 * <strong>WARNING: use is only if you know what you are doing !</strong>
	 */
	public SendGridV2Builder() {
		this(null, new DefaultBuildContext());
	}

	/**
	 * Constructor that is called when using Ogham builder:
	 * 
	 * <pre>
	 * MessagingBuilder msgBuilder = ...
	 * msgBuilder
	 * .email()
	 *    .sender(SendGridV2Builder.class)
	 * </pre>
	 * 
	 * @param parent
	 *            the parent builder instance for fluent chaining
	 * @param buildContext
	 *            for property resolution and evaluation
	 */
	public SendGridV2Builder(EmailBuilder parent, BuildContext buildContext) {
		super(SendGridV2Builder.class, parent, buildContext);
		usernameValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class, buildContext);
		passwordValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class, buildContext);
	}

	@Override
	public SendGridV2Builder username(String username) {
		usernameValueBuilder.setValue(username);
		return this;
	}

	@Override
	public ConfigurationValueBuilder<SendGridV2Builder, String> username() {
		return usernameValueBuilder;
	}

	@Override
	public SendGridV2Builder password(String password) {
		passwordValueBuilder.setValue(password);
		return this;
	}

	@Override
	public ConfigurationValueBuilder<SendGridV2Builder, String> password() {
		return passwordValueBuilder;
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
	public SendGridV2Builder client(SendGridClient client) {
		this.client = client;
		return this;
	}

	/**
	 * Ogham will transform general {@link Email} object into
	 * {@link SendGrid}.Email objects. This transformation will fit almost all
	 * use cases but you may need to customize a part of the SendGrid message.
	 * Instead of doing again the same work Ogham does, this builder allows you
	 * to intercept the message to modify it just before sending it:
	 * 
	 * <pre>
	 * .sender(SendGridV2Builder.class)
	 *    .intercept(new MyCustomInterceptor())
	 * </pre>
	 * 
	 * See {@link SendGridInterceptor} for more information.
	 * 
	 * @param interceptor
	 *            the custom interceptor used to modify {@link SendGrid}.Email
	 * @return this instance for fluent chaining
	 */
	public SendGridV2Builder intercept(SendGridInterceptor interceptor) {
		this.interceptor = interceptor;
		return this;
	}

	@Override
	public SendGridV2Sender build() {
		String apiKey = apiKeyValueBuilder.getValue();
		String username = usernameValueBuilder.getValue();
		String password = passwordValueBuilder.getValue();
		URL url = urlValueBuilder.getValue();
		SendGridClient builtClient = buildClient(apiKey, username, password, url);
		if (builtClient == null) {
			return null;
		}
		LOG.info("Sending email using SendGrid API is registered");
		LOG.debug("SendGrid account: apiKey={}, username={}", apiKey, username);
		return new SendGridV2Sender(builtClient, buildContentHandler(), interceptor);
	}

	private SendGridClient buildClient(String apiKey, String username, String password, URL url) {
		if (client != null) {
			return client;
		}
		if (apiKey != null || (username != null && password != null)) {
			return new DelegateSendGridClient(buildSendGrid(apiKey, username, password, url));
		}
		return null;
	}

	private SendGrid buildSendGrid(String apiKey, String username, String password, URL url) {
		SendGrid sendGrid = newSendGrid(apiKey, username, password);
		if (url != null) {
			sendGrid.setUrl(url.toString());
		}
		if (httpClient != null) {
			sendGrid.setClient(httpClient);
		}
		return sendGrid;
	}

	private static SendGrid newSendGrid(String apiKey, String username, String password) {
		if (apiKey != null) {
			return new SendGrid(apiKey);
		}
		return new SendGrid(username, password);
	}

	private PriorizedContentHandler buildContentHandler() {
		MimeTypeProvider mimetypeProvider = mimetypeBuilder.build();
		PriorizedContentHandler contentHandler = new PriorizedContentHandler();
		contentHandler.register(MultiContent.class, new MultiContentHandler(contentHandler));
		contentHandler.register(ContentWithAttachments.class, new ContentWithAttachmentsHandler(contentHandler));
		contentHandler.register(MayHaveStringContent.class, new StringContentHandler(mimetypeProvider));
		return contentHandler;
	}
}
