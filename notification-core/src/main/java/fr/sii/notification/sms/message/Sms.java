package fr.sii.notification.sms.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.util.EqualsBuilder;
import fr.sii.notification.core.util.HashCodeBuilder;
import fr.sii.notification.core.util.StringUtils;

public class Sms implements Message {
	/**
	 * The number of the sender
	 */
	private Sender from;

	/**
	 * The list of recipients of the SMS
	 */
	private List<Recipient> recipients;

	/**
	 * The content of the SMS
	 */
	private Content content;

	public Sms(Content content, Sender from, String to, String... tos) {
	}

	public Sms(Content content, Sender from, PhoneNumber to, PhoneNumber... tos) {
	}

	public Sms(Content content, Sender from, Recipient... to) {
	}

	public Sms(Content content, Sender from, List<Recipient> to) {
		super();
		this.content = content;
		this.from = from;
		this.recipients = to;
	}

	/**
	 * Get the sender of the SMS
	 * 
	 * @return SMS sender
	 */
	public Sender getFrom() {
		return from;
	}

	/**
	 * Set the sender of the SMS
	 * 
	 * @param from
	 *            SMS sender to set
	 */
	public void setFrom(Sender from) {
		this.from = from;
	}

	/**
	 * Get the recipients of the SMS
	 * 
	 * @return SMS recipients
	 */
	public List<Recipient> getRecipients() {
		return recipients;
	}

	/**
	 * Set the recipients of the SMS
	 * 
	 * @param to
	 *            SMS recipients
	 */
	public void setRecipients(List<Recipient> to) {
		this.recipients = to;
	}

	/**
	 * Add a recipient specifying the phone number.
	 * 
	 * @param number
	 *            the number of the recipient
	 * @return this instance for fluent use
	 */
	public Sms addRecipient(PhoneNumber number) {
		addRecipient(null, number);
		return this;
	}

	/**
	 * Add a recipient specifying the name and the phone number.
	 * 
	 * @param name
	 *            the name of the recipient
	 * @param number
	 *            the number of the recipient
	 * @return this instance for fluent use
	 */
	public Sms addRecipient(String name, PhoneNumber number) {
		addRecipient(new Recipient(number));
		return this;
	}

	/**
	 * Add a recipient.
	 * 
	 * @param recipient
	 *            the recipient to add
	 * @return this instance for fluent use
	 */
	public Sms addRecipient(Recipient recipient) {
		recipients.add(recipient);
		return this;
	}

	@Override
	public Content getContent() {
		return content;
	}

	@Override
	public void setContent(Content content) {
		this.content = content;
	}

	/**
	 * Converts a list of phone numbers to a list of recipients.
	 * 
	 * @param to
	 *            the list of phone numbers
	 * @return the list of recipients
	 */
	public static Recipient[] toRecipient(PhoneNumber[] to) {
		Recipient[] addresses = new Recipient[to.length];
		int i = 0;
		for (PhoneNumber t : to) {
			addresses[i++] = new Recipient(t);
		}
		return addresses;
	}

	/**
	 * Converts a list of string to a list of recipients.
	 * 
	 * @param to
	 *            the list of phone numbers as string
	 * @return the list of recipients
	 */
	public static Recipient[] toRecipient(String[] to) {
		Recipient[] addresses = new Recipient[to.length];
		int i = 0;
		for (String t : to) {
			addresses[i++] = new Recipient(t);
		}
		return addresses;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Sms message\r\nFrom: ").append(from);
		builder.append("To: ").append(StringUtils.join(recipients, ", "));
		builder.append("\r\n----------------------------------\r\n").append(content);
		builder.append("\r\n==================================\r\n");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(from).append(recipients).append(content).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("from", "recipients", "content").isEqual();
	}
}
