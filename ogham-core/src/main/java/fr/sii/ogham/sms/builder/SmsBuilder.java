package fr.sii.ogham.sms.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.ActivableAtRuntime;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.annotation.RequiredClass;
import fr.sii.ogham.core.builder.annotation.RequiredClasses;
import fr.sii.ogham.core.builder.annotation.RequiredProperties;
import fr.sii.ogham.core.builder.annotation.RequiredProperty;
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
import fr.sii.ogham.core.translator.content.TemplateContentTranslator;
import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.message.addressing.AddressedPhoneNumber;
import fr.sii.ogham.sms.sender.PhoneNumberTranslatorSender;
import fr.sii.ogham.sms.sender.SmsSender;

/**
 * Configures how to send {@link Sms} messages. It allows to:
 * <ul>
 * <li>register and configure several sender implementations</li>
 * <li>register and configure several template engines for parsing templates as
 * message content</li>
 * <li>configure handling of missing {@link Sms} information</li>
 * <li>configure number format handling</li>
 * </ul>
 * 
 * You can send a {@link Sms} using the minimal behavior and using Cloudhopper
 * implementation:
 * 
 * <pre>
 * <code>
 * // Instantiate the messaging service
 * MessagingService service = new MessagingBuilder()
 *   .sms()
 *     .sender(CloudhopperBuilder.class)   // enable SMS sending using Cloudhopper
 *       .host("your SMPP server host")
 *       .port("your SMPP server port")
 *       .systemId("your SMPP system_id")
 *       .password("an optional password")
 *       .and()
 *     .and()
 *   .build();
 * // send the sms
 * service.send(new Sms()
 *   .from("sender phone number")
 *   .content("sms content")
 *   .to("recipient phone number"));
 * </code>
 * </pre>
 * 
 * You can also send a {@link Sms} using a template (using Freemarker for
 * example):
 * 
 * The Freemarker template ("sms/sample.txt.ftl"):
 * 
 * <pre>
 * Sms content with variables: ${name} ${value}
 * </pre>
 * 
 * Then you can send the {@link Sms} like this:
 * 
 * <pre>
 * <code>
 * // Instantiate the messaging service
 * MessagingService service = new MessagingBuilder()
 *   .sms()
 *     .sender(CloudhopperBuilder.class)   // enable SMS sending using Cloudhopper
 *       .host("your SMPP server host")
 *       .port("your SMPP server port")
 *       .systemId("your SMPP system_id")
 *       .password("an optional password")
 *       .and()
 *     .and()
 *   .template(FreemarkerSmsBuilder.class)  // enable templating using Freemarker
 *     .classpath()
 *       .lookup("classpath:")   // search resources/templates in the classpath if a path is prefixed by "classpath:"
 *       .and()
 *     .and()
 *   .build();
 * // send the sms
 * service.send(new Sms()
 *   .from("sender phone number")
 *   .content(new TemplateContent("classpath:sms/sample.txt.ftl", new SampleBean("foo", 42)))
 *   .to("recipient phone number"));
 * </code>
 * </pre>
 * 
 * <p>
 * Instead of explicitly configures SMPP host/port/system_id/password in your
 * code, it could be better to externalize the configuration in a properties
 * file for example (for example a file named "sms.properties" in the
 * classpath). The previous example becomes:
 * 
 * <pre>
 * <code>
 * // Instantiate the messaging service
 * MessagingService service = new MessagingBuilder()
 *   .environment()
 *     .properties("sms.properties")
 *     .and()
 *   .sms()
 *     .sender(CloudhopperBuilder.class)   // enable SMS sending using Cloudhopper
 *       .host("${smpp.host}")
 *       .port("${smpp.port}")
 *       .systemId("${smpp.system-id}")
 *       .password("${smpp.password}")
 *       .and()
 *     .and()
 *   .template(FreemarkerSmsBuilder.class)  // enable templating using Freemarker
 *     .classpath()
 *       .lookup("classpath:")   // search resources/templates in the classpath if a path is prefixed by "classpath:"
 *       .and()
 *     .and()
 *   .build();
 * // send the sms
 * service.send(new Sms()
 *   .from("sender phone number")
 *   .content(new TemplateContent("classpath:sms/sample.txt.ftl", new SampleBean("foo", 42)))
 *   .to("recipient phone number"));
 * </code>
 * </pre>
 * 
 * The content of the file "sms.properties":
 * 
 * <pre>
 * smpp.host=your SMPP server host
 * smpp.port=your SMPP server port
 * smpp.system-id=your SMPP system_id
 * smpp.password=an optional password
 * </pre>
 * 
 * 
 * Some fields of the SMS may be automatically filled by a default value if they
 * are not defined. For example, the sender phone number could be configured
 * only once for your application:
 * 
 * <pre>
 * <code>
 * // Instantiate the messaging service
 * MessagingService service = new MessagingBuilder()
 *   .environment()
 *     .properties("sms.properties")
 *     .and()
 *   .sms()
 *     .sender(CloudhopperBuilder.class)   // enable SMS sending using Cloudhopper
 *       .host().properties("${smpp.host}").and()
 *       .port().properties("${smpp.port}").and()
 *       .systemId().properties("${smpp.system-id}").and()
 *       .password().properties("${smpp.password}").and()
 *       .and()
 *     .autofill()    // enables and configures autofilling
 *       .from()
 *         .defaultValue().properties("${sms.sender.number}").and()
 *         .and()
 *       .and()
 *     .and()
 *   .template(FreemarkerSmsBuilder.class)  // enable templating using Freemarker
 *     .classpath()
 *       .lookup("classpath:")   // search resources/templates in the classpath if a path is prefixed by "classpath:"
 *       .and()
 *     .and()
 *   .build();
 * // send the sms (now the sender phone number can be omitted)
 * service.send(new Sms()
 *   .content(new TemplateContent("classpath:sms/sample.txt.ftl", new SampleBean("foo", 42)))
 *   .to("recipient phone number"));
 * </code>
 * </pre>
 * 
 * The new content of the file "sms.properties":
 * 
 * <pre>
 * smpp.host=your SMPP server host
 * smpp.port=your SMPP server port
 * smpp.system-id=your SMPP system_id
 * smpp.password=an optional password
 * sms.sender.number=the sender phone number
 * </pre>
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
 * MessagingService service = MessagingBuilder.standard()
 *   .environment()
 *     .properties("sms.properties")
 *     .and()
 *   .build();
 * // send the sms
 * service.send(new Sms()
 *   .content(new TemplateContent("classpath:sms/sample.txt.ftl", new SampleBean("foo", 42)))
 *   .to("recipient phone number"));
 * </code>
 * </pre>
 * 
 * The new content of the file "sms.properties":
 * 
 * <pre>
 * ogham.sms.smpp.host=your SMPP server host
 * ogham.sms.smpp.port=your SMPP server port
 * ogham.sms.smpp.system-id=your SMPP system_id
 * ogham.sms.smpp.password=an optional password
 * ogham.sms.from.default-value=the sender phone number
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
 *     .properties("sms.properties")
 *     .and()
 *   .sms()
 *     .autofill()
 *       .from()
 *         .defaultValue().properties("${sms.sender.number}").and()   // overrides default sender phone number property
 *         .and()
 *       .and()
 *     .and()
 *   .build();
 * // send the sms
 * service.send(new Sms()
 *   .content(new TemplateContent("classpath:sms/sample.txt.ftl", new SampleBean("foo", 42)))
 *   .to("recipient phone number"));
 * </code>
 * </pre>
 * 
 * The new content of the file "sms.properties":
 * 
 * <pre>
 * ogham.sms.smpp.host=your SMPP server host
 * ogham.sms.smpp.port=your SMPP server port
 * ogham.sms.smpp.system-id=your SMPP system_id
 * ogham.sms.smpp.password=an optional password
 * sms.sender.number=the sender phone number
 * </pre>
 * 
 * @author Aurélien Baudet
 *
 */
public class SmsBuilder extends AbstractParent<MessagingBuilder> implements Builder<ConditionalSender> {
	private static final Logger LOG = LoggerFactory.getLogger(SmsBuilder.class);

	private EnvironmentBuilder<?> environmentBuilder;
	private final TemplateBuilderHelper<SmsBuilder> templateBuilderHelper;
	private final SenderImplementationBuilderHelper<SmsBuilder> senderBuilderHelper;
	private AutofillSmsBuilder autofillBuilder;
	private PhoneNumbersBuilder phoneNumbersBuilder;

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
	public SmsBuilder(MessagingBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		templateBuilderHelper = new TemplateBuilderHelper<>(this, environmentBuilder);
		senderBuilderHelper = new SenderImplementationBuilderHelper<>(this, environmentBuilder);
	}

	/**
	 * Configures how Ogham will add default values to the {@link Sms} if some
	 * information is missing.
	 * 
	 * If sender phone number is missing, a default one can be defined in
	 * configuration properties.
	 * 
	 * If recipient phone number is missing, a default one can be defined in
	 * configuration properties.
	 * 
	 * <pre>
	 * <code>
	 *	builder
	 *	  .autofill()
	 *	    .from()
	 *	      .defaultValue().properties("${ogham.sms.from.default-value}").and()
	 *	        .and()
	 *	    .to()
	 *	      .defaultValue().properties("${ogham.sms.to.default-value}").and()
	 *	        .and()
	 *	    .and()
	 * </code>
	 * </pre>
	 * 
	 * @return the builder to configure autofilling of SMS
	 */
	public AutofillSmsBuilder autofill() {
		if (autofillBuilder == null) {
			autofillBuilder = new AutofillSmsBuilder(this, environmentBuilder);
		}
		return autofillBuilder;
	}

	/**
	 * Configures the phone number conversions (from a {@link PhoneNumber} to an
	 * {@link AddressedPhoneNumber}).
	 * 
	 * The {@link PhoneNumber} is used by the developer to provide a simple
	 * phone number without knowing how phone number works (no need to handle
	 * formats, addressing, countries...). The {@link AddressedPhoneNumber} is
	 * used by Ogham implementations to have a phone number that is usable by a
	 * technical system.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * <code>
	 *	builder
	 *	  .numbers()
	 *	    .from()
	 *	      .format()
	 *	        .alphanumericCode().properties("${ogham.sms.from.alphanumeric-code-format.enable}").defaultValue(true).and()
	 *	        .shortCode().properties("${ogham.sms.from.short-code-format.enable}").defaultValue(true).and()
	 *	        .internationalNumber().properties("${ogham.sms.from.international-format.enable}").defaultValue(true).and()
	 *	        .and()
	 *	      .and()
	 *	    .to()
	 *	      .format()
	 *	        .internationalNumber().properties("${ogham.sms.to.international-format.enable}").defaultValue(true);
	 * </code>
	 * </pre>
	 * 
	 * @return the builder to configure phone number formats
	 */
	public PhoneNumbersBuilder numbers() {
		if (phoneNumbersBuilder == null) {
			phoneNumbersBuilder = new PhoneNumbersBuilder(this, environmentBuilder);
		}
		return phoneNumbersBuilder;
	}

	/**
	 * Registers and configures a {@link TemplateParser} through a dedicated
	 * builder.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * .register(ThymeleafSmsBuilder.class)
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
	public SmsBuilder customSender(MessageSender sender) {
		senderBuilderHelper.customSender(sender);
		return this;
	}

	/**
	 * Registers and configures sender through a dedicated builder.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * .sender(CloudhopperBuilder.class)
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
	 * ({@link SmsBuilder}). If you don't care about chaining, just provide a
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
	 */
	public <T extends Builder<? extends MessageSender>> T sender(Class<T> builderClass) {
		return senderBuilderHelper.register(builderClass);
	}

	@Override
	public ConditionalSender build() {
		SmsSender smsSender = new SmsSender();
		ConditionalSender sender = smsSender;
		senderBuilderHelper.addSenders(smsSender);
		if (templateBuilderHelper.hasRegisteredTemplates()) {
			ContentTranslator translator = buildContentTranslator();
			LOG.debug("Content translation enabled {}", translator);
			sender = new ContentTranslatorSender(translator, sender);
		}
		if (phoneNumbersBuilder != null) {
			PhoneNumberTranslatorPair pair = phoneNumbersBuilder.build();
			sender = new PhoneNumberTranslatorSender(pair.getSender(), pair.getRecipient(), sender);
		}
		if (autofillBuilder != null) {
			MessageFiller messageFiller = autofillBuilder.build();
			LOG.debug("Automatic filling of message enabled {}", messageFiller);
			sender = new FillerSender(messageFiller, sender);
		}
		return sender;
	}

	private ContentTranslator buildContentTranslator() {
		EveryContentTranslator translator = new EveryContentTranslator();
		addTemplateTranslator(translator);
		return translator;
	}

	private void addTemplateTranslator(EveryContentTranslator translator) {
		if (!templateBuilderHelper.hasRegisteredTemplates()) {
			return;
		}
		TemplateParser templateParser = templateBuilderHelper.buildTemplateParser();
		LOG.debug("Registering content translator that parses templates using {}", templateParser);
		translator.addTranslator(new TemplateContentTranslator(templateParser));
	}

}
