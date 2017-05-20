package fr.sii.ogham.sms.builder;

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
import fr.sii.ogham.core.translator.content.TemplateContentTranslator;
import fr.sii.ogham.sms.builder.PhoneNumbersBuilder.PhoneNumberTranslatorPair;
import fr.sii.ogham.sms.sender.PhoneNumberTranslatorSender;
import fr.sii.ogham.sms.sender.SmsSender;

public class SmsBuilder extends AbstractParent<MessagingBuilder> implements Builder<ConditionalSender> {
	private static final Logger LOG = LoggerFactory.getLogger(SmsBuilder.class);
	
	private EnvironmentBuilder<?> environmentBuilder;
	private final TemplateBuilderHelper<SmsBuilder> templateBuilderHelper;
	private final SenderImplementationBuilderHelper<SmsBuilder> senderBuilderHelper;
	private AutofillSmsBuilder autofillBuilder;
	private PhoneNumbersBuilder phoneNumbersBuilder;
	
	public SmsBuilder(MessagingBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		templateBuilderHelper = new TemplateBuilderHelper<>(this, environmentBuilder);
		senderBuilderHelper = new SenderImplementationBuilderHelper<>(this, environmentBuilder);
	}

	public AutofillSmsBuilder autofill() {
		if(autofillBuilder==null) {
			autofillBuilder = new AutofillSmsBuilder(this, environmentBuilder);
		}
		return autofillBuilder;
	}

	public PhoneNumbersBuilder numbers() {
		if(phoneNumbersBuilder==null) {
			phoneNumbersBuilder = new PhoneNumbersBuilder(this, environmentBuilder);
		}
		return phoneNumbersBuilder;
	}
	
	public <T extends Builder<? extends TemplateParser>> T template(Class<T> builderClass) {
		return templateBuilderHelper.register(builderClass);
	}

	public SmsBuilder customSender(MessageSender sender) {
		senderBuilderHelper.customSender(sender);
		return this;
	}
	
	public <T extends Builder<? extends MessageSender>> T sender(Class<T> builderClass) {
		return senderBuilderHelper.register(builderClass);
	}


	@Override
	public ConditionalSender build() throws BuildException {
		SmsSender smsSender = new SmsSender();
		ConditionalSender sender = smsSender;
		senderBuilderHelper.addSenders(smsSender);
		if(templateBuilderHelper.hasRegisteredTemplates()) {
			ContentTranslator translator = buildContentTranslator();
			LOG.debug("Content translation enabled {}", translator);
			sender = new ContentTranslatorSender(translator, sender);
		}
		if(phoneNumbersBuilder!=null) {
			PhoneNumberTranslatorPair pair = phoneNumbersBuilder.build();
			sender = new PhoneNumberTranslatorSender(pair.getSender(), pair.getRecipient(), sender);
		}
		if(autofillBuilder!=null) {
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
