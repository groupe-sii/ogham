package fr.sii.ogham.assertion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.jsmpp.bean.SubmitSm;
import org.w3c.dom.Document;

import com.google.common.base.Charsets;
import com.icegreen.greenmail.junit.GreenMailRule;

import fr.sii.ogham.assertion.context.Context;
import fr.sii.ogham.assertion.email.EmailsAssert;
import fr.sii.ogham.assertion.email.ReceivedEmailsAssert;
import fr.sii.ogham.assertion.hamcrest.CustomReason;
import fr.sii.ogham.assertion.hamcrest.IdenticalHtmlMatcher;
import fr.sii.ogham.assertion.hamcrest.SimilarHtmlMatcher;
import fr.sii.ogham.assertion.sms.ReceivedSmsAssert;
import fr.sii.ogham.assertion.sms.SmsListAssert;
import fr.sii.ogham.helper.sms.rule.SmppServerRule;

/**
 * Utility class that helps writing message assertions. For emails, you can
 * write something like this:
 * 
 * <pre>
 * assertThat(greenMail).receivedMessages().count(is(1)).message(0).subject(is("Test")).from().address(hasItems("test.sender@sii.fr")).and().to().address(hasItems("recipient@sii.fr")).and().body()
 * 		.contentAsString(is("body")).contentType(startsWith("text/plain")).and().alternative(nullValue()).attachments(hasSize(1)).attachment("04-Java-OOP-Basics.pdf")
 * 		.content(is(resource("/attachment/04-Java-OOP-Basics.pdf"))).contentType(startsWith("application/pdf")).filename(is("04-Java-OOP-Basics.pdf")).disposition(is(ATTACHMENT_DISPOSITION));
 * </pre>
 * 
 * For sms, you can write something like this:
 * 
 * <pre>
 * assertThat(smppServer).receivedMessages().count(is(1)).message(0).content(is("sms content")).from().number(is(INTERNATIONAL_PHONE_NUMBER)).typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
 * 		.numberPlanIndicator(is(NumberingPlanIndicator.ISDN)).and().to().number(is(NATIONAL_PHONE_NUMBER)).typeOfNumber(is(TypeOfNumber.UNKNOWN))
 * 		.numberPlanIndicator(is(NumberingPlanIndicator.ISDN));
 * </pre>
 * 
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class OghamAssertions {

	// @formatter:off
	/**
	 * Helper method to write assertions on mails using fluent API.
	 * For example:
	 * <pre>
	 * assertThat(greenMail).receivedMessages()
	 *   .count(is(1))
	 *   .message(0)
	 *     .subject(is("Test"))
	 *     .from().address(hasItems("test.sender@sii.fr")).and()
	 *     .to().address(hasItems("recipient@sii.fr")).and()
	 *   .body()
	 *      .contentAsString(is("body"))
	 *      .contentType(startsWith("text/plain")).and()
	 *   .alternative(nullValue())
	 *   .attachments(hasSize(1))
	 *   .attachment("04-Java-OOP-Basics.pdf")
	 *      .content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
	 *      .contentType(startsWith("application/pdf"))
	 *      .filename(is("04-Java-OOP-Basics.pdf"))
	 *      .disposition(is(ATTACHMENT_DISPOSITION));
	 * </pre>
	 * 
	 * @param greenMail
	 *            email server that stores received messages
	 * @return builder for fluent assertions on received messages
	 */
	// @formatter:on
	public static ReceivedEmailsAssert assertThat(GreenMailRule greenMail) {
		return new ReceivedEmailsAssert(Arrays.asList(greenMail.getReceivedMessages()));
	}

	// @formatter:off
	/**
	 * Helper method to write assertions on mails using fluent API.
	 * For example:
	 * <pre>
	 * assertThat(greenMail.getReceivedMessages())
	 *   .count(is(1))
	 *   .message(0)
	 *     .subject(is("Test"))
	 *     .from().address(hasItems("test.sender@sii.fr")).and()
	 *     .to().address(hasItems("recipient@sii.fr")).and()
	 *   .body()
	 *      .contentAsString(is("body"))
	 *      .contentType(startsWith("text/plain")).and()
	 *   .alternative(nullValue())
	 *   .attachments(hasSize(1))
	 *   .attachment("04-Java-OOP-Basics.pdf")
	 *      .content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
	 *      .contentType(startsWith("application/pdf"))
	 *      .filename(is("04-Java-OOP-Basics.pdf"))
	 *      .disposition(is(ATTACHMENT_DISPOSITION));
	 * </pre>
	 * 
	 * @param receivedEmails
	 *            list of messages received by the email server
	 * @return builder for fluent assertions on received messages
	 */
	// @formatter:on
	public static EmailsAssert<Void> assertThat(MimeMessage[] receivedEmails) {
		return new EmailsAssert<>(Arrays.asList(receivedEmails), null);
	}

	// @formatter:off
	/**
	 * Helper method to write assertions on sms using fluent API.
	 * For example:
	 * <pre>
	 * assertThat(smppServer).receivedMessages()
	 *   .count(is(1))
	 *   .message(0)
	 *     .content(is("sms content"))
	 *     .from()
	 *       .number(is(INTERNATIONAL_PHONE_NUMBER))
	 *       .typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
	 *       .numberPlanIndicator(is(NumberingPlanIndicator.ISDN)).and()
	 *     .to()
	 *       .number(is(NATIONAL_PHONE_NUMBER))
	 *       .typeOfNumber(is(TypeOfNumber.UNKNOWN))
	 *       .numberPlanIndicator(is(NumberingPlanIndicator.ISDN));
	 * </pre>
	 * 
	 * @param smsServer
	 *            SMS server that stores received messages
	 * @return builder for fluent assertions on received messages
	 */
	// @formatter:on
	public static ReceivedSmsAssert assertThat(SmppServerRule<SubmitSm> smsServer) {
		return new ReceivedSmsAssert(smsServer.getReceivedMessages());
	}

	// @formatter:off
	/**
	 * Helper method to write assertions on sms using fluent API.
	 * For example:
	 * <pre>
	 * assertThat(smppServer.getReceivedMessages())
	 *   .count(is(1))
	 *   .message(0)
	 *     .content(is("sms content"))
	 *     .from()
	 *       .number(is(INTERNATIONAL_PHONE_NUMBER))
	 *       .typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
	 *       .numberPlanIndicator(is(NumberingPlanIndicator.ISDN)).and()
	 *     .to()
	 *       .number(is(NATIONAL_PHONE_NUMBER))
	 *       .typeOfNumber(is(TypeOfNumber.UNKNOWN))
	 *       .numberPlanIndicator(is(NumberingPlanIndicator.ISDN));
	 * </pre>
	 * 
	 * @param receivedSms
	 *            The list of messages received by the SMS server
	 * @return builder for fluent assertions on received messages
	 */
	// @formatter:on
	public static SmsListAssert<Void> assertThat(List<SubmitSm> receivedSms) {
		return new SmsListAssert<>(receivedSms, null);
	}

	/**
	 * Ogham helper for keeping context information when using fluent
	 * assertions.
	 * 
	 * @param reasonTemplate
	 *            the template for the reason
	 * @param context
	 *            the evaluation context
	 * @param delegate
	 *            the matcher to decorate
	 * @param <T>
	 *            the type used for the matcher
	 * @return the matcher
	 */
	public static <T> Matcher<T> usingContext(String reasonTemplate, Context context, Matcher<T> delegate) {
		return new CustomReason<>(context.evaluate(reasonTemplate), delegate);
	}

	/**
	 * Check if the HTML is similar to the expected. The HTML strings are parsed
	 * into {@link Document}s. Two documents are considered to be "similar" if
	 * they contain the same elements and attributes regardless of order.
	 * 
	 * @param expectedHtml
	 *            the expected HTML
	 * @return the matcher that will check if HTML is identical to expected HTML
	 */
	@Factory
	public static Matcher<String> isSimilarHtml(String expectedHtml) {
		return new SimilarHtmlMatcher(expectedHtml);
	}

	/**
	 * Check if the HTML is identical to the expected. The HTML strings are
	 * parsed into {@link Document}s. Two documents are considered to be
	 * "identical" if they contain the same elements and attributes in the same
	 * order.
	 * 
	 * @param expectedHtml
	 *            the expected HTML
	 * @return the matcher that will check if HTML is identical to expected HTML
	 */
	@Factory
	public static Matcher<String> isIdenticalHtml(String expectedHtml) {
		return new IdenticalHtmlMatcher(expectedHtml);
	}

	/**
	 * Utility method that loads a file content from the classpath. UTF-8
	 * charset is used.
	 * 
	 * @param path
	 *            the path to the classpath resource
	 * @return the content of the file
	 * @throws IOException
	 *             when resource can't be read or doesn't exist
	 */
	public static String resourceAsString(String path) throws IOException {
		return resourceAsString(path, Charsets.UTF_8);
	}

	/**
	 * Utility method that loads a file content from the classpath.
	 * 
	 * @param path
	 *            the path to the classpath resource
	 * @param charset
	 *            the charset used for reading the file
	 * @return the content of the file
	 * @throws IOException
	 *             when resource can't be read or doesn't exist
	 */
	public static String resourceAsString(String path, Charset charset) throws IOException {
		return IOUtils.toString(resource(path), charset.name());
	}

	/**
	 * Utility method that loads a file content from the classpath.
	 * 
	 * @param path
	 *            the path to the classpath resource
	 * @return the content of the file as byte array
	 * @throws IOException
	 *             when resource can't be read or doesn't exist
	 */
	public static byte[] resource(String path) throws IOException {
		InputStream resource = OghamAssertions.class.getClassLoader().getResourceAsStream(path.startsWith("/") ? path.substring(1) : path);
		if (resource == null) {
			throw new FileNotFoundException("No resource found for path " + path);
		}
		return IOUtils.toByteArray(resource);
	}

	private OghamAssertions() {
		super();
	}

}
