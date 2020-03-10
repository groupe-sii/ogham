package fr.sii.ogham.core.builder.sender;

import static fr.sii.ogham.core.util.BuilderUtils.instantiateBuilder;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.ActivableAtRuntime;
import fr.sii.ogham.core.builder.BuildContext;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.annotation.RequiredClass;
import fr.sii.ogham.core.builder.annotation.RequiredClasses;
import fr.sii.ogham.core.builder.annotation.RequiredProperties;
import fr.sii.ogham.core.builder.annotation.RequiredProperty;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.priority.ImplementationPriorityProvider;
import fr.sii.ogham.core.builder.priority.PriorityProvider;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.fluent.MessageConditions;
import fr.sii.ogham.core.condition.provider.ImplementationConditionProvider;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.core.sender.MultiImplementationSender;

/**
 * Helps to configure a {@link MultiImplementationSender}.
 * 
 * <p>
 * It registers and uses {@link Builder}s to instantiate and configure a
 * {@link MessageSender} implementation.
 * </p>
 * 
 * <p>
 * It also let you provide your own direct {@link MessageSender} implementation.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder {@link MessageSender}
 *            {@link Builder}
 */
public class SenderImplementationBuilderHelper<P> {
	private static final Logger LOG = LoggerFactory.getLogger(SenderImplementationBuilderHelper.class);

	private final P parent;
	private final BuildContext buildContext;
	private final List<Builder<? extends MessageSender>> senderBuilders;
	private final List<MessageSender> customSenders;
	private final PriorityProvider<MessageSender> priorityProvider;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling and() method of any registered {@link Message}
	 * {@link Builder}. The {@link EnvironmentBuilder} is used to evaluate
	 * properties at build time (when
	 * {@link #addSenders(MultiImplementationSender)} is called).
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for property resolution and evaluation
	 */
	public SenderImplementationBuilderHelper(P parent, BuildContext buildContext) {
		super();
		this.parent = parent;
		this.buildContext = buildContext;
		senderBuilders = new ArrayList<>();
		customSenders = new ArrayList<>();
		priorityProvider = new ImplementationPriorityProvider<>(buildContext);
	}

	/**
	 * Returns true if at least either one custom sender or a sender builder was
	 * previously registered.
	 * 
	 * @return true if at least one custom sender or one sender builder
	 */
	public boolean hasRegisteredSenders() {
		return !customSenders.isEmpty() || !senderBuilders.isEmpty();
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
	 */
	public void customSender(MessageSender sender) {
		customSenders.add(sender);
	}

	/**
	 * Registers and configures sender through a dedicated builder.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * .register(JavaMailBuilder.class)
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
	 * ({@code &lt;P&gt;}). If you don't care about chaining, just provide a
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
	 * @param <B>
	 *            the type of the builder
	 * @return the builder to configure the implementation
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <B extends Builder<? extends MessageSender>> B register(Class<B> builderClass) {
		// if builder already registered => provide same instance
		for (Builder<? extends MessageSender> builder : senderBuilders) {
			if (builderClass.isAssignableFrom(builder.getClass())) {
				return (B) builder;
			}
		}
		B builder = instantiateBuilder(builderClass, parent, buildContext);
		senderBuilders.add(builder);
		return builder;
	}

	/**
	 * Add registered custom senders or built senders base on registered sender
	 * builders to the provided {@link MultiImplementationSender}.
	 * 
	 * <p>
	 * A {@link MultiImplementationSender} is able to evaluate a
	 * {@link Condition} when a message is being sent to determine which
	 * implementation can and should handle the message and really send it.
	 * </p>
	 * 
	 * <p>
	 * If a custom implementation was registered and is annotated by one or
	 * several of:
	 * <ul>
	 * <li>{@link RequiredClass}</li>
	 * <li>{@link RequiredProperty}</li>
	 * <li>{@link RequiredClasses}</li>
	 * <li>{@link RequiredProperties}</li>
	 * </ul>
	 * Then if condition evaluation returns true, the custom implementation will
	 * be used. If you provide several annotations, the custom implementation
	 * will be used only if all conditions are met (and operator).
	 * 
	 * <p>
	 * If a custom implementation was registered and implements
	 * {@link ActivableAtRuntime}, and the provided condition evaluation returns
	 * true, then the custom implementation will be used.
	 * 
	 * See {@link MessageConditions} to build your condition.
	 * </p>
	 * 
	 * <p>
	 * If neither annotations nor implementation of {@link ActivableAtRuntime}
	 * is used, then the custom implementation will be always used. All other
	 * implementations (even standard ones) will never be used.
	 * </p>
	 * 
	 * @param mainSender
	 *            the sender that manages several implementations
	 */
	public void addSenders(MultiImplementationSender<?> mainSender) {
		ImplementationConditionProvider implementationSelection = new ImplementationConditionProvider(buildContext.getPropertyResolver());
		for (MessageSender customSender : customSenders) {
			LOG.debug("Custom implementation {} registered into {}", customSender, mainSender);
			mainSender.addImplementation(implementationSelection.provide(customSender), customSender, priorityProvider.provide(customSender));
		}
		for (Builder<? extends MessageSender> builder : senderBuilders) {
			MessageSender sender = builder.build();
			if (sender != null) {
				LOG.debug("Implementation {} registered into {}", sender, mainSender);
				mainSender.addImplementation(implementationSelection.provide(builder), sender, priorityProvider.provide(sender));
			}
		}
	}

}
