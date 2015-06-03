package fr.sii.notification.sms.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.util.ArrayUtils;
import fr.sii.notification.core.util.EqualsBuilder;
import fr.sii.notification.core.util.HashCodeBuilder;
import fr.sii.notification.core.util.StringUtils;
import fr.sii.notification.sms.builder.SmsBuilder;

/**
 * SMS message that contains the following information:
 * <ul>
 * <li>The content of the SMS (see {@link Content} and sub classes for more
 * information)</li>
 * <li>The sender information (name and phone number). Name is optional</li>
 * <li>The list of recipients (name and phone number). Name is optional</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
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

	/**
	 * Initialize the SMS with the following information:
	 * <ul>
	 * <li>The content of the SMS as string, it will create a
	 * {@link StringContent} for you</li>
	 * <li>One or several recipients using only phone numbers</li>
	 * </ul>
	 * <p>
	 * No sender is specified. This information can be automatically added
	 * according to the behaviors you have selected see {@link SmsBuilder} for
	 * more information). Alternatively, you can manually add it using
	 * {@link #setFrom(Sender)} or {@link #setFrom(String)} methods.
	 * </p>
	 * 
	 * @param content
	 *            the content of the SMS
	 * @param to
	 *            one mandatory recipient for the SMS
	 * @param tos
	 *            the other optional recipients of the SMS
	 */
	public Sms(String content, String to, String... tos) {
		this(content, toRecipient(ArrayUtils.concat(to, tos)));
	}

	/**
	 * Initialize the SMS with the following information:
	 * <ul>
	 * <li>The content of the SMS as string, it will create a
	 * {@link StringContent} for you</li>
	 * <li>The sender information (name and phone number). Name is optional</li>
	 * <li>One or several recipients using only phone numbers</li>
	 * </ul>
	 * 
	 * @param content
	 *            the content of the SMS
	 * @param to
	 *            one mandatory recipient for the SMS
	 * @param tos
	 *            the other optional recipients of the SMS
	 */
	public Sms(String content, PhoneNumber to, PhoneNumber... tos) {
		this(content, toRecipient(ArrayUtils.concat(to, tos)));
	}

	/**
	 * Initialize the SMS with the following information:
	 * <ul>
	 * <li>The content of the SMS as string, it will create a
	 * {@link StringContent} for you</li>
	 * <li>None, one or several recipients (name and phone number). Name is
	 * optional</li>
	 * </ul>
	 * <p>
	 * No sender is specified. This information can be automatically added
	 * according to the behaviors you have selected see {@link SmsBuilder} for
	 * more information). Alternatively, you can manually add it using
	 * {@link #setFrom(Sender)} or {@link #setFrom(String)} methods.
	 * </p>
	 * 
	 * @param content
	 *            the content of the SMS
	 * @param to
	 *            the recipients of the SMS
	 */
	public Sms(String content, Recipient... to) {
		this(content, new ArrayList<>(Arrays.asList(to)));
	}

	/**
	 * Initialize the SMS with the following information:
	 * <ul>
	 * <li>The content of the SMS as string, it will create a
	 * {@link StringContent} for you</li>
	 * <li>The list of recipients (name and phone number). Name is optional</li>
	 * </ul>
	 * <p>
	 * No sender is specified. This information can be automatically added
	 * according to the behaviors you have selected see {@link SmsBuilder} for
	 * more information). Alternatively, you can manually add it using
	 * {@link #setFrom(Sender)} or {@link #setFrom(String)} methods.
	 * </p>
	 * 
	 * @param content
	 *            the content of the SMS
	 * @param to
	 *            the recipients of the SMS
	 */
	public Sms(String content, List<Recipient> to) {
		this(content, null, to);
	}

	/**
	 * Initialize the SMS with the following information:
	 * <ul>
	 * <li>The content of the SMS as string, it will create a
	 * {@link StringContent} for you</li>
	 * <li>The sender information (name and phone number). Name is optional</li>
	 * <li>One or several recipients using only phone numbers</li>
	 * </ul>
	 * 
	 * @param content
	 *            the content of the SMS
	 * @param from
	 *            the sender of the SMS
	 * @param to
	 *            one mandatory recipient for the SMS
	 * @param tos
	 *            the other optional recipients of the SMS
	 */
	public Sms(String content, Sender from, String to, String... tos) {
		this(content, from, toRecipient(ArrayUtils.concat(to, tos)));
	}

	/**
	 * Initialize the SMS with the following information:
	 * <ul>
	 * <li>The content of the SMS as string, it will create a
	 * {@link StringContent} for you</li>
	 * <li>The sender information (name and phone number). Name is optional</li>
	 * <li>One or several recipients using only phone numbers</li>
	 * </ul>
	 * 
	 * @param content
	 *            the content of the SMS
	 * @param from
	 *            the sender of the SMS
	 * @param to
	 *            one mandatory recipient for the SMS
	 * @param tos
	 *            the other optional recipients of the SMS
	 */
	public Sms(String content, Sender from, PhoneNumber to, PhoneNumber... tos) {
		this(content, from, toRecipient(ArrayUtils.concat(to, tos)));
	}

	/**
	 * Initialize the SMS with the following information:
	 * <ul>
	 * <li>The content of the SMS as string, it will create a
	 * {@link StringContent} for you</li>
	 * <li>The sender information (name and phone number). Name is optional</li>
	 * <li>None, one or several recipients (name and phone number). Name is
	 * optional</li>
	 * </ul>
	 * 
	 * @param content
	 *            the content of the SMS
	 * @param from
	 *            the sender of the SMS
	 * @param to
	 *            the recipients of the SMS
	 */
	public Sms(String content, Sender from, Recipient... to) {
		this(content, from, new ArrayList<>(Arrays.asList(to)));
	}

	/**
	 * Initialize the SMS with the following information:
	 * <ul>
	 * <li>The content of the SMS as string, it will create a
	 * {@link StringContent} for you</li>
	 * <li>The sender information (name and phone number). Name is optional</li>
	 * <li>The list of recipients (name and phone number). Name is optional</li>
	 * </ul>
	 * 
	 * @param content
	 *            the content of the SMS
	 * @param from
	 *            the sender of the SMS
	 * @param to
	 *            the recipients of the SMS
	 */
	public Sms(String content, Sender from, List<Recipient> to) {
		this(new StringContent(content), from, to);
	}

	/**
	 * Initialize the SMS with the following information:
	 * <ul>
	 * <li>The content of the SMS (see {@link Content} and sub classes for more
	 * information)</li>
	 * <li>One or several recipients using only phone numbers</li>
	 * </ul>
	 * <p>
	 * No sender is specified. This information can be automatically added
	 * according to the behaviors you have selected see {@link SmsBuilder} for
	 * more information). Alternatively, you can manually add it using
	 * {@link #setFrom(Sender)} or {@link #setFrom(String)} methods.
	 * </p>
	 * 
	 * @param content
	 *            the content of the SMS
	 * @param to
	 *            one mandatory recipient for the SMS
	 * @param tos
	 *            the other optional recipients of the SMS
	 */
	public Sms(Content content, String to, String... tos) {
		this(content, toRecipient(ArrayUtils.concat(to, tos)));
	}

	/**
	 * Initialize the SMS with the following information:
	 * <ul>
	 * <li>The content of the SMS (see {@link Content} and sub classes for more
	 * information)</li>
	 * <li>The sender information (name and phone number). Name is optional</li>
	 * <li>One or several recipients using only phone numbers</li>
	 * </ul>
	 * 
	 * @param content
	 *            the content of the SMS
	 * @param to
	 *            one mandatory recipient for the SMS
	 * @param tos
	 *            the other optional recipients of the SMS
	 */
	public Sms(Content content, PhoneNumber to, PhoneNumber... tos) {
		this(content, toRecipient(ArrayUtils.concat(to, tos)));
	}

	/**
	 * Initialize the SMS with the following information:
	 * <ul>
	 * <li>The content of the SMS (see {@link Content} and sub classes for more
	 * information)</li>
	 * <li>None, one or several recipients (name and phone number). Name is
	 * optional</li>
	 * </ul>
	 * <p>
	 * No sender is specified. This information can be automatically added
	 * according to the behaviors you have selected see {@link SmsBuilder} for
	 * more information). Alternatively, you can manually add it using
	 * {@link #setFrom(Sender)} or {@link #setFrom(String)} methods.
	 * </p>
	 * 
	 * @param content
	 *            the content of the SMS
	 * @param to
	 *            the recipients of the SMS
	 */
	public Sms(Content content, Recipient... to) {
		this(content, new ArrayList<>(Arrays.asList(to)));
	}

	/**
	 * Initialize the SMS with the following information:
	 * <ul>
	 * <li>The content of the SMS (see {@link Content} and sub classes for more
	 * information)</li>
	 * <li>The list of recipients (name and phone number). Name is optional</li>
	 * </ul>
	 * <p>
	 * No sender is specified. This information can be automatically added
	 * according to the behaviors you have selected see {@link SmsBuilder} for
	 * more information). Alternatively, you can manually add it using
	 * {@link #setFrom(Sender)} or {@link #setFrom(String)} methods.
	 * </p>
	 * 
	 * @param content
	 *            the content of the SMS
	 * @param to
	 *            the recipients of the SMS
	 */
	public Sms(Content content, List<Recipient> to) {
		this(content, null, to);
	}

	/**
	 * Initialize the SMS with the following information:
	 * <ul>
	 * <li>The content of the SMS (see {@link Content} and sub classes for more
	 * information)</li>
	 * <li>The sender information (name and phone number). Name is optional</li>
	 * <li>One or several recipients using only phone numbers</li>
	 * </ul>
	 * 
	 * @param content
	 *            the content of the SMS
	 * @param from
	 *            the sender of the SMS
	 * @param to
	 *            one mandatory recipient for the SMS
	 * @param tos
	 *            the other optional recipients of the SMS
	 */
	public Sms(Content content, Sender from, String to, String... tos) {
		this(content, from, toRecipient(ArrayUtils.concat(to, tos)));
	}

	/**
	 * Initialize the SMS with the following information:
	 * <ul>
	 * <li>The content of the SMS (see {@link Content} and sub classes for more
	 * information)</li>
	 * <li>The sender information (name and phone number). Name is optional</li>
	 * <li>One or several recipients using only phone numbers</li>
	 * </ul>
	 * 
	 * @param content
	 *            the content of the SMS
	 * @param from
	 *            the sender of the SMS
	 * @param to
	 *            one mandatory recipient for the SMS
	 * @param tos
	 *            the other optional recipients of the SMS
	 */
	public Sms(Content content, Sender from, PhoneNumber to, PhoneNumber... tos) {
		this(content, from, toRecipient(ArrayUtils.concat(to, tos)));
	}

	/**
	 * Initialize the SMS with the following information:
	 * <ul>
	 * <li>The content of the SMS (see {@link Content} and sub classes for more
	 * information)</li>
	 * <li>The sender information (name and phone number). Name is optional</li>
	 * <li>None, one or several recipients (name and phone number). Name is
	 * optional</li>
	 * </ul>
	 * 
	 * @param content
	 *            the content of the SMS
	 * @param from
	 *            the sender of the SMS
	 * @param to
	 *            the recipients of the SMS
	 */
	public Sms(Content content, Sender from, Recipient... to) {
		this(content, from, new ArrayList<>(Arrays.asList(to)));
	}

	/**
	 * Initialize the SMS with the following information:
	 * <ul>
	 * <li>The content of the SMS (see {@link Content} and sub classes for more
	 * information)</li>
	 * <li>The sender information (name and phone number). Name is optional</li>
	 * <li>The list of recipients (name and phone number). Name is optional</li>
	 * </ul>
	 * 
	 * @param content
	 *            the content of the SMS
	 * @param from
	 *            the sender of the SMS
	 * @param to
	 *            the recipients of the SMS
	 */
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
	 * Set the sender phone number of the SMS as string.
	 * 
	 * @param phoneNumber
	 *            SMS sender phone number to set
	 */
	public void setFrom(String phoneNumber) {
		setFrom(new Sender(phoneNumber));
	}

	/**
	 * Set the sender.
	 * 
	 * @param from
	 *            the sender
	 * @return this instance for fluent use
	 */
	public Sms withFrom(Sender from) {
		setFrom(from);
		return this;
	}

	/**
	 * Set the sender using the phone number as string.
	 * 
	 * @param phoneNumber
	 *            the sender number
	 * @return this instance for fluent use
	 */
	public Sms withFrom(String phoneNumber) {
		return withFrom(null, phoneNumber);
	}

	/**
	 * Set the sender using the phone number.
	 * 
	 * @param phoneNumber
	 *            the sender number
	 * @return this instance for fluent use
	 */
	public Sms withFrom(PhoneNumber phoneNumber) {
		return withFrom(null, phoneNumber);
	}

	/**
	 * Set the sender using the phone number as string.
	 * 
	 * @param name
	 *            the name of the sender
	 * @param phoneNumber
	 *            the sender number
	 * @return this instance for fluent use
	 */
	public Sms withFrom(String name, String phoneNumber) {
		return withFrom(name, new PhoneNumber(phoneNumber));
	}

	/**
	 * Set the sender using the phone number.
	 * 
	 * @param name
	 *            the name of the sender
	 * @param phoneNumber
	 *            the sender number
	 * @return this instance for fluent use
	 */
	public Sms withFrom(String name, PhoneNumber phoneNumber) {
		return withFrom(new Sender(name, phoneNumber));
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
		addRecipient(new Recipient(name, number));
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
	 * Set the content of the message.
	 * 
	 * @param content
	 *            the content of the message
	 * @return this instance for fluent use
	 */
	public Sms withContent(Content content) {
		setContent(content);
		return this;
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
		builder.append("\r\nTo: ").append(StringUtils.join(recipients, ", "));
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
