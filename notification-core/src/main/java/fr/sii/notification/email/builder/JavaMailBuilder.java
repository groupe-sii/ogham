package fr.sii.notification.email.builder;

import java.util.Properties;

import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.MultiContent;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.util.BuilderUtil;
import fr.sii.notification.email.sender.impl.JavaMailSender;
import fr.sii.notification.email.sender.impl.javamail.FallbackContentHandler;
import fr.sii.notification.email.sender.impl.javamail.JavaMailContentHandler;
import fr.sii.notification.email.sender.impl.javamail.MapContentHandler;
import fr.sii.notification.email.sender.impl.javamail.StringContentHandler;

public class JavaMailBuilder {
	private Properties properties;
	private JavaMailContentHandler contentHandler;
	private MapContentHandler mapContentHandler;
	
	public JavaMailBuilder() {
		super();
		contentHandler = mapContentHandler = new MapContentHandler();
	}
	
	public JavaMailBuilder withDefaults() {
		withDefaults(BuilderUtil.getDefaultProperties());
		return this;
	}
	
	public JavaMailBuilder withDefaults(Properties props) {
		withProperties(props);
		registerContentHandler(MultiContent.class, new FallbackContentHandler(mapContentHandler));
		registerContentHandler(StringContent.class, new StringContentHandler());
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
	
	public JavaMailSender build() {
		return new JavaMailSender(properties, contentHandler);
	}
}
