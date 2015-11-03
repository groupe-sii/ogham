package fr.sii.ogham.email.builder.javamail;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.annotation.RequiredProperty;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilderDelegate;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilderDelegate;
import fr.sii.ogham.core.builder.mimetype.SimpleMimetypeDetectionBuilder;
import fr.sii.ogham.core.charset.CharsetProvider;
import fr.sii.ogham.core.charset.FixedCharsetProvider;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.resource.ByteResource;
import fr.sii.ogham.core.resource.FileResource;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.email.message.content.ContentWithAttachments;
import fr.sii.ogham.email.sender.impl.JavaMailSender;
import fr.sii.ogham.email.sender.impl.PropertiesBridge;
import fr.sii.ogham.email.sender.impl.javamail.ContentWithAttachmentsHandler;
import fr.sii.ogham.email.sender.impl.javamail.FileResourceHandler;
import fr.sii.ogham.email.sender.impl.javamail.JavaMailInterceptor;
import fr.sii.ogham.email.sender.impl.javamail.MapAttachmentResourceHandler;
import fr.sii.ogham.email.sender.impl.javamail.MapContentHandler;
import fr.sii.ogham.email.sender.impl.javamail.MultiContentHandler;
import fr.sii.ogham.email.sender.impl.javamail.StreamResourceHandler;
import fr.sii.ogham.email.sender.impl.javamail.StringContentHandler;

@RequiredProperty(value="mail.host", alternatives="mail.smtp.host")
public class JavaMailBuilder extends AbstractParent<EmailBuilder> implements Builder<JavaMailSender> {
	private static final Logger LOG = LoggerFactory.getLogger(JavaMailBuilder.class);
	
	private EnvironmentBuilder<JavaMailBuilder> environmentBuilder;
	private List<String> hosts;
	private List<String> ports;
	private Integer port;
	private Authenticator authenticator;
	private UsernamePasswordAuthenticatorBuilder authenticatorBuilder;
	private JavaMailInterceptor interceptor;
	private MimetypeDetectionBuilder<JavaMailBuilder> mimetypeBuilder;
	private List<String> charsets;
	private Charset charset;
	private CharsetProvider charsetProvider;
	
	public JavaMailBuilder() {
		this(null);
		environment();
		mimetype();
	}

	public JavaMailBuilder(EmailBuilder parent) {
		super(parent);
		hosts = new ArrayList<>();
		ports = new ArrayList<>();
	}

	public JavaMailBuilder host(String... host) {
		for(String h : host) {
			hosts.add(h);
		}
		return this;
	}
	
	public JavaMailBuilder port(int port) {
		this.port = port;
		return this;
	}
	
	public JavaMailBuilder port(String... port) {
		for(String p : port) {
			ports.add(p);
		}
		return this;
	}
	
	public JavaMailBuilder charset(String... charsets) {
		this.charsets = new ArrayList<>(Arrays.asList(charsets));
		return this;
	}
	
	public JavaMailBuilder charset(Charset charset) {
		this.charset = charset;
		return this;
	}
	
	public JavaMailBuilder charset(CharsetProvider charsetProvider) {
		this.charsetProvider = charsetProvider;
		return this;
	}
	
	public UsernamePasswordAuthenticatorBuilder authenticator() {
		if(authenticatorBuilder==null) {
			authenticatorBuilder = new UsernamePasswordAuthenticatorBuilder(this);
		}
		return authenticatorBuilder;
	}
	
	public JavaMailBuilder authenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
		return this;
	}
	
	public EnvironmentBuilder<JavaMailBuilder> environment() {
		if(environmentBuilder==null) {
			environmentBuilder = new SimpleEnvironmentBuilder<>(this);
		}
		return environmentBuilder;
	}
	
	public JavaMailBuilder environment(EnvironmentBuilder<?> builder) {
		environmentBuilder = new EnvironmentBuilderDelegate<>(this, builder);
		return this;
	}
	
	public JavaMailBuilder intercept(JavaMailInterceptor interceptor) {
		this.interceptor = interceptor;
		return this;
	}
	
	public MimetypeDetectionBuilder<JavaMailBuilder> mimetype() {
		if(mimetypeBuilder==null) {
			mimetypeBuilder = new SimpleMimetypeDetectionBuilder<>(this, environmentBuilder);
		}
		return mimetypeBuilder;
	}

	public JavaMailBuilder mimetype(MimetypeDetectionBuilder<?> builder) {
		mimetypeBuilder = new MimetypeDetectionBuilderDelegate<>(this, builder);
		return this;
	}

	@Override
	public JavaMailSender build() throws BuildException {
		Properties props = buildProperties();
		// TODO: check custom properties too
//		if(!props.containsKey("mail.smtp.host") && !props.containsKey("mail.host")) {
//			LOG.debug("No host property => can't use JavaMail implementation");
//			return null;
//		}
//		if(!props.containsKey("mail.smtp.port") && !props.containsKey("mail.port")) {
//			LOG.debug("No port property => can't use JavaMail implementation");
//			return null;
//		}
		MimeTypeProvider mimetypeProvider = mimetypeBuilder.build();
		return new JavaMailSender(props, 
								buildContentHandler(mimetypeProvider), 
								buildAttachmentHandler(mimetypeProvider), 
								buildAuthenticator(), 
								interceptor);
	}

	private Properties buildProperties() {
		return new OverrideJavaMailProperties(new PropertiesBridge(getPropertyResolver()), hosts, ports, port);
	}

	private Authenticator buildAuthenticator() {
		Authenticator authenticator = null;
		if(this.authenticator!=null) {
			authenticator = this.authenticator;
		} else if(authenticatorBuilder!=null) {
			authenticator = authenticatorBuilder.build();
		}
		return authenticator;
	}

	private MapContentHandler buildContentHandler(MimeTypeProvider mimetypeProvider) {
		MapContentHandler contentHandler = new MapContentHandler();
		contentHandler.addContentHandler(MultiContent.class, new MultiContentHandler(contentHandler));
		contentHandler.addContentHandler(StringContent.class, new StringContentHandler(mimetypeProvider, buildCharset()));
		contentHandler.addContentHandler(ContentWithAttachments.class, new ContentWithAttachmentsHandler(contentHandler));
		return contentHandler;
	}

	private CharsetProvider buildCharset() {
		CharsetProvider charsetProvider;
		if(this.charsetProvider!=null) {
			charsetProvider = this.charsetProvider;
		} else if(charset!=null) {
			charsetProvider = new FixedCharsetProvider(charset);
		} else if(charsets!=null) {
			String charset = BuilderUtils.evaluate(charsets, getPropertyResolver(), String.class);;
			charsetProvider = new FixedCharsetProvider(Charset.forName(charset));
		} else {
			charsetProvider = new FixedCharsetProvider();
		}
		return charsetProvider;
	}

	private PropertyResolver getPropertyResolver() {
		PropertyResolver propertyResolver;
		if(environmentBuilder!=null) {
			propertyResolver = environmentBuilder.build();
		} else {
			propertyResolver = BuilderUtils.getDefaultPropertyResolver(BuilderUtils.getDefaultProperties());
		}
		return propertyResolver;
	}
	
	private MapAttachmentResourceHandler buildAttachmentHandler(MimeTypeProvider mimetypeProvider) {
		MapAttachmentResourceHandler attachmentHandler = new MapAttachmentResourceHandler();
		attachmentHandler.addResourceHandler(ByteResource.class, new StreamResourceHandler(mimetypeProvider));
		attachmentHandler.addResourceHandler(FileResource.class, new FileResourceHandler(mimetypeProvider));
		return attachmentHandler;
	}

}
