package fr.sii.ogham.sms.sender.impl;

import static fr.sii.ogham.sms.OvhSmsConstants.DEFAULT_OVHSMS_HTTP2SMS_IMPLEMENTATION_PRIORITY;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.sii.ogham.core.builder.priority.Priority;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.util.HttpException;
import fr.sii.ogham.core.exception.util.PhoneNumberException;
import fr.sii.ogham.core.sender.AbstractSpecializedSender;
import fr.sii.ogham.core.util.StringUtils;
import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.Recipient;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.ovh.OvhAuthParams;
import fr.sii.ogham.sms.sender.impl.ovh.OvhOptions;
import fr.sii.ogham.sms.sender.impl.ovh.SmsCoding;
import fr.sii.ogham.sms.sender.impl.ovh.SmsCodingDetector;
import fr.sii.ogham.sms.util.HttpUtils;
import fr.sii.ogham.sms.util.http.Parameter;
import fr.sii.ogham.sms.util.http.Response;

/**
 * Implementation that is able to send SMS through <a href=
 * "https://docs.ovh.com/fr/sms/envoyer_des_sms_depuis_une_url_-_http2sms/#objectif">OVH
 * web service</a>. This sender requires that phone numbers are provided using
 * either:
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/E.164">E.164 international
 * format</a> (prefixed with '+' followed by country code)</li>
 * <li><a href="https://en.wikipedia.org/wiki/E.123">E.123 international
 * format</a> (prefixed with '+' followed by country code and can contain
 * spaces)</li>
 * <li>Using 13 digits: country code on 4 digits followed by number. For
 * example, 0033 6 01 02 03 04 is a valid French number (country code is 33,
 * additional '0' are added to reach the 4 digits)</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
@Priority(properties = "${ogham.sms.implementation-priority.ovh-http2sms}", defaultValue = DEFAULT_OVHSMS_HTTP2SMS_IMPLEMENTATION_PRIORITY)
public class OvhSmsSender extends AbstractSpecializedSender<Sms> {
	private static final Logger LOG = LoggerFactory.getLogger(OvhSmsSender.class);
	private static final String CONTENT_TYPE = "application/json";
	private static final String RESPONSE_TYPE = "contentType";
	private static final String MESSAGE = "message";
	private static final String SMS_CODING = "smsCoding";
	private static final String TO = "to";
	private static final String FROM = "from";
	private static final String RECIPIENTS_SEPARATOR = ",";
	private static final int OK_STATUS = 200;
	private static final int INTERNATIONAL_FORMAT_LENGTH = 13;
	private static final Pattern SPACES = Pattern.compile("\\s+");

	/**
	 * The authentication parameters
	 */
	private final OvhAuthParams authParams;

	/**
	 * The OVH options
	 */
	private final OvhOptions options;

	/**
	 * This is used to parse JSON response
	 */
	private final ObjectMapper mapper;

	/**
	 * The URL to OVH web service
	 */
	private final URL url;

	/**
	 * If {@link SmsCoding} not set, detects which {@link SmsCoding} can be used
	 * to encode the message
	 */
	private final SmsCodingDetector smsCodingDetector;

	public OvhSmsSender(URL url, OvhAuthParams authParams, OvhOptions options, SmsCodingDetector smsCodingDetector) {
		super();
		this.url = url;
		this.authParams = authParams;
		this.options = options;
		this.smsCodingDetector = smsCodingDetector;
		this.mapper = new ObjectMapper();
	}

	@Override
	public void send(Sms message) throws MessageException {
		try {
			String text = getContent(message);
			// @formatter:off
			Response response = HttpUtils.get(url.toString(), authParams, options,
									new Parameter(SMS_CODING, getSmsCodingValue(text)),
									new Parameter(RESPONSE_TYPE, CONTENT_TYPE),
									// convert phone number to international format
									new Parameter(FROM, toInternational(message.getFrom().getPhoneNumber())),
									new Parameter(TO, StringUtils.join(convert(message.getRecipients()), RECIPIENTS_SEPARATOR)),
									// TODO: manage long messages: how to do ??
									new Parameter(MESSAGE, text));
			// @formatter:on
			handleResponse(message, response);
		} catch (IOException e) {
			throw new MessageException("Failed to read response when sending SMS through OVH", message, e);
		} catch (HttpException e) {
			throw new MessageException("Failed to send SMS through OVH", message, e);
		} catch (PhoneNumberException e) {
			throw new MessageException("Failed to send SMS through OVH (invalid phone number)", message, e);
		}
	}

	private Integer getSmsCodingValue(String message) {
		SmsCoding smsCoding = getSmsCoding(message);
		return smsCoding == null ? null : smsCoding.getValue();
	}

	private SmsCoding getSmsCoding(String message) {
		if (options.getSmsCoding() != null) {
			return options.getSmsCoding();
		}
		return smsCodingDetector.detect(message);
	}

	/**
	 * Handle OVH response. If status provided in response is less than 200,
	 * then the message has been sent. Otherwise, the message has not been sent.
	 * 
	 * @param message
	 *            the SMS to send
	 * @param response
	 *            the received response from OVH API
	 * @throws IOException
	 *             when the response couldn't be read
	 * @throws JsonProcessingException
	 *             when the response format is not valid JSON
	 * @throws MessageNotSentException
	 *             generated exception to indicate that the message couldn't be
	 *             sent
	 */
	private void handleResponse(Sms message, Response response) throws IOException, MessageNotSentException {
		if (response.getStatus().isSuccess()) {
			JsonNode json = mapper.readTree(response.getBody());
			int ovhStatus = json.get("status").asInt();
			// 100 <= ovh status < 200 ====> OK -> just log response
			// 200 <= ovh status ====> KO -> throw an exception
			if (ovhStatus >= OK_STATUS) {
				LOG.error("SMS failed to be sent through OVH");
				LOG.debug("Sent SMS: {}", message);
				LOG.debug("Response status {}", response.getStatus());
				LOG.debug("Response body {}", response.getBody());
				throw new MessageNotSentException("SMS couldn't be sent through OVH: " + json.get(MESSAGE).asText(), message);
			} else {
				LOG.info("SMS successfully sent through OVH");
				LOG.debug("Sent SMS: {}", message);
				LOG.debug("Response: {}", response.getBody());
			}
		} else {
			LOG.error("Response status {}", response.getStatus());
			LOG.error("Response body {}", response.getBody());
			throw new MessageNotSentException("SMS couldn't be sent. Response status is " + response.getStatus(), message);
		}
	}

	/**
	 * Get the content of the SMS and apply some transformations on it to be
	 * usable by OVH.
	 * 
	 * @param message
	 *            the message that contains the content to extract
	 * @return the content formatted for OVH
	 */
	private static String getContent(Sms message) {
		// if a string contains \r\n, only \r is kept
		// if there are \n without \r, those \n are converted to \r
		return message.getContent().toString().replaceAll("(\r)?\n", "\r");
	}

	/**
	 * Convert the list of SMS recipients to international phone numbers usable
	 * by OVH.
	 * 
	 * @param recipients
	 *            the list of recipients
	 * @return the list of international phone numbers
	 * @throws PhoneNumberException
	 *             when phone number can't be handled by OVH
	 */
	private static List<String> convert(List<Recipient> recipients) throws PhoneNumberException {
		List<String> tos = new ArrayList<>(recipients.size());
		// convert phone numbers to international format
		for (Recipient recipient : recipients) {
			tos.add(toInternational(recipient.getPhoneNumber()));
		}
		return tos;
	}

	/**
	 * Convert a raw phone number to its international form usable by OVH (13
	 * digits with no space).
	 * 
	 * @param phoneNumber
	 *            the phone number to transform
	 * @return the international phone number
	 * @throws PhoneNumberException
	 *             when phone number can't be handled by OVH
	 */
	private static String toInternational(PhoneNumber phoneNumber) throws PhoneNumberException {
		String number = phoneNumber.getNumber();
		if (number.startsWith("+") || number.length() == INTERNATIONAL_FORMAT_LENGTH) {
			String withoutPlus = number.replace("+", "");
			String withoutSpaces = SPACES.matcher(withoutPlus).replaceAll("");
			return StringUtils.leftPad(withoutSpaces, INTERNATIONAL_FORMAT_LENGTH, '0');
		} else {
			throw new PhoneNumberException("Invalid phone number. OVH only accepts international phone numbers. Please write the phone number with the country prefix. "
					+ "For example, if the number is 0601020304 and it is a French number, then the international number is +33601020304", phoneNumber);
		}
	}

	public OvhAuthParams getAuthParams() {
		return authParams;
	}

	public OvhOptions getOptions() {
		return options;
	}

	public URL getUrl() {
		return url;
	}
}
