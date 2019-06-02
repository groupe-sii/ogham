package fr.sii.ogham.email.sendgrid.sender;

import fr.sii.ogham.core.sender.SpecializedSender;
import fr.sii.ogham.email.message.Email;

/**
 * Base interface for SendGrid sender that is necessary to abstract
 * implementations for each SendGrid library versions
 * 
 * @author Aurélien Baudet
 *
 */
public interface SendGridSender extends SpecializedSender<Email> {

}
