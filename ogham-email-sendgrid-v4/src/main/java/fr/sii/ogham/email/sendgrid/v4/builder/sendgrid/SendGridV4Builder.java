package fr.sii.ogham.email.sendgrid.v4.builder.sendgrid;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.Client;
import com.sendgrid.SendGrid;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.message.content.MayHaveStringContent;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.sendgrid.builder.AbstractSendGridBuilder;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.SendGridV4Sender;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.DelegateSendGridClient;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.SendGridClient;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.SendGridInterceptor;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler.MapContentHandler;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler.MultiContentHandler;
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
public class SendGridV4Builder extends AbstractSendGridBuilder<SendGridV4Builder, EmailBuilder> {
	private static final Logger LOG = LoggerFactory.getLogger(SendGridV4Builder.class);

	private SendGridClient client;
	private SendGridInterceptor interceptor;
	private Client clientHelper;
	private boolean unitTesting;
	private List<String> unitTestingKeys;
	
	/**
	 * Default constructor when using SendGrid sender without all Ogham work.
	 * 
	 * <strong>WARNING: use is only if you know what you are doing !</strong>
	 */
	public SendGridV4Builder() {
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
	public SendGridV4Builder(EmailBuilder parent) {
		super(SendGridV4Builder.class, parent);
		unitTestingKeys = new ArrayList<>();
	}

	/**
	 * @deprecated SendGrid v4 doesn't use username/password anymore. You must use an {@link #apiKey(String...)}.
	 * 
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
	@Override
	@Deprecated
	public SendGridV4Builder username(String... username) {
		LOG.warn("username and password are no more available with SendGrid v4");
		return this;
	}

	/**
	 * @deprecated SendGrid v4 doesn't use username/password anymore. You must use an {@link #apiKey(String...)}.
	 * 
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
	@Override
	@Deprecated
	public SendGridV4Builder password(String... password) {
		LOG.warn("username and password are no more available with SendGrid v4");
		return this;
	}

	/**
	 * SendGrid allows to call API for unit tests. Set this to true if you are
	 * unit testing.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .unitTesting("true");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .unitTesting("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param key
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public SendGridV4Builder unitTesting(String... key) {
		for (String k : key) {
			if (k != null) {
				unitTestingKeys.add(k);
			}
		}
		return this;
	}

	/**
	 * SendGrid allows to call API for unit tests. Set this to true if you are
	 * unit testing.
	 * 
	 * <pre>
	 * .unitTesting(true)
	 * </pre>
	 * 
	 * @param test
	 *            true if you are unit testing
	 * @return this instance for fluent chaining
	 */
	public SendGridV4Builder unitTesting(boolean test) {
		this.unitTesting = test;
		return this;
	}

	/**
	 * By default, SendGrid uses a {@link Client} instance as an helper to
	 * perform HTTP requests. You may want to use custom client configuration
	 * such as providing custom {@link HttpClient}:
	 * 
	 * <pre>
	 * .client(new Client(new MyCustomHttpClient()))
	 * </pre>
	 * 
	 * NOTE: if you provide a custom {@link Client}, the test 
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
	 * and values using {@link #apiKey(String...)}, {@link #username(String...)}
	 * or {@link #password(String...)} won't be used at all. You then have to
	 * handle it by yourself.
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
	public SendGridV4Builder intercept(SendGridInterceptor interceptor) {
		this.interceptor = interceptor;
		return this;
	}

	@Override
	public SendGridV4Sender build() {
		PropertyResolver propertyResolver = environmentBuilder.build();
		String apiKey = BuilderUtils.evaluate(this.apiKeys, propertyResolver, String.class);
		Boolean test = BuilderUtils.evaluate(this.unitTestingKeys, propertyResolver, Boolean.class);
		SendGridClient builtClient = buildClient(apiKey, buildClientHelper(clientHelper, unitTesting || test!=null && test));
		if (builtClient == null) {
			return null;
		}
		LOG.info("Sending email using SendGrid API is registered");
		LOG.debug("SendGrid account: apiKey={}, test={}", apiKey, test);
		return new SendGridV4Sender(builtClient, buildContentHandler(), interceptor);
	}

	private Client buildClientHelper(Client clientHelper, Boolean test) {
		if(clientHelper != null) {
			return clientHelper;
		}
		if(test != null && test) {
			return new Client(test);
		}
		return null;
	}

	private SendGridClient buildClient(String apiKey, Client client) {
		if (this.client != null) {
			return this.client;
		}
		if(apiKey != null) {
			return new DelegateSendGridClient(buildSendGrid(apiKey, client));
		}
		return null;
	}

	private SendGrid buildSendGrid(String apiKey, Client client) {
		if(client!=null) {
			return new SendGrid(apiKey, client);
		}
		return new SendGrid(apiKey);
	}

	private MapContentHandler buildContentHandler() {
		MimeTypeProvider mimetypeProvider = mimetypeBuilder.build();
		MapContentHandler contentHandler = new MapContentHandler();
		contentHandler.register(MultiContent.class, new MultiContentHandler(contentHandler));
		contentHandler.register(MayHaveStringContent.class, new StringContentHandler(mimetypeProvider));
		return contentHandler;
	}
}
