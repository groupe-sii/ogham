package fr.sii.ogham.spring.common;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;

/**
 * Spring configurer that simply delegates to standalone configurer. This is
 * useful when nothing from Spring is used.
 * 
 * @author Aurélien Baudet
 *
 */
public class StandaloneWrapperConfigurer implements SpringMessagingConfigurer {
	private final MessagingConfigurer delegate;
	private final int priority;

	public StandaloneWrapperConfigurer(MessagingConfigurer delegate, int priority) {
		super();
		this.delegate = delegate;
		this.priority = priority;
	}

	@Override
	public void configure(MessagingBuilder builder) {
		delegate.configure(builder);
	}

	@Override
	public int getOrder() {
		return priority;
	}

}
