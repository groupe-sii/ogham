package fr.sii.ogham.core.condition.provider;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.sender.MessageSender;

public class ImplementationConditionProvider implements ConditionProvider<Builder<? extends MessageSender>, Message> {
	private final AnnotationConditionProvider<Message> annotationProvider;
	private final ActivableAtRuntimeConditionProvider runtimeProvider;
	
	public ImplementationConditionProvider(PropertyResolver propertyResolver) {
		super();
		annotationProvider = new AnnotationConditionProvider<>(propertyResolver);
		runtimeProvider = new ActivableAtRuntimeConditionProvider();
	}

	@Override
	public Condition<Message> provide(Builder<? extends MessageSender> source) {
		AndCondition<Message> mainCondition = new AndCondition<>();
		mainCondition.and(annotationProvider.provide(source.getClass()));
		mainCondition.and(runtimeProvider.provide(source));
		return mainCondition;
	}
	
}
