package fr.sii.notification.core.builder;

import java.util.List;

import fr.sii.notification.core.sender.NotificationSender;


public interface NotificationSenderBuilder<N extends NotificationSender> extends Builder<List<N>> {
}
