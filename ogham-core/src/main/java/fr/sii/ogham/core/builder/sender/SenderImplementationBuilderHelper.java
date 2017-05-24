package fr.sii.ogham.core.builder.sender;

import static fr.sii.ogham.core.condition.fluent.MessageConditions.alwaysTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.condition.provider.ImplementationConditionProvider;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.core.sender.MultiImplementationSender;

public class SenderImplementationBuilderHelper<P> {
	private static final Logger LOG = LoggerFactory.getLogger(SenderImplementationBuilderHelper.class);

	private final P parent;
	private final EnvironmentBuilder<?> environmentBuilder;
	private final List<Builder<? extends MessageSender>> senderBuilders;
	private final List<MessageSender> customSenders;
	
	public SenderImplementationBuilderHelper(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super();
		this.parent = parent;
		this.environmentBuilder = environmentBuilder;
		senderBuilders = new ArrayList<>();
		customSenders = new ArrayList<>();
	}
	
	public boolean hasRegisteredSenders() {
		return !customSenders.isEmpty() || !senderBuilders.isEmpty();
	}

	public void customSender(MessageSender sender) {
		customSenders.add(sender);
	}
	

	@SuppressWarnings("unchecked")
	public <T extends Builder<? extends MessageSender>> T register(Class<T> builderClass) {
		// if builder already registered => provide same instance
		for(Builder<? extends MessageSender> builder : senderBuilders) {
			if(builderClass.isAssignableFrom(builder.getClass())) {
				return (T) builder;
			}
		}
		try {
			T builder;
			Constructor<T> constructor = builderClass.getConstructor(parent.getClass());
			if(constructor!=null) {
				builder = constructor.newInstance(parent);
			} else {
				builder = builderClass.newInstance();
			}
			senderBuilders.add(builder);
			return builder;
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			throw new BuildException("Can't instantiate builder from class "+builderClass.getSimpleName(), e);
		}
	}
	
	public void addSenders(MultiImplementationSender<?> mainSender) {
		for(MessageSender s : customSenders) {
			mainSender.addImplementation(alwaysTrue(), s);
		}
		ImplementationConditionProvider implementationSelection = new ImplementationConditionProvider(environmentBuilder.build());
		for(Builder<? extends MessageSender> builder : senderBuilders) {
			MessageSender s = builder.build();
			if(s!=null) {
				LOG.debug("Implementation {} registered", s);
				mainSender.addImplementation(implementationSelection.provide(builder), s);
			}
		}
	}

}
