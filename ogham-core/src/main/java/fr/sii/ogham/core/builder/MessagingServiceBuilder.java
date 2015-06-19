package fr.sii.ogham.core.builder;

import fr.sii.ogham.core.service.MessagingService;

/**
 * Specialized interface acting just as marker for building the messaging
 * service. The aim is to provide a default implementation of the builder but
 * let anyone define a different builder.
 * 
 * @author Aurélien Baudet
 *
 */
public interface MessagingServiceBuilder extends Builder<MessagingService> {
}
