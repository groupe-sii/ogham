package fr.sii.notification.core.builder;

import fr.sii.notification.core.sender.NotificationSender;


/**
 * Base interface acting as a marker for builders that help to construct a
 * notification sender.
 * 
 * @author Aurélien Baudet
 *
 * @param <S>
 *            the type of the sender
 */
public interface NotificationSenderBuilder<S extends NotificationSender> extends Builder<S> {
}
