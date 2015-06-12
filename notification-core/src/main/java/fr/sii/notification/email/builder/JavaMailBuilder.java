package fr.sii.notification.email.builder;

import java.util.Properties;

import javax.mail.Authenticator;

import fr.sii.notification.core.builder.Builder;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.MultiContent;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.mimetype.FallbackMimeTypeProvider;
import fr.sii.notification.core.mimetype.JMimeMagicProvider;
import fr.sii.notification.core.mimetype.MimeTypeProvider;
import fr.sii.notification.core.resource.ByteResource;
import fr.sii.notification.core.resource.FileResource;
import fr.sii.notification.core.resource.NamedResource;
import fr.sii.notification.core.util.BuilderUtil;
import fr.sii.notification.email.EmailConstants;
import fr.sii.notification.email.sender.impl.JavaMailSender;
import fr.sii.notification.email.sender.impl.javamail.FileResourceHandler;
import fr.sii.notification.email.sender.impl.javamail.JavaMailAttachmentResourceHandler;
import fr.sii.notification.email.sender.impl.javamail.JavaMailContentHandler;
import fr.sii.notification.email.sender.impl.javamail.JavaMailInterceptor;
import fr.sii.notification.email.sender.impl.javamail.MapAttachmentResourceHandler;
import fr.sii.notification.email.sender.impl.javamail.MapContentHandler;
import fr.sii.notification.email.sender.impl.javamail.MultiContentHandler;
import fr.sii.notification.email.sender.impl.javamail.PropertiesUsernamePasswordAuthenticator;
import fr.sii.notification.email.sender.impl.javamail.StreamResourceHandler;
import fr.sii.notification.email.sender.impl.javamail.StringContentHandler;

/**
 * Builder that helps to construct the Java mail API implementation.
 * 
 * @author Aurélien Baudet
 *
 */
public class JavaMailBuilder implements Builder<JavaMailSender> {
	/**
	 * The properties to use
	 */
	private Properties properties;

	/**
	 * The content handler to use. By default, it uses a
	 * {@link MapContentHandler}.
	 */
	private JavaMailContentHandler contentHandler;

	/**
	 * The content handler that associates the content class to the content
	 * handler implementation
	 */
	private MapContentHandler mapContentHandler;

	/**
	 * The attachment resource handler to use. By default, it uses a
	 * {@link MapAttachmentResourceHandler}.
	 */
	private JavaMailAttachmentResourceHandler attachmentResourceHandler;

	/**
	 * The attachment resource handler that associates the attachment resource class to the
	 * attachment resource handler implementation
	 */
	private MapAttachmentResourceHandler mapAttachmentResourceHandler;

	/**
	 * The provider for Mime Type detection
	 */
	private FallbackMimeTypeProvider mimetypeProvider;

	/**
	 * Extra operations to apply on the message
	 */
	private JavaMailInterceptor interceptor;

	/**
	 * Authentication mechanism
	 */
	private Authenticator authenticator;

	public JavaMailBuilder() {
		super();
		contentHandler = mapContentHandler = new MapContentHandler();
		attachmentResourceHandler = mapAttachmentResourceHandler = new MapAttachmentResourceHandler();
		mimetypeProvider = new FallbackMimeTypeProvider();
	}

	/**
	 * Tells the builder to use all default behaviors and values:
	 * <ul>
	 * <li>Use the system properties</li>
	 * <li>Register Mime Type detection using MimeMagic library</li>
	 * <li>Handle {@link MultiContent}</li>
	 * <li>Handle {@link StringContent}</li>
	 * <li>Handle {@link ByteResource}</li>
	 * <li>Handle {@link FileResource}</li>
	 * </ul>
	 * 
	 * @return this instance for fluent use
	 */
	public JavaMailBuilder useDefaults() {
		useDefaults(BuilderUtil.getDefaultProperties());
		return this;
	}

	/**
	 * Tells the builder to use all default behaviors and values:
	 * <ul>
	 * <li>Use the provided properties</li>
	 * <li>Register Mime Type detection using MimeMagic library</li>
	 * <li>Handle {@link MultiContent}</li>
	 * <li>Handle {@link StringContent}</li>
	 * <li>Handle {@link ByteResource}</li>
	 * <li>Handle {@link FileResource}</li>
	 * </ul>
	 * 
	 * @param props
	 *            the properties to use
	 * @return this instance for fluent use
	 */
	public JavaMailBuilder useDefaults(Properties props) {
		withProperties(props);
		if (props.containsKey(EmailConstants.AUTHENTICATOR_PROPERTIES_USERNAME_KEY)) {
			setAuthenticator(new PropertiesUsernamePasswordAuthenticator(props));
		}
		registerMimeTypeProvider(new JMimeMagicProvider());
		registerContentHandler(MultiContent.class, new MultiContentHandler(mapContentHandler));
		registerContentHandler(StringContent.class, new StringContentHandler(mimetypeProvider));
		registerAttachmentResourceHandler(ByteResource.class, new StreamResourceHandler(mimetypeProvider));
		registerAttachmentResourceHandler(FileResource.class, new FileResourceHandler(mimetypeProvider));
		return this;
	}

	/**
	 * Set the properties to use for Java mail API implementation.
	 * 
	 * @param props
	 *            the properties to use
	 * @return this instance for fluent use
	 */
	public JavaMailBuilder withProperties(Properties props) {
		properties = props;
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
	public JavaMailBuilder registerContentHandler(Class<? extends Content> clazz, JavaMailContentHandler handler) {
		mapContentHandler.addContentHandler(clazz, handler);
		return this;
	}

	/**
	 * Register a new handler for a specific attachment resource.
	 * 
	 * @param clazz
	 *            the class of the attachment resource to handle
	 * @param handler
	 *            the handler
	 * @return this instance for fluent use
	 */
	public JavaMailBuilder registerAttachmentResourceHandler(Class<? extends NamedResource> clazz, JavaMailAttachmentResourceHandler handler) {
		mapAttachmentResourceHandler.addResourceHandler(clazz, handler);
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
	public JavaMailBuilder registerMimeTypeProvider(MimeTypeProvider provider) {
		mimetypeProvider.addProvider(provider);
		return this;
	}

	/**
	 * Set an interceptor used to customize the message before sending it. It is
	 * called at the really end and just before sending the message.
	 * 
	 * @param interceptor
	 *            the interceptor to use
	 * @return this instance for fluent use
	 */
	public JavaMailBuilder setInterceptor(JavaMailInterceptor interceptor) {
		this.interceptor = interceptor;
		return this;
	}

	/**
	 * Set the authentication mechanism to use for sending email.
	 * 
	 * @param authenticator
	 *            the authentication mechanism
	 * @return this instance for fluent use
	 */
	public JavaMailBuilder setAuthenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
		return this;
	}

	@Override
	public JavaMailSender build() {
		return new JavaMailSender(properties, contentHandler, attachmentResourceHandler, authenticator, interceptor);
	}
}
