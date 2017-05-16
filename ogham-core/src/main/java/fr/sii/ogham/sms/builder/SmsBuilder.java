package fr.sii.ogham.sms.builder;

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
import fr.sii.ogham.core.builder.template.TemplateBuilder;
import fr.sii.ogham.core.condition.FixedCondition;
import fr.sii.ogham.core.condition.provider.ImplementationConditionProvider;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.filler.MessageFiller;
import fr.sii.ogham.core.message.Message;
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
	private TemplateBuilder<SmsBuilder> templateBuilder;
	private AutofillSmsBuilder autofillBuilder;
	private List<Builder<? extends MessageSender>> senderBuilders;
	private List<MessageSender> customSenders;
	private PhoneNumbersBuilder phoneNumbersBuilder;
	
	public SmsBuilder(MessagingBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		senderBuilders = new ArrayList<>();
		customSenders = new ArrayList<>();
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
	
	public TemplateBuilder<SmsBuilder> template() {
		if(templateBuilder==null) {
			templateBuilder = new TemplateBuilder<>(this, environmentBuilder);
		}
		return templateBuilder;
	}

	public SmsBuilder customSender(MessageSender sender) {
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
			Constructor<T> constructor = builderClass.getConstructor(SmsBuilder.class);
			if(constructor!=null) {
				builder = constructor.newInstance(this);
			} else {
				builder = builderClass.newInstance();
			}
			senderBuilders.add(builder);
			return builder;
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			throw new BuildException("Can't instantiate builder class "+builderClass.getSimpleName(), e);
		}
	}

	@Override
	public ConditionalSender build() throws BuildException {
		SmsSender smsSender = new SmsSender();
		ConditionalSender sender = smsSender;
		for(MessageSender s : customSenders) {
			smsSender.addImplementation(new FixedCondition<Message>(true), s);
		}
		ImplementationConditionProvider implementationSelection = new ImplementationConditionProvider(environmentBuilder.build());
		for(Builder<? extends MessageSender> builder : senderBuilders) {
			MessageSender s = builder.build();
			if(s!=null) {
				LOG.debug("Implementation {} registered", s);
				smsSender.addImplementation(implementationSelection.provide(builder), s);
			}
		}
		if(templateBuilder!=null) {
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
		if (templateBuilder == null) {
			return;
		}
		TemplateParser templateParser = templateBuilder.build();
		LOG.debug("Registering content translator that parses templates using {}", templateParser);
		translator.addTranslator(new TemplateContentTranslator(templateParser));
	}


}
