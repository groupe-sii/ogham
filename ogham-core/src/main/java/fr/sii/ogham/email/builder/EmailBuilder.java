package fr.sii.ogham.email.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.sender.SenderImplementationBuilderHelper;
import fr.sii.ogham.core.builder.template.TemplateBuilderHelper;
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
	
	private final EnvironmentBuilder<?> environmentBuilder;
	private final TemplateBuilderHelper<EmailBuilder> templateBuilderHelper;
	private final SenderImplementationBuilderHelper<EmailBuilder> senderBuilderHelper;
	private AttachmentHandlingBuilder attachmentBuilder;
	private AutofillEmailBuilder autofillBuilder;
	private CssHandlingBuilder cssBuilder;
	private ImageHandlingBuilder imageBuilder;

	public EmailBuilder(MessagingBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		templateBuilderHelper = new TemplateBuilderHelper<>(this, environmentBuilder);
		senderBuilderHelper = new SenderImplementationBuilderHelper<>(this, environmentBuilder);
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
	
	public <T extends Builder<? extends TemplateParser>> T template(Class<T> builderClass) {
		return templateBuilderHelper.register(builderClass);
	}

	public EmailBuilder customSender(MessageSender sender) {
		senderBuilderHelper.customSender(sender);
		return this;
	}
	
	public <T extends Builder<? extends MessageSender>> T sender(Class<T> builderClass) {
		return senderBuilderHelper.register(builderClass);
	}

	@Override
	public ConditionalSender build() throws BuildException {
		EmailSender emailSender = new EmailSender();
		ConditionalSender sender = emailSender;
		senderBuilderHelper.addSenders(emailSender);
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
		if(templateBuilderHelper.hasRegisteredTemplates() || cssBuilder!=null || imageBuilder!=null) {
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
