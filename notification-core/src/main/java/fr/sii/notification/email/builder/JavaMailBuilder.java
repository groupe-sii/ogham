package fr.sii.notification.email.builder;

import java.util.Properties;

import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.MultiContent;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.mimetype.FallbackMimeTypeProvider;
import fr.sii.notification.core.mimetype.JMimeMagicProvider;
import fr.sii.notification.core.mimetype.MimeTypeProvider;
import fr.sii.notification.core.util.BuilderUtil;
import fr.sii.notification.email.sender.impl.JavaMailSender;
import fr.sii.notification.email.sender.impl.javamail.JavaMailContentHandler;
import fr.sii.notification.email.sender.impl.javamail.MapContentHandler;
import fr.sii.notification.email.sender.impl.javamail.MultiContentHandler;
import fr.sii.notification.email.sender.impl.javamail.StringContentHandler;

public class JavaMailBuilder {
	private Properties properties;
	private JavaMailContentHandler contentHandler;
	private MapContentHandler mapContentHandler;
	private FallbackMimeTypeProvider mimetypeProvider;
	
	public JavaMailBuilder() {
		super();
		contentHandler = mapContentHandler = new MapContentHandler();
		mimetypeProvider = new FallbackMimeTypeProvider();
	}
	
	public JavaMailBuilder useDefaults() {
		useDefaults(BuilderUtil.getDefaultProperties());
		return this;
	}
	
	public JavaMailBuilder useDefaults(Properties props) {
		withProperties(props);
		registerMimeTypeProvider(new JMimeMagicProvider());
		registerContentHandler(MultiContent.class, new MultiContentHandler(mapContentHandler));
		registerContentHandler(StringContent.class, new StringContentHandler(mimetypeProvider));
		return this;
	}
	
	public JavaMailBuilder withProperties(Properties props) {
		properties = props;
		return this;
	}
	
	public JavaMailBuilder registerContentHandler(Class<? extends Content> clazz, JavaMailContentHandler handler) {
		mapContentHandler.addContentHandler(clazz, handler);
		return this;
	}
	
	public JavaMailBuilder registerMimeTypeProvider(MimeTypeProvider provider) {
		mimetypeProvider.addProvider(provider);
		return this;
	}
	
	public JavaMailSender build() {
		return new JavaMailSender(properties, contentHandler);
	}
}
