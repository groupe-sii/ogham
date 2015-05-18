package fr.sii.notification.core.builder;

import fr.sii.notification.core.service.NotificationService;

/**
 * Specialized interface acting just as marker for building the notification
 * service. The aim is to provide a default implementation of the builder but
 * let anyone define a different builder.
 * 
 * @author Aurélien Baudet
 *
 */
public interface NotificationServiceBuilder extends Builder<NotificationService> {
}
