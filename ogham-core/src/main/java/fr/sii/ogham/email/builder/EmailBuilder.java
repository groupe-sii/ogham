package fr.sii.ogham.email.builder;

import static fr.sii.ogham.core.builder.condition.MessageConditions.alwaysTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.template.TemplateMultiContentBuilder;
import fr.sii.ogham.core.condition.provider.ImplementationConditionProvider;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.filler.MessageFiller;
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
import fr.sii.ogham.email.sender.AttachmentResourceTranslatorSender;
import fr.sii.ogham.email.sender.EmailSender;

public class EmailBuilder extends AbstractParent<MessagingBuilder> implements Builder<ConditionalSender> {
	private static final Logger LOG = LoggerFactory.getLogger(EmailBuilder.class);
	
	private TemplateMultiContentBuilder<EmailBuilder> templateBuilder;
	private EnvironmentBuilder<?> environmentBuilder;
	private AttachmentHandlingBuilder attachmentBuilder;
	private AutofillEmailBuilder autofillBuilder;
	private CssHandlingBuilder cssBuilder;
	private ImageHandlingBuilder imageBuilder;
	private List<Builder<? extends MessageSender>> senderBuilders;
	private List<MessageSender> customSenders;

	public EmailBuilder(MessagingBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		senderBuilders = new ArrayList<>();
		customSenders = new ArrayList<>();
	}

	public AttachmentHandlingBuilder attachments() {
		if(attachmentBuilder==null) {
			attachmentBuilder = new AttachmentHandlingBuilder(this, environmentBuilder);
		}
		return attachmentBuilder;
	}
	
	public AutofillEmailBuilder autofill() {
		if(autofillBuilder==null) {
			autofillBuilder = new AutofillEmailBuilder(this, environmentBuilder);
		}
		return autofillBuilder;
	}
	
	public CssHandlingBuilder css() {
		if(cssBuilder==null) {
			cssBuilder = new CssHandlingBuilder(this, environmentBuilder);
		}
		return cssBuilder;
	}
	
	public ImageHandlingBuilder images() {
		if(imageBuilder==null) {
			imageBuilder = new ImageHandlingBuilder(this, environmentBuilder);
		}
		return imageBuilder;
	}
	
	public TemplateMultiContentBuilder<EmailBuilder> template() {
		if(templateBuilder==null) {
			templateBuilder = new TemplateMultiContentBuilder<>(this, environmentBuilder);
		}
		return templateBuilder;
	}
	
	public EmailBuilder customSender(MessageSender sender) {
		customSenders.add(sender);
		return this;
	}
	
	public <T extends Builder<? extends MessageSender>> T sender(Class<T> builderClass) {
		for(Builder<? extends MessageSender> builder : senderBuilders) {
			if(builderClass.isAssignableFrom(builder.getClass())) {
				return (T) builder;
			}
		}
		try {
			T builder;
			Constructor<T> constructor = builderClass.getConstructor(EmailBuilder.class);
			if(constructor!=null) {
				builder = constructor.newInstance(this);
			} else {
				builder = builderClass.newInstance();
			}
			senderBuilders.add(builder);
			return builder;
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			throw new BuildException("Can't instantiate builder from class "+builderClass.getSimpleName(), e);
		}
	}

	@Override
	public ConditionalSender build() throws BuildException {
		EmailSender emailSender = new EmailSender();
		ConditionalSender sender = emailSender;
		for(MessageSender s : customSenders) {
			emailSender.addImplementation(alwaysTrue(), s);
		}
		ImplementationConditionProvider implementationSelection = new ImplementationConditionProvider(environmentBuilder.build());
		for(Builder<? extends MessageSender> builder : senderBuilders) {
			MessageSender s = builder.build();
			if(s!=null) {
				LOG.debug("Implementation {} registered", s);
				emailSender.addImplementation(implementationSelection.provide(builder), s);
			}
		}
		if(autofillBuilder!=null) {
			MessageFiller messageFiller = autofillBuilder.build();
			LOG.debug("Automatic filling of message enabled {}", messageFiller);
			sender = new FillerSender(messageFiller, sender);
		}
		if(attachmentBuilder!=null) {
			AttachmentResourceTranslator resourceTranslator = attachmentBuilder.build();
			LOG.debug("Resource translation enabled {}", resourceTranslator);
			sender = new AttachmentResourceTranslatorSender(resourceTranslator, sender);
		}
		if(templateBuilder!=null || cssBuilder!=null || imageBuilder!=null) {
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
		if (templateBuilder == null) {
			return;
		}
		TemplateParser templateParser = templateBuilder.build();
		LOG.debug("Registering content translator that parses templates using {}", templateParser);
		translator.addTranslator(new TemplateContentTranslator(templateParser, templateBuilder.buildVariant()));
	}

	private void addMultiContent(EveryContentTranslator translator) {
		translator.addTranslator(new MultiContentTranslator(translator));
	}
	
	private void addImageInlining(EveryContentTranslator translator) {
		if(imageBuilder==null) {
			return;
		}
		ContentTranslator imageInliner = imageBuilder.build();
		if(imageInliner!=null) {
			LOG.debug("Image inlining is enabled");
			translator.addTranslator(imageInliner);
		}
	}

	private void addCssInlining(EveryContentTranslator translator) {
		if(cssBuilder==null) {
			return;
		}
		ContentTranslator cssInliner = cssBuilder.build();
		if(cssInliner!=null) {
			LOG.debug("CSS inlining is enabled");
			translator.addTranslator(cssInliner);
		}
	}
}
