package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client;

import com.sendgrid.helpers.mail.Mail;

import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.SendGridException;

/**
 * Description of the operations a service backed with SendGrid should expose.
 */
public interface SendGridClient {

    /**
     * Sends the provided email to SendGrid.
     * 
     * @param email
     *            the email to send, cannot be {@code null}
     * @throws SendGridException
     *             an unexpected error occurred when trying to send the email
     */
    void send(Mail email) throws SendGridException;

}
