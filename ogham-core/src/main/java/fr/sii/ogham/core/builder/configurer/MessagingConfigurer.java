package fr.sii.ogham.core.builder.configurer;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;

/**
 * Specialized interface of {@link Configurer} generic interface. This interface
 * is designed to configure a {@link MessagingBuilder} instance.
 * 
 * {@link MessagingBuilder}s are used to instantiate a {@link MessagingService}.
 * {@link MessagingBuilder} has static factory methods to provide predefined
 * behaviors.
 * 
 * To explicitly target a factory method, {@link MessagingConfigurer}
 * implementations can be annotated by a {@link ConfigurerFor} annotation.
 * 
 * @author Aurélien Baudet
 *
 */
public interface MessagingConfigurer extends Configurer<MessagingBuilder> {
}
