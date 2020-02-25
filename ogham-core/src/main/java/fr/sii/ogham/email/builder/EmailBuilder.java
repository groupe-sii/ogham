package fr.sii.ogham.email.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.ActivableAtRuntime;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.annotation.RequiredClass;
import fr.sii.ogham.core.builder.annotation.RequiredClasses;
import fr.sii.ogham.core.builder.annotation.RequiredProperties;
import fr.sii.ogham.core.builder.annotation.RequiredProperty;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderDelegate;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.sender.SenderImplementationBuilderHelper;
import fr.sii.ogham.core.builder.template.DetectorBuilder;
import fr.sii.ogham.core.builder.template.TemplateBuilderHelper;
import fr.sii.ogham.core.builder.template.VariantBuilder;
import fr.sii.ogham.core.condition.fluent.MessageConditions;
import fr.sii.ogham.core.filler.MessageFiller;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.core.sender.ConditionalSender;
import fr.sii.ogham.core.sender.ContentTranslatorSender;
import fr.sii.ogham.core.sender.FillerSender;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.core.translator.content.ContentTranslator;
import fr.sii.ogham.core.translator.content.EveryContentTranslator;
import fr.sii.ogham.core.translator.content.MultiContentTranslator;
import fr.sii.ogham.core.translator.content.TemplateContentTranslator;
import fr.sii.ogham.core.translator.resource.AttachmentResourceTranslator;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.sender.AttachmentResourceTranslatorSender;
import fr.sii.ogham.email.sender.EmailSender;
import fr.sii.ogham.template.common.adapter.VariantResolver;

/**
 * Configures how to send {@link Email} messages. It allows to:
 * <ul>
 * <li>register and configure several sender implementations</li>
 * <li>register and configure several template engines for parsing templates as
 * message content</li>
 * <li>configure handling of missing {@link Email} information</li>
 * <li>configure handling of file attachments</li>
 * <li>configure CSS and image handling for {@link Email}s with an HTML
 * body</li>
 * </ul>
 * 
 * You can send an {@link Email} using the minimal behavior and using JavaMail
 * implementation:
 * 
 * <pre>
 * <code>
 * // Instantiate the messaging service
 * MessagingService service = new MessagingBuilder()
 *   .email()
 *     .sender(JavaMailBuilder.class)   // enable Email sending using JavaMail
 *       .host("your SMTP server host")
 *       .port("your SMTP server port")
 *       .and()
 *     .and()
 *   .build();
 * // send the email
 * service.send(new Email()
 *   .from("sender email address")
 *   .subject("email subject")
 *   .content("email body")
 *   .to("recipient email address"));
 * </code>
 * </pre>
 * 
 * You can also send an {@link Email} using a template (using Freemarker for
 * example):
 * 
 * The Freemarker template ("email/sample.html.ftl"):
 * 
 * <pre>
 * &lt;html&gt;
 * &lt;head&gt;
 * &lt;/head&gt;
 * &lt;body&gt;
 * Email content with variables: ${name} ${value}
 * &lt;/body&gt;
 * &lt;/html&gt;
 * </pre>
 * 
 * Then you can send the {@link Email} like this:
 * 
 * <pre>
 * <code>
 * // Instantiate the messaging service
 * MessagingService service = new MessagingBuilder()
 *   .email()
 *     .sender(JavaMailBuilder.class)   // enable Email sending using JavaMail
 *       .host("your SMTP server host")
 *       .port("your SMTP server port")
 *       .and()
 *     .and()
 *   .template(FreemarkerEmailBuilder.class)  // enable templating using Freemarker
 *     .classpath()
 *       .lookup("classpath:")   // search resources/templates in the classpath if a path is prefixed by "classpath:"
 *       .and()
 *     .and()
 *   .build();
 * // send the email
 * service.send(new Email()
 *   .from("sender email address")
 *   .subject("email subject")
 *   .content(new TemplateContent("classpath:email/sample.html.ftl", new SampleBean("foo", 42)))
 *   .to("recipient email address"));
 * </code>
 * </pre>
 * 
 * <p>
 * Instead of explicitly configures SMTP host and port in your code, it could be
 * better to externalize the configuration in a properties file for example (for
 * example a file named "email.properties" in the classpath). The previous
 * example becomes:
 * 
 * <pre>
 * <code>
 * // Instantiate the messaging service
 * MessagingService service = new MessagingBuilder()
 *   .environment()
 *     .properties("email.properties")
 *     .and()
 *   .email()
 *     .sender(JavaMailBuilder.class)   // enable Email sending using JavaMail
 *       .host("${mail.host}")
 *       .port("${mail.port}")
 *       .and()
 *     .and()
 *   .template(FreemarkerEmailBuilder.class)  // enable templating using Freemarker
 *     .classpath()
 *       .lookup("classpath:")   // search resources/templates in the classpath if a path is prefixed by "classpath:"
 *       .and()
 *     .and()
 *   .build();
 * // send the email
 * service.send(new Email()
 *   .from("sender email address")
 *   .subject("email subject")
 *   .content(new TemplateContent("classpath:email/sample.html.ftl", new SampleBean("foo", 42)))
 *   .to("recipient email address"));
 * </code>
 * </pre>
 * 
 * The content of the file "email.properties":
 * 
 * <pre>
 * mail.host=your STMP server host
 * mail.port=your STMP server port
 * </pre>
 * 
 * 
 * Some fields of the Email may be automatically filled by a default value if
 * they are not defined. For example, the sender address could be configured
 * only once for your application:
 * 
 * <pre>
 * <code>
 * // Instantiate the messaging service
 * MessagingService service = new MessagingBuilder()
 *   .environment()
 *     .properties("email.properties")
 *     .and()
 *   .email()
 *     .sender(JavaMailBuilder.class)   // enable Email sending using JavaMail
 *       .host("${mail.host}")
 *       .port("${mail.port}")
 *       .and()
 *     .autofill()    // enables and configures autofilling
 *       .from()
 *         .defaultValue("${email.sender.address}")
 *         .and()
 *     .and()
 *   .template(FreemarkerEmailBuilder.class)  // enable templating using Freemarker
 *     .classpath()
 *       .lookup("classpath:")   // search resources/templates in the classpath if a path is prefixed by "classpath:"
 *       .and()
 *     .and()
 *   .build();
 * // send the email (now the sender address can be omitted)
 * service.send(new Email()
 *   .subject("email subject")
 *   .content(new TemplateContent("classpath:email/sample.html.ftl", new SampleBean("foo", 42)))
 *   .to("recipient email address"));
 * </code>
 * </pre>
 * 
 * The content of the file "email.properties":
 * 
 * <pre>
 * mail.host=your STMP server host
 * mail.port=your STMP server port
 * email.sender.address=sender email address
 * </pre>
 * 
 * 
 * 
 * Another very useful automatic filling is for providing the email subject:
 * 
 * <pre>
 * <code>
 * // Instantiate the messaging service
 * MessagingService service = new MessagingBuilder()
 *   .environment()
 *     .properties("email.properties")
 *     .and()
 *   .email()
 *     .sender(JavaMailBuilder.class)   // enable Email sending using JavaMail
 *       .host("${mail.host}")
 *       .port("${mail.port}")
 *       .and()
 *     .autofill()    // enables and configures autofilling
 *       .from()
 *         .defaultValue().properties("${email.sender.address}").and()
 *         .and()
 *       .subject()
 *         .htmlTitle(true)    // enables use of html title tag as subject
 *     .and()
 *   .template(FreemarkerEmailBuilder.class)  // enable templating using Freemarker
 *     .classpath()
 *       .lookup("classpath:")   // search resources/templates in the classpath if a path is prefixed by "classpath:"
 *       .and()
 *     .and()
 *   .build();
 * // send the email (now the subject can be omitted)
 * service.send(new Email()
 *   .content(new TemplateContent("classpath:email/sample.html.ftl", new SampleBean("foo", 42)))
 *   .to("recipient email address"));
 * </code>
 * </pre>
 * 
 * Change your template:
 * 
 * <pre>
 * &lt;html&gt;
 * &lt;head&gt;
 *   &lt;title&gt;email subject - ${name}&lt;/title&gt;
 * &lt;/head&gt;
 * &lt;body&gt;
 * Email content with variables: ${name} ${value}
 * &lt;/body&gt;
 * &lt;/html&gt;
 * </pre>
 * 
 * The obvious advantage is that you have a single place to handle email content
 * (body + subject). There is another benefit: you can also use variables in the
 * subject.
 * 
 * 
 * There many other configuration possibilities:
 * <ul>
 * <li>for configuring {@link Email}s with HTML content with a text fallback
 * (useful for smartphones preview of your email for example)</li>
 * <li>for configuring attachments handling</li>
 * <li>for configuring image and css handling</li>
 * </ul>
 * 
 * <p>
 * All the previous examples are provided to understand what can be configured.
 * Hopefully, Ogham provides auto-configuration with a default behavior that
 * fits 95% of usages. This auto-configuration is provided by
 * {@link MessagingConfigurer}s. Those configurers are automatically applied
 * when using predefined {@link MessagingBuilder}s like
 * {@link MessagingBuilder#minimal()} and {@link MessagingBuilder#standard()}.
 * 
 * The previous sample using standard configuration becomes:
 * 
 * <pre>
 * <code>
 * // Instantiate the messaging service
 * MessagingService service = MessagingBuilder.standad()
 *   .environment()
 *     .properties("email.properties")
 *     .and()
 *   .build();
 * // send the email
 * service.send(new Email()
 *   .content(new TemplateContent("classpath:email/sample.html.ftl", new SampleBean("foo", 42)))
 *   .to("recipient email address"));
 * </code>
 * </pre>
 * 
 * The new content of the file "email.properties":
 * 
 * <pre>
 * mail.host=your STMP server host
 * mail.port=your STMP server port
 * ogham.email.from=sender email address
 * </pre>
 * 
 * <p>
 * You can also use the auto-configuration for benefit from default behaviors
 * and override some behaviors for your needs:
 * 
 * <pre>
 * <code>
 * // Instantiate the messaging service
 * MessagingService service = MessagingBuilder.standard()
 *   .environment()
 *     .properties("email.properties")
 *     .and()
 *   .email()
 *     .autofill()
 *       .from()
 *         .defaultValue()
 *           .properties("${email.sender.address}")   // overrides default sender email address property
 *           .and()
 *         .and()
 *       .and()
 *     .and()
 *   .build();
 * // send the email
 * service.send(new Email()
 *   .content(new TemplateContent("classpath:email/sample.html.ftl", new SampleBean("foo", 42)))
 *   .to("recipient email address"));
 * </code>
 * </pre>
 * 
 * The new content of the file "email.properties":
 * 
 * <pre>
 * mail.host=your STMP server host
 * mail.port=your STMP server port
 * email.sender.address=sender email address
 * </pre>
 * 
 * @author Aurélien Baudet
 *
 */
public class EmailBuilder extends AbstractParent<MessagingBuilder> implements Builder<ConditionalSender> {
	private static final Logger LOG = LoggerFactory.getLogger(EmailBuilder.class);

	private final EnvironmentBuilder<?> environmentBuilder;
	private final TemplateBuilderHelper<EmailBuilder> templateBuilderHelper;
	private final SenderImplementationBuilderHelper<EmailBuilder> senderBuilderHelper;
	private AttachmentHandlingBuilder attachmentBuilder;
	private AutofillEmailBuilder autofillBuilder;
	private CssHandlingBuilder cssBuilder;
	private ImageHandlingBuilder imageBuilder;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param environmentBuilder
	 *            the configuration for property resolution and evaluation
	 */
	public EmailBuilder(MessagingBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		templateBuilderHelper = new TemplateBuilderHelper<>(this, environmentBuilder);
		senderBuilderHelper = new SenderImplementationBuilderHelper<>(this, environmentBuilder);
	}

	/**
	 * Configures how {@link Attachment}s are handled.
	 * 
	 * Attachment resolution consists of finding a file:
	 * <ul>
	 * <li>either on filesystem</li>
	 * <li>or in the classpath</li>
	 * <li>or anywhere else</li>
	 * </ul>
	 * 
	 * <p>
	 * To identify which resolution to use, each resolution is configured to
	 * handle one or several lookups prefixes. For example, if resolution is
	 * configured like this:
	 * 
	 * <pre>
	 * <code>
	 * .string()
	 *   .lookup("string:", "s:")
	 *   .and()
	 * .file()
	 *   .lookup("file:")
	 *   .and()
	 * .classpath()
	 *   .lookup("classpath:", "");
	 * </code>
	 * </pre>
	 * 
	 * Then you can reference a file that is in the classpath like this:
	 * 
	 * <pre>
	 * "classpath:foo/bar.pdf"
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Resource resolution is also able to handle path prefix and suffix. The
	 * aim is for example to have a folder that contains all templates. The
	 * developer then configures a path prefix for the folder. He can also
	 * configure a suffix to fix extension for templates. Thanks to those
	 * prefix/suffix, templates can now be referenced by the name of the file
	 * (without extension). It is useful to reference a template independently
	 * from where it is in reality (classpath, file or anywhere else) .
	 * Switching from classpath to file and conversely can be done easily (by
	 * updating the lookup).
	 * 
	 * For example:
	 * 
	 * <pre>
	 * .classpath().lookup("classpath:").pathPrefix("foo/").pathSuffix(".html");
	 * 
	 * resourceResolver.getResource("classpath:bar");
	 * </pre>
	 * 
	 * The real path is then {@code foo/bar.pdf}.
	 * 
	 * <p>
	 * This implementation is used by {@link MessagingBuilder} for general
	 * configuration. That configuration may be inherited (applied to other
	 * resource resolution builders).
	 * 
	 * 
	 * <p>
	 * Detection of the mimetype of each attachment is not directly configured
	 * here. Attachment handling depends totally on the sender implementation.
	 * Some implementations will require that a mimetype is provided (JavaMail
	 * for example) while other implementations doesn't need it (SendGrid for
	 * example).
	 * 
	 * @return the builder to configure attachment handling
	 */
	public AttachmentHandlingBuilder attachments() {
		if (attachmentBuilder == null) {
			attachmentBuilder = new AttachmentHandlingBuilder(this, environmentBuilder);
		}
		return attachmentBuilder;
	}

	/**
	 * Configures how Ogham will add default values to the {@link Email} if some
	 * information is missing.
	 * 
	 * If sender address is missing, a default one can be defined in
	 * configuration properties.
	 * 
	 * If recipient address is missing, a default one can be defined in
	 * configuration properties.
	 * 
	 * If subject is missing, a default one can be defined either:
	 * <ul>
	 * <li>In HTML title</li>
	 * <li>In first line of text template</li>
	 * <li>Using a default value defined in configuration properties</li>
	 * </ul>
	 * 
	 * For example:
	 * 
	 * <pre>
	 * <code>
	 * builder
	 *  .autofill()
	 *    .subject()
	 *      .defaultValue().properties("${ogham.email.subject}").and()
	 *      .htmlTitle(true)
	 *      .text().properties("${ogham.email.subject-first-line-prefix}").defaultValue("Subject:").and()
	 *	    .and()
	 *    .from()
	 *	    .defaultValue().properties("${ogham.email.from}", "${mail.smtp.from}").and()
	 *	    .and()
	 *    .to()
	 *	    .defaultValue().properties("${ogham.email.to}").and()
	 *	    .and()
	 *    .cc()
	 *	    .defaultValue().properties("${ogham.email.cc}").and()
	 *	    .and()
	 *    .bcc()
	 *	    .defaultValue().properties("${ogham.email.bcc}")
	 * </code>
	 * </pre>
	 * 
	 * @return the builder to configure autofilling of Email
	 */
	public AutofillEmailBuilder autofill() {
		if (autofillBuilder == null) {
			autofillBuilder = new AutofillEmailBuilder(this, environmentBuilder);
		}
		return autofillBuilder;
	}

	/**
	 * CSS handling consists of defining how CSS are inlined in the email.
	 * Inlining CSS means that CSS styles are loaded and applied on the matching
	 * HTML nodes using the {@code style} HTML attribute.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * .css()
	 *   .inline()
	 *     .jsoup()
	 * </pre>
	 * 
	 * Enables inlining of CSS styles using Jsoup utility.
	 * 
	 * @return the builder to configure css handling
	 */
	public CssHandlingBuilder css() {
		if (cssBuilder == null) {
			cssBuilder = new CssHandlingBuilder(this, environmentBuilder);
		}
		return cssBuilder;
	}

	/**
	 * Image handling consists of defining how images are inlined in the email:
	 * <ul>
	 * <li>Either inlining directly in the HTML content by enconding image into
	 * base64 string</li>
	 * <li>Or attaching the image to the email and referencing it using a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (CID)</a></li>
	 * <li>Or no inlining</li>
	 * </ul>
	 * 
	 * 
	 * For example:
	 * 
	 * <pre>
	 * .images()
	 *   .inline()
	 *     .attach()
	 *       .cid()
	 *         .generator(new SequentialIdGenerator())
	 *         .and()
	 *       .and()
	 *     .base64();
	 * </pre>
	 * 
	 * Enables both inlining modes (attaching images and encoding in base64). By
	 * default, attaching is used if nothing is specified in the HTML. You can
	 * also explicitly specify which mode to using the {@code ogham-inline-mode}
	 * attribute (see {@link ImageHandlingBuilder#inline()} for more
	 * information).
	 * 
	 * @return the builder to configure images handling
	 */
	public ImageHandlingBuilder images() {
		if (imageBuilder == null) {
			imageBuilder = new ImageHandlingBuilder(this, environmentBuilder);
		}
		return imageBuilder;
	}

	/**
	 * Registers and configures a {@link TemplateParser} through a dedicated
	 * builder.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * .register(ThymeleafEmailBuilder.class)
	 *     .detector(new ThymeleafEngineDetector());
	 * </pre>
	 * 
	 * <p>
	 * Your {@link Builder} may implement {@link VariantBuilder} to handle
	 * template {@link Variant}s (used for {@link MultiTemplateContent} that
	 * provide a single path to templates with different extensions for
	 * example).
	 * </p>
	 * 
	 * <p>
	 * Your {@link Builder} may also implement {@link DetectorBuilder} in order
	 * to indicate which kind of templates your {@link TemplateParser} is able
	 * to parse. If your template parse is able to parse any template file you
	 * are using, you may not need to implement {@link DetectorBuilder}.
	 * </p>
	 * 
	 * <p>
	 * In order to be able to keep chaining, you builder instance may provide a
	 * constructor with two arguments:
	 * <ul>
	 * <li>The type of the parent builder ({@code &lt;P&gt;})</li>
	 * <li>The {@link EnvironmentBuilder} instance</li>
	 * </ul>
	 * If you don't care about chaining, just provide a default constructor.
	 * 
	 * @param builderClass
	 *            the builder class to instantiate
	 * @param <T>
	 *            the type of the builder
	 * @return the builder to configure the implementation
	 */
	public <T extends Builder<? extends TemplateParser>> T template(Class<T> builderClass) {
		return templateBuilderHelper.register(builderClass);
	}

	/**
	 * Registers a custom message sender implementation.
	 * 
	 * <p>
	 * If your custom implementation is annotated by one or several of:
	 * <ul>
	 * <li>{@link RequiredClass}</li>
	 * <li>{@link RequiredProperty}</li>
	 * <li>{@link RequiredClasses}</li>
	 * <li>{@link RequiredProperties}</li>
	 * </ul>
	 * Then if condition evaluation returns true, your implementation will be
	 * used. If you provide several annotations, your implementation will be
	 * used only if all conditions are met (and operator).
	 * 
	 * <p>
	 * If your custom implementation implements {@link ActivableAtRuntime}, and
	 * the provided condition evaluation returns true, then your implementation
	 * will be used.
	 * 
	 * See {@link MessageConditions} to build your condition.
	 * </p>
	 * 
	 * <p>
	 * If neither annotations nor implementation of {@link ActivableAtRuntime}
	 * is used, then your custom implementation will be always used. All other
	 * implementations (even standard ones) will never be used.
	 * </p>
	 * 
	 * @param sender
	 *            the sender to register
	 * @return this instance for fluent chaining
	 */
	public EmailBuilder customSender(MessageSender sender) {
		senderBuilderHelper.customSender(sender);
		return this;
	}

	/**
	 * Registers and configures sender through a dedicated builder.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * .sender(JavaMailBuilder.class)
	 *     .host("localhost");
	 * </pre>
	 * 
	 * <p>
	 * If your custom builder is annotated by one or several of:
	 * <ul>
	 * <li>{@link RequiredClass}</li>
	 * <li>{@link RequiredProperty}</li>
	 * <li>{@link RequiredClasses}</li>
	 * <li>{@link RequiredProperties}</li>
	 * </ul>
	 * Then if condition evaluation returns true, your built implementation will
	 * be used. If you provide several annotations, your built implementation
	 * will be used only if all conditions are met (and operator).
	 * 
	 * <p>
	 * If your custom builder implements {@link ActivableAtRuntime}, and the
	 * provided condition evaluation returns true, then your built
	 * implementation will be used.
	 * 
	 * See {@link MessageConditions} to build your condition.
	 * </p>
	 * 
	 * <p>
	 * If neither annotations nor implementation of {@link ActivableAtRuntime}
	 * is used, then your built implementation will be always used. All other
	 * implementations (even standard ones) will never be used.
	 * </p>
	 * 
	 * <p>
	 * In order to be able to keep chaining, you builder instance may provide a
	 * constructor with one argument with the type of the parent builder
	 * ({@link EmailBuilder}). If you don't care about chaining, just provide a
	 * default constructor.
	 * </p>
	 * 
	 * <p>
	 * Your builder may return {@code null} when calling
	 * {@link Builder#build()}. In this case it means that your implementation
	 * can't be used due to current environment. Your implementation is then not
	 * registered.
	 * </p>
	 * 
	 * @param builderClass
	 *            the builder class to instantiate
	 * @param <T>
	 *            the type of the builder
	 * @return the builder to configure the implementation
	 * 
	 */
	public <T extends Builder<? extends MessageSender>> T sender(Class<T> builderClass) {
		return senderBuilderHelper.register(builderClass);
	}
	
	/**
	 * If a variant is missing, then force to fail.
	 * 
	 * <p>
	 * This may be useful if you want for example to always provide a text
	 * fallback when using an html template. So if a client can't read the html
	 * version, the fallback version will still always be readable. So to avoid
	 * forgetting to write text template, set this to true.
	 * </p>
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #failIfMissingVariant()}.
	 * 
	 * <pre>
	 * .failIfMissingVariant(false)
	 * .failIfMissingVariant()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <pre>
	 * .failIfMissingVariant(false)
	 * .failIfMissingVariant()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * In both cases, {@code failIfMissingVariant(false)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param fail
	 *            fail if a variant is missing
	 * @return this instance for fluent chaining
	 */
	public EmailBuilder failIfMissingVariant(Boolean fail) {
		templateBuilderHelper.failIfMissingVariant(fail);
		return this;
	}

	/**
	 * If a variant is missing, then force to fail.
	 * 
	 * <p>
	 * This may be useful if you want for example to always provide a text
	 * fallback when using an html template. So if a client can't read the html
	 * version, the fallback version will still always be readable. So to avoid
	 * forgetting to write text template, set this to true.
	 * </p>
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .failIfMissingVariant()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #failIfMissingVariant(Boolean)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .failIfMissingVariant(false)
	 * .failIfMissingVariant()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * The value {@code false} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<EmailBuilder, Boolean> failIfMissingVariant() {
		return new ConfigurationValueBuilderDelegate<>(this, templateBuilderHelper.failIfMissingVariant());
	}
	
	
	/**
	 * When {@link #failIfMissingVariant()} is enabled, also indicate which paths were tried in order to help debugging why a variant was not found.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #listPossiblePaths()}.
	 * 
	 * <pre>
	 * .listPossiblePaths(true)
	 * .listPossiblePaths()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <pre>
	 * .listPossiblePaths(true)
	 * .listPossiblePaths()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * In both cases, {@code listPossiblePaths(true)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param enable
	 *            enable/disable tracking of possible paths for template variants
	 * @return this instance for fluent chaining
	 */
	public EmailBuilder listPossiblePaths(Boolean enable) {
		templateBuilderHelper.listPossiblePaths(enable);
		return this;
	}

	/**
	 * When {@link #failIfMissingVariant()} is enabled, also indicate which paths were tried in order to help debugging why a variant was not found.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .listPossiblePaths()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #listPossiblePaths(Boolean)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .listPossiblePaths(true)
	 * .listPossiblePaths()
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
	public ConfigurationValueBuilder<EmailBuilder, Boolean> listPossiblePaths() {
		return new ConfigurationValueBuilderDelegate<>(this, templateBuilderHelper.listPossiblePaths());
	}

	/**
	 * Provide custom resolver that will handle a missing variant.
	 * 
	 * @param resolver
	 *            the custom resolver
	 * @return this instance for fluent chaining
	 */
	public EmailBuilder missingVariant(VariantResolver resolver) {
		templateBuilderHelper.missingVariant(resolver);
		return this;
	}

	@Override
	public ConditionalSender build() {
		EmailSender emailSender = new EmailSender();
		ConditionalSender sender = emailSender;
		senderBuilderHelper.addSenders(emailSender);
		if (autofillBuilder != null) {
			MessageFiller messageFiller = autofillBuilder.build();
			LOG.debug("Automatic filling of message enabled {}", messageFiller);
			sender = new FillerSender(messageFiller, sender);
		}
		if (attachmentBuilder != null) {
			AttachmentResourceTranslator resourceTranslator = attachmentBuilder.build();
			LOG.debug("Resource translation enabled {}", resourceTranslator);
			sender = new AttachmentResourceTranslatorSender(resourceTranslator, sender);
		}
		if (templateBuilderHelper.hasRegisteredTemplates() || cssBuilder != null || imageBuilder != null) {
			ContentTranslator translator = buildContentTranslator();
			LOG.debug("Content translation enabled {}", translator);
			sender = new ContentTranslatorSender(translator, sender);

		}
		return sender;
	}

	private ContentTranslator buildContentTranslator() {
		EveryContentTranslator translator = new EveryContentTranslator();
		addTemplateTranslator(translator);
		addMultiContent(translator);
		addCssInlining(translator);
		addImageInlining(translator);
		return translator;
	}

	private void addTemplateTranslator(EveryContentTranslator translator) {
		if (!templateBuilderHelper.hasRegisteredTemplates()) {
			return;
		}
		TemplateParser templateParser = templateBuilderHelper.buildTemplateParser();
		LOG.debug("Registering content translator that parses templates using {}", templateParser);
		translator.addTranslator(new TemplateContentTranslator(templateParser, templateBuilderHelper.buildVariant()));
	}

	private static void addMultiContent(EveryContentTranslator translator) {
		translator.addTranslator(new MultiContentTranslator(translator));
	}

	private void addImageInlining(EveryContentTranslator translator) {
		if (imageBuilder == null) {
			return;
		}
		ContentTranslator imageInliner = imageBuilder.build();
		if (imageInliner != null) {
			LOG.debug("Image inlining is enabled");
			translator.addTranslator(imageInliner);
		}
	}

	private void addCssInlining(EveryContentTranslator translator) {
		if (cssBuilder == null) {
			return;
		}
		ContentTranslator cssInliner = cssBuilder.build();
		if (cssInliner != null) {
			LOG.debug("CSS inlining is enabled");
			translator.addTranslator(cssInliner);
		}
	}

}
