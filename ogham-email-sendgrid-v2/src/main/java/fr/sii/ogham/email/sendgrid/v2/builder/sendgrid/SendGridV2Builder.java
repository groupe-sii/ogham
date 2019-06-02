package fr.sii.ogham.email.sendgrid.v2.builder.sendgrid;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.SendGrid;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.sendgrid.builder.AbstractSendGridBuilder;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.SendGridV2Sender;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.client.DelegateSendGridClient;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.client.SendGridClient;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.client.SendGridInterceptor;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.MapContentHandler;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.MultiContentHandler;
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
 *    .sender(SendGridBuilder.class)    // registers the builder and accesses to that builder for configuring it
 * </code>
 * </pre>
 * 
 * Once the builder is registered, sending email through SendGrid requires
 * either an API key or a username/password pair. You can define it using:
 * 
 * <pre>
 * <code>
 * msgBuilder.email()
 *    .sender(SendGridBuilder.class)    // registers the builder and accesses to that builder for configuring it
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
 *    .sender(SendGridBuilder.class)    // registers the builder and accesses to that builder for configuring it
 *       .host("${custom.property.for.api-key}")
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
 * .sender(SendGridBuilder.class)
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

	private List<String> usernames;
	private List<String> passwords;
	private SendGridClient client;
	private SendGridInterceptor interceptor;

	/**
	 * Default constructor when using SendGrid sender without all Ogham work.
	 * 
	 * <strong>WARNING: use is only if you know what you are doing !</strong>
	 */
	public SendGridV2Builder() {
		this(null);
	}

	/**
	 * Constructor that is called when using Ogham builder:
	 * 
	 * <pre>
	 * MessagingBuilder msgBuilder = ...
	 * msgBuilder
	 * .email()
	 *    .sender(SendGridBuilder.class)
	 * </pre>
	 * 
	 * @param parent
	 *            the parent builder instance for fluent chaining
	 */
	public SendGridV2Builder(EmailBuilder parent) {
		super(SendGridV2Builder.class, parent);
		usernames = new ArrayList<>();
		passwords = new ArrayList<>();
	}

	/**
	 * Set username for SendGrid HTTP API.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .username("foo");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .username("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param username
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public SendGridV2Builder username(String... username) {
		for (String u : username) {
			if (u != null) {
				usernames.add(u);
			}
		}
		return this;
	}

	/**
	 * Set password for SendGrid HTTP API.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .password("foo");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .password("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param password
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public SendGridV2Builder password(String... password) {
		for (String p : password) {
			if (p != null) {
				passwords.add(p);
			}
		}
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
	 * and values using {@link #apiKey(String...)}, {@link #username(String...)}
	 * or {@link #password(String...)} won't be used at all. You then have to
	 * handle it by yourself.
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
	 * .sender(SendGridBuilder.class)
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
		PropertyResolver propertyResolver = environmentBuilder.build();
		String apiKey = BuilderUtils.evaluate(this.apiKeys, propertyResolver, String.class);
		String username = BuilderUtils.evaluate(this.usernames, propertyResolver, String.class);
		String password = BuilderUtils.evaluate(this.passwords, propertyResolver, String.class);
		SendGridClient builtClient = buildClient(apiKey, username, password);
		if (builtClient == null) {
			return null;
		}
		LOG.info("Sending email using SendGrid API is registered");
		LOG.debug("SendGrid account: apiKey={}, username={}", apiKey, username);
		return new SendGridV2Sender(builtClient, buildContentHandler(), interceptor);
	}

	private SendGridClient buildClient(String apiKey, String username, String password) {
		if (client != null) {
			return client;
		}
		if (apiKey != null) {
			return new DelegateSendGridClient(apiKey);
		}
		if (username != null && password != null) {
			return new DelegateSendGridClient(username, password);
		}
		return null;
	}

	private MapContentHandler buildContentHandler() {
		MimeTypeProvider mimetypeProvider = mimetypeBuilder.build();
		MapContentHandler contentHandler = new MapContentHandler();
		contentHandler.register(MultiContent.class, new MultiContentHandler(contentHandler));
		contentHandler.register(StringContent.class, new StringContentHandler(mimetypeProvider));
		return contentHandler;
	}
}
