package fr.sii.ogham.testing.assertion;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.jsmpp.bean.SubmitSm;

import com.icegreen.greenmail.junit.GreenMailRule;

import fr.sii.ogham.testing.assertion.email.EmailsAssert;
import fr.sii.ogham.testing.assertion.email.ReceivedEmailsAssert;
import fr.sii.ogham.testing.assertion.sms.ReceivedSmsAssert;
import fr.sii.ogham.testing.assertion.sms.SmsListAssert;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;
import fr.sii.ogham.testing.sms.simulator.jsmpp.SubmitSmAdapter;

/**
 * Utility class that helps writing message assertions. For emails, you can
 * write something like this:
 * 
 * <pre>
 * {@code
 * assertThat(greenMail)
 *   .receivedMessages()
 *     .count(is(1))
 *       .message(0)
 *         .subject(is("Test"))
 *         .from()
 *           .address(hasItems("test.sender@sii.fr"))
 *           .and()
 *         .to()
 *           .address(hasItems("recipient@sii.fr"))
 *         .and()
 *       .body()
 * 		   .contentAsString(is("body"))
 *         .contentType(startsWith("text/plain"))
 *         .and()
 *       .alternative(nullValue())
 *       .attachments(hasSize(1))
 *         .attachment("04-Java-OOP-Basics.pdf")
 * 		     .content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
 *           .contentType(startsWith("application/pdf"))
 *           .filename(is("04-Java-OOP-Basics.pdf"))
 *           .disposition(is(ATTACHMENT_DISPOSITION));
 * }
 * </pre>
 * 
 * For sms, you can write something like this:
 * 
 * <pre>
 * {@code
 * assertThat(smppServer)
 *   .receivedMessages()
 *     .count(is(1))
 *       .message(0)
 *         .content(is("sms content"))
 *         .from()
 *           .number(is(INTERNATIONAL_PHONE_NUMBER))
 *           .typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
 * 		     .numberPlanIndicator(is(NumberingPlanIndicator.ISDN))
 *           .and()
 *         .to()
 *           .number(is(NATIONAL_PHONE_NUMBER))
 *           .typeOfNumber(is(TypeOfNumber.UNKNOWN))
 * 		     .numberPlanIndicator(is(NumberingPlanIndicator.ISDN));
 * }
 * </pre>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public final class OghamAssertions {

	/**
	 * Helper method to write assertions on mails using fluent API. For example:
	 * 
	 * <pre>
	 * {@code
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
	 * }
	 * </pre>
	 * 
	 * @param greenMail
	 *            email server that stores received messages
	 * @return builder for fluent assertions on received messages
	 */
	public static ReceivedEmailsAssert assertThat(GreenMailRule greenMail) {
		return new ReceivedEmailsAssert(Arrays.asList(greenMail.getReceivedMessages()));
	}

	/**
	 * Helper method to write assertions on mails using fluent API. For example:
	 * 
	 * <pre>
	 * {@code
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
	 * }
	 * </pre>
	 * 
	 * @param receivedEmails
	 *            list of messages received by the email server
	 * @return builder for fluent assertions on received messages
	 */
	public static EmailsAssert<Void> assertThat(MimeMessage[] receivedEmails) {
		return new EmailsAssert<>(Arrays.asList(receivedEmails), null);
	}

	/**
	 * Helper method to write assertions on sms using fluent API. For example:
	 * 
	 * <pre>
	 * {@code
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
	 * }
	 * </pre>
	 * 
	 * @param smsServer
	 *            SMS server that stores received messages
	 * @return builder for fluent assertions on received messages
	 */
	public static ReceivedSmsAssert<SubmitSmAdapter> assertThat(SmppServerRule<SubmitSm> smsServer) {
		return new ReceivedSmsAssert<>(smsServer.getReceivedMessages().stream().map(SubmitSmAdapter::new).collect(toList()));
	}

	/**
	 * Helper method to write assertions on sms using fluent API. For example:
	 * 
	 * <pre>
	 * {@code
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
	 * }
	 * </pre>
	 * 
	 * @param receivedSms
	 *            The list of messages received by the SMS server
	 * @return builder for fluent assertions on received messages
	 */
	public static SmsListAssert<Void, SubmitSmAdapter> assertThat(List<SubmitSm> receivedSms) {
		return new SmsListAssert<>(receivedSms.stream().map(SubmitSmAdapter::new).collect(toList()), null);
	}

	private OghamAssertions() {
		super();
	}

}
