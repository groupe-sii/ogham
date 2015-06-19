package fr.sii.ogham.core.builder;

import fr.sii.ogham.core.sender.MessageSender;


/**
 * Base interface acting as a marker for builders that help to construct a
 * message sender.
 * 
 * @author Aurélien Baudet
 *
 * @param <S>
 *            the type of the sender
 */
public interface MessagingSenderBuilder<S extends MessageSender> extends Builder<S> {
}
