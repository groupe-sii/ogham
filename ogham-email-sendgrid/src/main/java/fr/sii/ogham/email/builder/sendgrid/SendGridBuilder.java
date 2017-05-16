package fr.sii.ogham.email.builder.sendgrid;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilderDelegate;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilderDelegate;
import fr.sii.ogham.core.builder.mimetype.SimpleMimetypeDetectionBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.email.sender.impl.SendGridSender;
import fr.sii.ogham.email.sender.impl.sendgrid.client.DelegateSendGridClient;
import fr.sii.ogham.email.sender.impl.sendgrid.client.SendGridClient;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.MapContentHandler;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.MultiContentHandler;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.StringContentHandler;

public class SendGridBuilder extends AbstractParent<EmailBuilder> implements Builder<SendGridSender> {
	private EnvironmentBuilder<SendGridBuilder> environmentBuilder;
	private MimetypeDetectionBuilder<SendGridBuilder> mimetypeBuilder;
	private String apiKey;
	private String username;
	private String password;
	private SendGridClient client;

	public SendGridBuilder() {
		this(null);
		environment();
		mimetype();
	}

	public SendGridBuilder(EmailBuilder parent) {
		super(parent);
	}

	public SendGridBuilder apiKey(String key) {
		this.apiKey = key;
		return this;
	}
	
	public SendGridBuilder username(String username) {
		this.username = username;
		return this;
	}
	
	public SendGridBuilder password(String password) {
		this.password = password;
		return this;
	}
	
	public SendGridBuilder client(SendGridClient client) {
		this.client = client;
		return this;
	}
	
	public MimetypeDetectionBuilder<SendGridBuilder> mimetype() {
		if(mimetypeBuilder==null) {
			mimetypeBuilder = new SimpleMimetypeDetectionBuilder<>(this, environmentBuilder);
		}
		return mimetypeBuilder;
	}

	public SendGridBuilder mimetype(MimetypeDetectionBuilder<?> builder) {
		mimetypeBuilder = new MimetypeDetectionBuilderDelegate<>(this, builder);
		return this;
	}

	public EnvironmentBuilder<SendGridBuilder> environment() {
		if(environmentBuilder==null) {
			environmentBuilder = new SimpleEnvironmentBuilder<>(this);
		}
		return environmentBuilder;
	}

	public SendGridBuilder environment(EnvironmentBuilder<?> builder) {
		environmentBuilder = new EnvironmentBuilderDelegate<>(this, builder);
		return this;
	}
	

	@Override
	public SendGridSender build() throws BuildException {
		PropertyResolver propertyResolver = environmentBuilder.build();
		String apiKey = BuilderUtils.evaluate(this.apiKey, propertyResolver, String.class);
		String username = BuilderUtils.evaluate(this.username, propertyResolver, String.class);
		String password = BuilderUtils.evaluate(this.password, propertyResolver, String.class);
		if(apiKey==null && (username==null || password==null) && client==null) {
			return null;
		}
		SendGridClient client = buildClient(apiKey, username, password);
		return new SendGridSender(client, buildContentHandler());
	}

	private SendGridClient buildClient(String apiKey, String username, String password) {
		if (client == null) {
			if(username!=null && password!=null) {
				return new DelegateSendGridClient(username, password);
			} else {
				return new DelegateSendGridClient(apiKey);
			}
		}
		return client;
	}

	private MapContentHandler buildContentHandler() {
		MimeTypeProvider mimetypeProvider = mimetypeBuilder.build();
		MapContentHandler contentHandler = new MapContentHandler();
		contentHandler.register(MultiContent.class, new MultiContentHandler(contentHandler));
		contentHandler.register(StringContent.class, new StringContentHandler(mimetypeProvider));
		return contentHandler;
	}
}
